package nl.martijnkamstra.simscale.writer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.martijnkamstra.simscale.TraceBuilder;
import nl.martijnkamstra.simscale.model.Trace;

import java.io.File;
import java.io.IOException;

/**
 * Created by Martijn Kamstra on 01/04/2017.
 */
public class JsonWriter {
    // The WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED property enables the writing of single element arrays as objects instead
    // of arrays. This is applicable to for example the root which has been configured in the code to potentially
    // consist of more than one element. But as this usually doesn't happen it avoids the array notation in the json
    // output
    private static ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);

    public static void printTraceAsJson(Trace trace) {
        try {
            mapper.writeValue(new File("./generated_trace.json"), trace);
            String jsonTrace = null;
            if (TraceBuilder.getConfiguration().isJsonPrettyPrintOutput()) {
                jsonTrace = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(trace);
            } else {
                jsonTrace = mapper.writeValueAsString(trace);
            }
            System.out.println(jsonTrace);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
