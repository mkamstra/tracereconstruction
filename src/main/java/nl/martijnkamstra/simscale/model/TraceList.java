package nl.martijnkamstra.simscale.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    ConcurrentHashMap<String, Trace> currentTraceList = new ConcurrentHashMap<>();

    public boolean addTraceElement(String traceId, TraceElement traceElement) {
        if (!currentTraceList.containsKey(traceId)) {
            // A new trace
            Trace trace = new Trace(traceId);
            boolean added = trace.addTraceElement(traceElement);
            // TODO: Remember that the trace needs to start with null!!!!
            //TODO Remove following print statement
            boolean stored = storeTraceInMap(traceId, trace);
            printTraceList();
            return added && stored;
        } else {
            // Trace exists already, so add the trace element to it
            Trace existingTrace = currentTraceList.get(traceId);
            boolean added = existingTrace.addTraceElement(traceElement);
            boolean stored = storeTraceInMap(traceId, existingTrace);
            printTraceList();
            return added && stored;
        }
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
