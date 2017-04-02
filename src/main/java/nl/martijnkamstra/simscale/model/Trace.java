package nl.martijnkamstra.simscale.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Martijn Kamstra on 31/03/2017.
 */
@JsonIgnoreProperties({"state", "lastTimeUpdated"})
public class Trace {
    private static final Logger logger = LogManager.getLogger(Trace.class);

    @JsonProperty("id")
    private String traceId = null;
    private AtomicInteger state = new AtomicInteger(0); // 0: empty trace; 1: in progress, no head yet; 2: one head, 3: multiple heads
    @JsonProperty("root")
    private List<TraceElement> traceElements = Collections.synchronizedList(new ArrayList<>());
    private LocalDateTime lastTimeUpdated; // The last time the trace was updated based on trace event end times (needed for checking if trace finished using a timeout)

    public Trace(String traceId) {
        this.traceId = traceId;
        state.set(0);
        lastTimeUpdated = LocalDateTime.MIN;
    }

    public LocalDateTime getLastTimeUpdated() {
        synchronized (lastTimeUpdated) {
            return lastTimeUpdated;
        }
    }

    public int getState() {
        return state.get();
    }

    public boolean addTraceElement(TraceElement traceElement) {
        synchronized (lastTimeUpdated) {
            if (traceElement.getEndTimestamp().isAfter(lastTimeUpdated))
                lastTimeUpdated = traceElement.getEndTimestamp();
        }

        if (traceElement.getReceivedSpanId().equalsIgnoreCase("null")) {
            // A head was found (potentially there are multiple heads)
            if (state.get() == 2)
                state.set(3); // Multiple heads
            else
                state.set(2); // First head

            traceElements.add(0, traceElement);
            // Move the existing traceElements down if they are not heads. Note that an iterator is used as a for
            // each loop doesn't allow for safe removal of items
            synchronized (traceElements) {
                Iterator<TraceElement> traceElementIterator = traceElements.iterator();
                while (traceElementIterator.hasNext()) {
                    TraceElement te = traceElementIterator.next();
                    // Only compare with the received traceElement as the assumption is that the adding of another head was
                    // already done properly
                    if (te.getReceivedSpanId().equalsIgnoreCase(traceElement.getSentSpanId())) {
                        // The te was called from the head, so can be moved down
                        boolean addedAsChild = traceElement.addChild(te);
                        traceElementIterator.remove();
                        if (!addedAsChild)
                            logger.error("Moving down child " + te + " in the trace failed");
                    }
                }
            }
            return true;
        }

        if (state.get() == 0)
            state.set(1);

        // Check if any of the existing traceElements are a child of this traceElement and move them there
        synchronized (traceElements) {
            Iterator<TraceElement> traceElementIterator = traceElements.iterator();
            while (traceElementIterator.hasNext()) {
                TraceElement te = traceElementIterator.next();
                if (te.getReceivedSpanId().equalsIgnoreCase(traceElement.getSentSpanId())) {
                    // The te was called from the head, so can be moved down
                    boolean addedAsChild = traceElement.addChild(te);
                    traceElementIterator.remove();
                    if (!addedAsChild)
                        logger.error("Moving down child " + te + " in the trace failed");
                }
            }

            // Find the right location of the traceElement in the trace
            for (TraceElement te : traceElements) {
                if (te.addChild(traceElement)) {
                    // Successfully added to an element in the trace
                    return true;
                }
            }
            // If it could not be added so add to list here for now even if it is not a head (it will be moved down later if
            // a head is received of course
            return traceElements.add(traceElement);
        }
    }

    @JsonIgnore
    public int getSize() {
        int size = 0;
        for (TraceElement te : traceElements) {
            size += te.getSize();
        }
        return size;
    }

    @Override
    public String toString() {
        String traceElementsString = "";
        synchronized (traceElements) {
            for (TraceElement traceElement : traceElements) {
                traceElementsString += traceElement + ", ";
            }
            return "Trace{" +
                    "traceId='" + traceId + '\'' +
                    ", state=" + state +
                    ", traceElements=[" + traceElements.size() + "]: " + traceElementsString +
                    '}';
        }
    }
}
