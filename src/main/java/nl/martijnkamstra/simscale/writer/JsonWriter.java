package nl.martijnkamstra.simscale.writer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.martijnkamstra.simscale.TraceBuilder;
import nl.martijnkamstra.simscale.model.Trace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Martijn Kamstra on 01/04/2017.
 */
public class JsonWriter {
    private static final Logger logger = LogManager.getLogger(JsonWriter.class);

    // The WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED property enables the writing of single element arrays as objects instead
    // of arrays. This is applicable to for example the root which has been configured in the code to potentially
    // consist of more than one element. But as this usually doesn't happen it avoids the array notation in the json
    // output
    private static ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);

    private static String outputFileName = null; 

    public static void setOutputFileName(String pOutputFileName) {
        outputFileName = pOutputFileName;
    }

    public static void printTraceAsJson(Trace trace) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            String jsonTrace = null;
            if (TraceBuilder.getConfiguration().isJsonPrettyPrintOutput()) {
                jsonTrace = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(trace);
            } else {
                jsonTrace = mapper.writeValueAsString(trace);
            }
            System.out.println(jsonTrace);
            if (outputFileName == null || outputFileName.length() == 0) {
                logger.fatal("Output file name not specified, cannot print trace to file");
                return;
            } else {
                fileWriter = new FileWriter(outputFileName, true); // append
                bufferedWriter = new BufferedWriter(fileWriter);
                if (TraceBuilder.getConfiguration().isJsonPrettyPrintOutput()) {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(bufferedWriter, trace);
                } else {
                    mapper.writeValue(bufferedWriter, trace);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null)
                    bufferedWriter.close();

                if (fileWriter != null)
                    fileWriter.close();
            } catch (IOException ex) {
                logger.error("Error closing output JSON file for writing: ", ex);
            }
        }
    }
}
