package nl.martijnkamstra.simscale.parser;

import nl.martijnkamstra.simscale.model.TraceElement;
import nl.martijnkamstra.simscale.model.TraceList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Created by Martijn Kamstra on 30/03/2017.
 */
public class LogParser {

    private static final Logger logger = LogManager.getLogger(LogParser.class);

    TraceList traceList = new TraceList();

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Parse a log file with name pFileName. Note that the file could be VERY large and should therefore not entirely
     * be loaded into memory. Instead the Java 8 Streams API is used to read (and process) the file line by line
     * @param pFileName The name of the log file to be parsed
     */
    public void parseLog(String pFileName) throws IOException {
        ClassLoader classLoader = LogParser.class.getClassLoader();
        InputStream fileStream = classLoader.getResourceAsStream(pFileName);
        printMemoryUsage();
        try (Scanner scanner = new Scanner(fileStream)) {
            while (scanner.hasNext()) {
                parseLine(scanner.nextLine());
                //logger.debug(scanner.nextLine());
                printMemoryUsage();
            }
            // note that Scanner suppresses exceptions
            if (scanner.ioException() != null) {
                logger.error("Scanner exception: ", scanner.ioException());
            }
        } finally {
            if (fileStream != null) {
                fileStream.close();
            }
        }
        printMemoryUsage();
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
                return;
            }
            String receivedSpanId = spanIds[0];
            String sentSpanId = spanIds[1];
            TraceElement traceElement = new TraceElement(startTime, endTime, serviceName, receivedSpanId, sentSpanId);
            boolean added = traceList.addTraceElement(traceId, traceElement);
            if (!added) {
                logger.error("Problem adding trace element to trace list for line " + line + " , line ignored");
                return;
            }
        } catch (DateTimeParseException ex) {
            logger.error("Datetime of line " + line + " could not be parsed, line ignored: ", ex);
        }
    }


    private static void printMemoryUsage() {
        long total = Runtime.getRuntime().totalMemory() / 1000000;
        long used  = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000;
        System.out.println("Total memory (MB): " + total + ", used memory (MB): " + used);
    }
}
