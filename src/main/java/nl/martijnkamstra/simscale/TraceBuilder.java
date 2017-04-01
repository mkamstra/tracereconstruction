package nl.martijnkamstra.simscale;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.martijnkamstra.simscale.configuration.ConfigReader;
import nl.martijnkamstra.simscale.configuration.Configuration;
import nl.martijnkamstra.simscale.parser.LogParser;
import nl.martijnkamstra.simscale.statistics.StatsReporter;
import nl.martijnkamstra.simscale.writer.JsonWriter;
import org.apache.commons.cli.*;
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
        CommandLineParser commandLineParser = new DefaultParser();
        Options options = new Options();
        options.addOption("c", "configuration file", true, "JSON configuration file path (full or relative to the directory you are running from).");
        options.addOption("h", "help", false, "Display usage instructions");
        options.addOption("i", "input", true, "Input file path (full or relative to directory you are running from");
        options.addOption("o", "output", true, "Output file path (full or relative to directory you are running from");
        String configFileName = null;
        String inputFileName = null;
        String outputFileName = null;
        try {
            CommandLine line = commandLineParser.parse(options, args);
            if (line.hasOption("c")) {
                configFileName = line.getOptionValue("c");
            }
            if (line.hasOption("i")) {
                inputFileName = line.getOptionValue("i");
            }

            if (line.hasOption("o")) {
                outputFileName = line.getOptionValue("o");
            }

            if (line.hasOption("h")) {
                printHelp(options);
            }
        } catch (ParseException ex) {
            logger.fatal("Error parsing the arguments you provided: ", ex);
            printHelp(options);
            return;
        }

        logger.debug("Reading json config file first");
        configuration = ConfigReader.readConfig(configFileName);

        logger.debug("Starting to parse log file");
        JsonWriter.setOutputFileName(outputFileName);
        ExecutorService service = Executors.newFixedThreadPool(1);
        LogParser logParser = new LogParser(inputFileName);
        Future<String> resultFuture = service.submit(logParser);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        StatsReporter statsReporter = new StatsReporter();
        ScheduledFuture statsFuture = scheduledExecutorService.scheduleAtFixedRate(statsReporter, 10, 10, TimeUnit.SECONDS);
        String result = "";
        try {
            result = resultFuture.get();
            service.shutdown();
            service.awaitTermination(10, TimeUnit.SECONDS);
            scheduledExecutorService.shutdown();
            scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            logger.error("Log parser execution interrrupted: ", ex);
        } catch (ExecutionException ex) {
            logger.error("Error getting result from log parser: ", ex);
        } finally {
            resultFuture.cancel(true);
            service.shutdownNow();
            System.out.println("Return value from log parser: " + result);
            statsFuture.cancel(true);
            scheduledExecutorService.shutdownNow();
        }
        logger.debug("Finished parsing log file");
    }

    private static void printHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Trace Reconstruction", "Use a log file to reconstruct a trace file. A JSON file can be used to configure some of the parameters of the application.", options, "", true);
    }
}
