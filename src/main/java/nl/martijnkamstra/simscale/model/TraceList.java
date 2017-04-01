package nl.martijnkamstra.simscale.model;

import nl.martijnkamstra.simscale.TraceBuilder;
import nl.martijnkamstra.simscale.statistics.StatsCollector;
import nl.martijnkamstra.simscale.writer.JsonWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Martijn Kamstra on 31/03/2017.
 */
public class TraceList {
    private static final Logger logger = LogManager.getLogger(TraceList.class);

    /**
     * A map suited for concurrency. It contains all the traces that have not been finished yet. Once finished they will
     * be printed and removed from this list
     */
    private ConcurrentHashMap<String, Trace> currentTraceList = new ConcurrentHashMap<>();

    private LocalDateTime lastReceivedEventTime = LocalDateTime.MIN;

    /**
     * Add a trace element to the trace list. It will check if the trace id already exists. If not add it to the
     * concurrent hashmap containing all active traces. Else update it.
     * @param traceId The trace id the trace element is related to
     * @param traceElement The trace element to be added
     * @return true if the trace element was succesfully added.
     */
    public boolean addTraceElement(String traceId, TraceElement traceElement) {
        synchronized (lastReceivedEventTime) {
            if (traceElement.getEndTimestamp().isAfter(lastReceivedEventTime))
                lastReceivedEventTime = traceElement.getEndTimestamp();
        }

        boolean returnValue = false;

        if (!currentTraceList.containsKey(traceId)) {
            // A new trace
            Trace trace = new Trace(traceId);
            boolean added = trace.addTraceElement(traceElement);
            boolean stored = storeTraceInMap(traceId, trace);
            returnValue = added && stored;
        } else {
            // Trace exists already, so add the trace element to it
            Trace existingTrace = currentTraceList.get(traceId);
            boolean added = existingTrace.addTraceElement(traceElement);
            boolean stored = storeTraceInMap(traceId, existingTrace);
            returnValue = added && stored;
        }

        /**
         * Check if any of the traces are complete (which is defined by not having received any events for a specified
         * number of seconds) so they can be printed. Note that when no more events are received this is not invoked
         * either but this is considered correct behaviour as none of those traces are already considered complete (or
         * they would have been removed and printed before already)
         */
        int traceFinishedTimeoutSec = TraceBuilder.getConfiguration().getTraceFinishedTimeoutSec();
        currentTraceList.forEach((trace_id, trace) -> {
            long secondsSinceLastUpdate = ChronoUnit.SECONDS.between(trace.getLastTimeUpdated(), lastReceivedEventTime);
            if (secondsSinceLastUpdate > traceFinishedTimeoutSec) {
                // Traces are old enough to be considered completed
                Trace removedTrace = currentTraceList.remove(trace_id);
                if (removedTrace.getState() >= 2) {
                    // Has at least a head
                    JsonWriter.printTraceAsJson(removedTrace);
                    StatsCollector.addNumberOfGeneratedCompleteTraces(1);
                }
                else {
                    // Orphan trace
                    logger.error("The following trace has no root: " + trace);
                    StatsCollector.addNumberOfOrphanRequests(1);
                }
            }
        });

        return returnValue;
    }

    /**
     * @param traceId The id of the trace which is the key of the map
     * @param trace The new or updated trace to be stored in the map
     * @return true if the map was successfully updated
     */
    private boolean storeTraceInMap(String traceId, Trace trace) {
        try {
            currentTraceList.put(traceId, trace);
            return true;
        } catch (NullPointerException ex) {
            logger.error("Something went wrong trying to store a trace " + traceId + " in the map meant for storing those: ", ex);
            return false;
        }
    }

    private void printTraceList() {
        System.out.println("=============================================================================");
        for (Map.Entry<String, Trace> trace : currentTraceList.entrySet()) {
            System.out.println("Trace id: " + trace.getKey() + " : " + trace.getValue());
        }
    }
}
