package services;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Class serves the work with the configuration files, such as ./resources/orm.properties
 */
public class Settings {
	
	private final Properties properties = new Properties();
	private static final String PROPERTIES_PATH_1 = "out/production/ProjectORM/resources/orm.properties";

    private Settings() {
        try {
            properties.load(new FileInputStream(PROPERTIES_PATH_1));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * SingletonHolder is loaded on the first execution of Settings.getInstance()
     * or the first access to SettingsHolder.INSTANCE, not before.
     */
    private static class SettingsHolder {
        private static final Settings INSTANCE = new Settings();
      }

    public static Settings getInstance() {
        return SettingsHolder.INSTANCE;
    }

    public String getValue(String key) {
        return this.properties.getProperty(key);
    }

}
