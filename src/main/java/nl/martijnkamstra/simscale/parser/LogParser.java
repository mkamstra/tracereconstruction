package nl.martijnkamstra.simscale.parser;

import nl.martijnkamstra.simscale.model.TraceElement;
import nl.martijnkamstra.simscale.model.TraceList;
import nl.martijnkamstra.simscale.statistics.StatsCollector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.Callable;

/**
 * Created by Martijn Kamstra on 30/03/2017.
 */
public class LogParser implements Callable<String> {

    private static final Logger logger = LogManager.getLogger(LogParser.class);

    private TraceList traceList = new TraceList();

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    // The name of the log file to parse including its path
    private String fileName;

    /**
     * @param fileName The name of the file that needs to be parsed
     */
    public LogParser(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Parse a log line which typically looks like this:
     * 2013-10-23T10:13:05.076Z 2013-10-23T10:13:05.078Z ouvyulux service1 lydof6z2->3shf5olz
     * @param line The string to be parsed
     */
    private void parseLine(String line) {
        String[] lineElements = line.split(" "); // Space is separation character in line
        if (lineElements.length < 5) {
            logger.error("Line \"" + line + "\" not correct as it cannot be splitted into 5 elements using the space character, line ignored");
            // TODO: Possibly store the line somewhere
            StatsCollector.addNumberOfIllegalLines(1);
            return;
        }
        try {
            LocalDateTime startTime = LocalDateTime.parse(lineElements[0], dateTimeFormatter);
            LocalDateTime endTime = LocalDateTime.parse(lineElements[1], dateTimeFormatter);
            String traceId = lineElements[2];
            String serviceName = lineElements[3];
            String[] spanIds = lineElements[4].split("->");
            if (spanIds.length != 2) {
                logger.error("Line \"" + line + "\" not correct as the span ids cannot be splitted into 2 elements using the -> character combination, line ignored");
                StatsCollector.addNumberOfIllegalLines(1);
                return;
            }
            String receivedSpanId = spanIds[0];
            String sentSpanId = spanIds[1];
            TraceElement traceElement = new TraceElement(startTime, endTime, serviceName, receivedSpanId, sentSpanId);
            boolean added = traceList.addTraceElement(traceId, traceElement);
            if (!added) {
                logger.error("Problem adding trace element to trace list for line " + line + " , line ignored");
                return;
            } else {
                StatsCollector.addNumberOfLinesProcessed(1);
            }
        } catch (DateTimeParseException ex) {
            logger.error("Datetime of line " + line + " could not be parsed, line ignored: ", ex);
            StatsCollector.addNumberOfIllegalLines(1);
        }
    }


    private static void printMemoryUsage() {
        long total = Runtime.getRuntime().totalMemory() / 1000000;
        long used  = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000;
        logger.info("Total memory (MB): " + total + ", used memory (MB): " + used);
    }

    /**
     * Parse a log file with name fileName. Note that the file could be VERY large and should therefore not entirely
     * be loaded into memory. Instead the Java 8 Streams API is used to read (and process) the file line by line
     * @return A string containing the number of lines read
     * @throws Exception
     */
    @Override
    public String call() throws Exception {
        printMemoryUsage();
        int counter = 0;
        if (fileName == null || fileName.length() == 0) {
            logger.fatal("Input file name not specified");
            throw new Exception("Input file name not specified");
        }
        StatsCollector.setStartProcessingTime(System.nanoTime());
        // BufferedReader is synchronized and has larger buffer than Scanner
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while (!Thread.currentThread().isInterrupted() && true) {
                line = reader.readLine();
                if (line == null) {
                    // No input at the moment
                    Thread.sleep(1000);
                } else {
                    parseLine(line);
                    //logger.debug(scanner.nextLine());
                    if (counter % 100 == 0)
                        printMemoryUsage();
                }
            }

        } catch (IOException ex) {
            logger.error("Problem reading from log file: ", ex);
        } catch (InterruptedException ex) {
            logger.error("Problem waiting before checking for new contents: ", ex);
            Thread.currentThread().interrupt();
        }
        printMemoryUsage();
        return new String("Number of lines parsed: " + counter);
    }
}
