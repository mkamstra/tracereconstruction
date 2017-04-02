package nl.martijnkamstra.simscale.configuration;

/**
 * Created by Martijn Kamstra on 01/04/2017.
 */
public class Configuration {
    // Represents a timeout in seconds for a trace to be considered finished.
    private int traceFinishedTimeoutSec = 30;

    // A flag to indicate whether pretty print should be used or not
    private boolean jsonPrettyPrintOutput = false;

    public boolean isJsonPrettyPrintOutput() {
        return jsonPrettyPrintOutput;
    }

    @SuppressWarnings("unused")
    public void setJsonPrettyPrintOutput(boolean jsonPrettyPrintOutput) {
        this.jsonPrettyPrintOutput = jsonPrettyPrintOutput;
    }

    public int getTraceFinishedTimeoutSec() {
        return traceFinishedTimeoutSec;
    }

    @SuppressWarnings("unused")
    public void setTraceFinishedTimeoutSec(int traceFinishedTimeoutSec) {
        this.traceFinishedTimeoutSec = traceFinishedTimeoutSec;
    }
}
