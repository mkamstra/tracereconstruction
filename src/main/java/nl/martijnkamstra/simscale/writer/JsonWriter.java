package nl.martijnkamstra.simscale.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.martijnkamstra.simscale.TraceBuilder;
import nl.martijnkamstra.simscale.model.Trace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Created by Martijn Kamstra on 01/04/2017.
 */
public class JsonWriter {
    private static final Logger logger = LogManager.getLogger(JsonWriter.class);

    // The WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED property enables the writing of single element arrays as objects instead
    // of arrays. This is applicable to for example the root which has been configured in the code to potentially
    // consist of more than one element. But as this usually doesn't happen it avoids the array notation in the json
    // output
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);

    private static String outputFileName = null;

    public static void setOutputFileName(String pOutputFileName) {
        outputFileName = pOutputFileName;
    }

    public static void printTraceAsJson(Trace trace) {
        Writer outputStreamWriter = null;
        try {
            String jsonTrace;
            if (TraceBuilder.getConfiguration().isJsonPrettyPrintOutput()) {
                jsonTrace = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(trace);
            } else {
                jsonTrace = mapper.writeValueAsString(trace);
            }
            System.out.println(jsonTrace);
            if (outputFileName == null || outputFileName.length() == 0) {
                logger.fatal("Output file name not specified, cannot print trace to file");
            } else {
                outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outputFileName, true), "UTF-8"); // append
                try (BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
                	if (TraceBuilder.getConfiguration().isJsonPrettyPrintOutput()) {
                		mapper.writerWithDefaultPrettyPrinter().writeValue(bufferedWriter, trace);
                	} else {
                		mapper.writeValue(bufferedWriter, trace); // closes the buffered writer and output stream writer
                	}
                } catch (Exception ex) {
                	logger.error("Error writing a trace to file: ", ex);
                }
                // Add a newline to the case of not pretty print (poor temp code as this should be handled by settings in ObjectMapper instead)
                try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName, true), "UTF-8"))) {
                	if (!TraceBuilder.getConfiguration().isJsonPrettyPrintOutput()) {
                		bufferedWriter.append('\n');
                	}
                } catch (Exception ex) {
                	logger.error("Error writing newline character a trace to file: ", ex);
                }
            }	
        } catch (IOException ex) {
            logger.error("Problem writing to JSON: ", ex);
        } finally {
            try {
                if (outputStreamWriter != null)
                    outputStreamWriter.close();
            } catch (IOException ex) {
                logger.error("Error closing output JSON file for writing: ", ex);
            }
        }
    }
}
