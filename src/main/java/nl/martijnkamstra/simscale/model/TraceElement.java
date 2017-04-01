package nl.martijnkamstra.simscale.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Martijn Kamstra on 31/03/2017.
 */
public class TraceElement {
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private String serviceName;
    private String receivedSpanId;
    private String sentSpanId;
    private List<TraceElement> childrenTraceElements = Collections.synchronizedList(new ArrayList<>()); // The traceElement(s) that have been called from this traceElement

    public TraceElement(LocalDateTime startTimestamp, LocalDateTime endTimestamp, String serviceName, String receivedSpanId, String sentSpanId) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.serviceName = serviceName;
        this.receivedSpanId = receivedSpanId;
        this.sentSpanId = sentSpanId;
    }

    /**
     * Add a child traceElement to this traceElement, as it has been called from this
     * @param traceElement The child traceElement to add
     * @return whether the child was added
     */
    public boolean addChild(TraceElement traceElement) {
        synchronized (childrenTraceElements) {
            if (traceElement.getReceivedSpanId().equalsIgnoreCase(sentSpanId)) {
                return childrenTraceElements.add(traceElement);
            } else {
                // check if it can be added to any of the children
                for (TraceElement te : childrenTraceElements) {
                    if (te.addChild(traceElement))
                        return true; // traceElement was successfully added to a child element
                }
            }
            return false;
        }
    }

    public LocalDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public LocalDateTime getEndTimestamp() {
        return endTimestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getReceivedSpanId() {
        return receivedSpanId;
    }

    public String getSentSpanId() {
        return sentSpanId;
    }

    @Override
    public String toString() {
        String childrenTraceElementsString = "";
        synchronized (childrenTraceElementsString) {
            for (TraceElement child : childrenTraceElements) {
                childrenTraceElementsString += child.toString() + ", ";
            }
            return "TraceElement{" +
                    "startTimestamp=" + startTimestamp +
                    ", endTimestamp=" + endTimestamp +
                    ", serviceName='" + serviceName + '\'' +
                    ", receivedSpanId='" + receivedSpanId + '\'' +
                    ", sentSpanId='" + sentSpanId + '\'' +
                    ", childrenTraceElements=[" + childrenTraceElements.size() + "]: " + childrenTraceElementsString +
                    '}';
        }
    }
}
