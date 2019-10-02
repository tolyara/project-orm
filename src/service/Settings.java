package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Class serves the work with the configuration files, such as ./resources/orm.properties
 */
public class Settings {
	
	private final Properties properties = new Properties();

    private Settings() {
        try {
            properties.load(new FileInputStream("C:\\Users\\Дмитрий\\IdeaProjects\\ProjectORM\\out\\production\\ProjectORM\\resources\\orm.properties"));
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
