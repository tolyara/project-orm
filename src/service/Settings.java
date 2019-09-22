package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
	
	private final Properties properties = new Properties();

    private Settings() {
        try {
            properties.load(new FileInputStream(this.getClass().getClassLoader().getResource("/resources/orm.properties").getFile()));
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
