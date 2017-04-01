package nl.martijnkamstra.simscale.statistics;

import nl.martijnkamstra.simscale.TraceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

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
        try {
            logger.info("Processing time so far [sec]: "+ processingTimeSec);
            logger.info("Number of generated complete traces: " + numberOfGeneratedCompleteTraces + ", rate [per sec]: " + rateOfGeneratedCompleteTraces);
            logger.info("Number of lines processed          : " + numberOfLinesProcessed + ", rate [per sec]: " + rateOfNumberOfLinesProcessed);
            logger.info("Number of illegal lines            : " + numberOfIllegalLines + ", rate [per sec]: " + rateOfNumberOfIllegalLines);
            logger.info("Number of orphan requests          : " + numberOfOrphanRequests + ", rate [per sec]: " + rateOfNumberOfOrphanRequests);
        } catch (Exception ex) {
            logger.error("Error writing some statistics: ", ex);
        }
    }
}