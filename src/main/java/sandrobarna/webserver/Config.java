package sandrobarna.webserver;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Exposes a singleton object giving an access to global configuration.
 */
public enum Config {

    INSTANCE;

    final Logger LOGGER = LogManager.getLogger(Config.class);

    Configuration config;

    Config() {

        try {

            this.config = new PropertiesConfiguration("config.properties");

        } catch (ConfigurationException e) {

            LOGGER.error("Error loading server configuration: " + e.getMessage());
        }
    }

    public String get(String property) {
        return this.config.getString(property);
    }
}
