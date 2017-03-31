package nl.martijnkamstra.simscale.model;

import java.time.LocalDateTime;

/**
 * Created by Martijn Kamstra on 31/03/2017.
 */
public class TraceElement {
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private String serviceName;
    private String receivedSpanId;
    private String sentSpanId;

    public TraceElement(LocalDateTime startTimestamp, LocalDateTime endTimestamp, String serviceName, String receivedSpanId, String sentSpanId) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.serviceName = serviceName;
        this.receivedSpanId = receivedSpanId;
        this.sentSpanId = sentSpanId;
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
        return "TraceElement{" +
                "startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                ", serviceName='" + serviceName + '\'' +
                ", receivedSpanId='" + receivedSpanId + '\'' +
                ", sentSpanId='" + sentSpanId + '\'' +
                '}';
    }
}
