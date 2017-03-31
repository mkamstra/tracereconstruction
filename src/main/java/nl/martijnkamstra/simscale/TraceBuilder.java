package nl.martijnkamstra.simscale;

import nl.martijnkamstra.simscale.parser.LogParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by Martijn Kamstra on 30/03/2017.
 */
public class TraceBuilder {
    private static final Logger logger = LogManager.getLogger(TraceBuilder.class);

    public static void main(String[] args) {
        logger.debug("Starting to parse log file");
        try {
            LogParser logParser = new LogParser();
            logParser.parseLog("log.txt");
        } catch (IOException ex) {
            logger.error("Error parsing log file: ", ex);
        }
        logger.debug("Finished parsing log file");
    }
}
