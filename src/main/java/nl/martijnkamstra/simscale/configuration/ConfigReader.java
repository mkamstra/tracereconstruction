package nl.martijnkamstra.simscale.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Martijn Kamstra on 01/04/2017.
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);

    public static Configuration readConfig(String configFileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream configFileStream  = ConfigReader.class.getClassLoader().getResourceAsStream(configFileName);
            return mapper.readValue(configFileStream, Configuration.class);
        } catch (IOException ex) {
            // Tried as resource, also try as file
            try {
                return mapper.readValue(new File(configFileName), Configuration.class);
            } catch (IOException e) {
                logger.fatal("Couldn't read the Json configuration file " + configFileName + ": ", ex);
            }
        }

        return null;
    }
}
