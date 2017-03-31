package nl.martijnkamstra.simscale.model;

import java.util.LinkedList;

/**
 * Created by Martijn Kamstra on 31/03/2017.
 */
public class Trace {
    private String traceId = null;
    private int state = 0; // 0 means empty trace; 1 means in progress; 2 means finished
    private LinkedList<TraceElement> traceElements = new LinkedList<>();

    // TODO: SEt state when finished

    public Trace(String traceId) {
        this.traceId = traceId;
        state = 0;
    }

    public boolean addTraceElement(TraceElement traceElement) {
        state = 1;
        return traceElements.add(traceElement); // TODO: Do this by checking span ids
    }

    @Override
    public String toString() {
        String traceElementsString = "";
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
