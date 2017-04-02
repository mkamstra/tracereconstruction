package nl.martijnkamstra.simscale.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import nl.martijnkamstra.simscale.writer.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Martijn Kamstra on 31/03/2017.
 */
@JsonIgnoreProperties({"receivedSpanId"})
public class TraceElement {
    @JsonIgnore
    private final Object lock;

    @SuppressWarnings("all")
    @JsonProperty("start")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startTimestamp;
    @SuppressWarnings("all")
    @JsonProperty("end")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endTimestamp;
    @SuppressWarnings("all")
    @JsonProperty("service")
    private String serviceName;
    @SuppressWarnings("all")
    private String receivedSpanId;
    @SuppressWarnings("all")
    @JsonProperty("span")
    private String sentSpanId;
    @SuppressWarnings("all")
    @JsonProperty("calls")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TraceElement> childrenTraceElements = Collections.synchronizedList(new ArrayList<>()); // The traceElement(s) that have been called from this traceElement

    public TraceElement(LocalDateTime startTimestamp, LocalDateTime endTimestamp, String serviceName, String receivedSpanId, String sentSpanId) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.serviceName = serviceName;
        this.receivedSpanId = receivedSpanId;
        this.sentSpanId = sentSpanId;
        lock = new Object();
    }

    /**
     * Add a child traceElement to this traceElement, as it has been called from this
     *
     * @param traceElement The child traceElement to add
     * @return whether the child was added
     */
    public boolean addChild(TraceElement traceElement) {
        synchronized (lock) {
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

    @SuppressWarnings("unused")
    public LocalDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public LocalDateTime getEndTimestamp() {
        return endTimestamp;
    }

    @SuppressWarnings("unused")
    public String getServiceName() {
        return serviceName;
    }

    public String getReceivedSpanId() {
        return receivedSpanId;
    }

    public String getSentSpanId() {
        return sentSpanId;
    }

    @JsonIgnore
    public int getSize() {
        int size = 1;
        for (TraceElement te : childrenTraceElements) {
            size += te.getSize();
        }
        return size;
    }

    @Override
    public String toString() {
        StringBuilder childrenTraceElementsString = new StringBuilder("");
        synchronized (lock) {
            for (TraceElement child : childrenTraceElements) {
                childrenTraceElementsString.append(child.toString()).append(", ");
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
