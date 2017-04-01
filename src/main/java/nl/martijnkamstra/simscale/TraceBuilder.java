package nl.martijnkamstra.simscale;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.martijnkamstra.simscale.configuration.ConfigReader;
import nl.martijnkamstra.simscale.configuration.Configuration;
import nl.martijnkamstra.simscale.parser.LogParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by Martijn Kamstra on 30/03/2017.
 */
public class TraceBuilder {
    private static final Logger logger = LogManager.getLogger(TraceBuilder.class);

    private static Configuration configuration = null;

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static void main(String[] args) {
        logger.debug("Reading json config file first");
        configuration = ConfigReader.readConfig("config.json");

        logger.debug("Starting to parse log file");
        ExecutorService service = Executors.newFixedThreadPool(1);
        LogParser logParser = new LogParser("log.txt");
        Future<String> resultFuture = service.submit(logParser);
        String result = "";
        try {
            result = resultFuture.get();
            service.shutdown();
            service.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            logger.error("Log parser execution interrrupted: ", ex);
        } catch (ExecutionException ex) {
            logger.error("Error getting result from log parser: ", ex);
        } finally {
            resultFuture.cancel(true);
            service.shutdownNow();
            System.out.println("Return value from log parser: " + result);
        }
        logger.debug("Finished parsing log file");
    }
}
