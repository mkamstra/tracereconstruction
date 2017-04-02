package nl.martijnkamstra.simscale.statistics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Martijn Kamstra on 01/04/2017.
 *
 * A class intended for collecting stats
 */
public class StatsCollector {

    @SuppressWarnings("all")
    private static AtomicLong startProcessingTime = new AtomicLong(0);
    @SuppressWarnings("all")
    private static AtomicLong numberOfLinesProcessed = new AtomicLong(0);
    @SuppressWarnings("all")
    private static AtomicLong numberOfIllegalLines = new AtomicLong(0);
    @SuppressWarnings("all")
    private static AtomicLong numberOfGeneratedCompleteTraces = new AtomicLong(0);
    @SuppressWarnings("all")
    private static AtomicLong numberOfOrphanRequests = new AtomicLong(0);
    @SuppressWarnings("all")
    private static AtomicLong numberOfTraceElementsInCompleteTraces = new AtomicLong(0);

    public static void setStartProcessingTime(long pStartProcessingTime) {
        startProcessingTime.set(pStartProcessingTime);
    }

    @SuppressWarnings("all")
    public static void addNumberOfLinesProcessed(long pNumberOfLinesProcessed) {
        numberOfLinesProcessed.set(numberOfLinesProcessed.get() + pNumberOfLinesProcessed);
    }

    @SuppressWarnings("all")
    public static void addNumberOfIllegalLines(long pNumberOfIllegalLines) {
        numberOfIllegalLines.set(numberOfIllegalLines.get() + pNumberOfIllegalLines);
    }

    @SuppressWarnings("all")
    public static void addNumberOfGeneratedCompleteTraces(long pNumberOfGeneratedCompleteTraces) {
        numberOfGeneratedCompleteTraces.set(numberOfGeneratedCompleteTraces.get() + pNumberOfGeneratedCompleteTraces);
    }

    @SuppressWarnings("all")
    public static void addNumberOfOrphanRequests(long pNumberOfOrphanRequests) {
        numberOfOrphanRequests.set(numberOfOrphanRequests.get() + pNumberOfOrphanRequests);
    }

    public static void addNumberOfTraceElementsInCompleteTraces(long pNumberOfTraceElementsInCompleteTraces) {
        numberOfTraceElementsInCompleteTraces.set(numberOfTraceElementsInCompleteTraces.get() + pNumberOfTraceElementsInCompleteTraces);
    }

    public static long getStartProcessingTime() {
        return startProcessingTime.get();
    }

    public static long getNumberOfLinesProcessed() {
        return numberOfLinesProcessed.get();
    }

    public static long getNumberOfIllegalLines() {
        return numberOfIllegalLines.get();
    }

    public static long getNumberOfGeneratedCompleteTraces() {
        return numberOfGeneratedCompleteTraces.get();
    }

    public static long getNumberOfOrphanRequests() {
        return numberOfOrphanRequests.get();
    }

    public static long getNumberOfTraceElementsInCompleteTraces() {
        return numberOfTraceElementsInCompleteTraces.get();
    }
}
