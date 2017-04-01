package nl.martijnkamstra.simscale.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Martijn Kamstra on 01/04/2017.
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);

    public static Configuration readConfig(String configFileName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream configFileStream  = ConfigReader.class.getClassLoader().getResourceAsStream(configFileName);
            return mapper.readValue(configFileStream, Configuration.class);
        } catch (IOException ex) {
            logger.fatal("Couldn't read the Json configuration file " + configFileName + ": ", ex);
        }

        return null;
    }
}
