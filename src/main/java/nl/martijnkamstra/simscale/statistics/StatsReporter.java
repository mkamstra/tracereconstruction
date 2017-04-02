package nl.martijnkamstra.simscale.statistics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Martijn Kamstra on 01/04/2017.
 */
public class StatsReporter implements Runnable {
    private static final Logger logger = LogManager.getLogger(StatsReporter.class);

    @Override
    public void run() {
        long currentTimeNanos = System.nanoTime();
        double processingTimeSec = (currentTimeNanos - StatsCollector.getStartProcessingTime()) * 1E-9;
        if (processingTimeSec <= 0.0)
            return; // Nothing to report yet

        long numberOfGeneratedCompleteTraces = StatsCollector.getNumberOfGeneratedCompleteTraces();
        double rateOfGeneratedCompleteTraces = (double)numberOfGeneratedCompleteTraces / processingTimeSec;
        long numberOfLinesProcessed = StatsCollector.getNumberOfLinesProcessed();
        double rateOfNumberOfLinesProcessed = (double)numberOfLinesProcessed / processingTimeSec;
        long numberOfIllegalLines = StatsCollector.getNumberOfIllegalLines();
        double rateOfNumberOfIllegalLines = (double)numberOfIllegalLines / processingTimeSec;
        long numberOfOrphanRequests = StatsCollector.getNumberOfOrphanRequests();
        double rateOfNumberOfOrphanRequests = (double)numberOfOrphanRequests / processingTimeSec;
        long numberOfTraceElementsInCompleteTraces = StatsCollector.getNumberOfTraceElementsInCompleteTraces();
        double averageCompleteTraceSize = 0.0;
        if (numberOfGeneratedCompleteTraces > 0)
            averageCompleteTraceSize = (double)numberOfTraceElementsInCompleteTraces / (double)numberOfGeneratedCompleteTraces;
        try {
            logger.info("Processing time so far [sec]: "+ processingTimeSec);
            logger.info("Number of generated complete traces: " + numberOfGeneratedCompleteTraces + ", rate [per sec]: " + rateOfGeneratedCompleteTraces);
            logger.info("Number of lines processed          : " + numberOfLinesProcessed + ", rate [per sec]: " + rateOfNumberOfLinesProcessed);
            logger.info("Number of illegal lines            : " + numberOfIllegalLines + ", rate [per sec]: " + rateOfNumberOfIllegalLines);
            logger.info("Number of orphan requests          : " + numberOfOrphanRequests + ", rate [per sec]: " + rateOfNumberOfOrphanRequests);
            logger.info("Average complete trace size        : " + averageCompleteTraceSize);
        } catch (Exception ex) {
            logger.error("Error writing some statistics: ", ex);
        }
    }
}