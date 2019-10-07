package connections;

import java.util.HashMap;

public class DatabaseTypeManager {
	
	private static String DatabaseType = "postgres";

    public static HashMap<String, String> getDatabaseSettings() {
    	HashMap<String, String> databaseSettings = new HashMap<>();
        switch (DatabaseType) {
            case "postgres":
            	databaseSettings.put("driver", "postgres.driver"); 
            	databaseSettings.put("url", "postgres.url");
            	databaseSettings.put("username", "postgres.username");
            	databaseSettings.put("password", "postgres.password");
            	databaseSettings.put("host", "postgres.host");
            	databaseSettings.put("database", "postgres.database");
                break;                
            case "mysql":
            	databaseSettings.put("driver", "mysql.driver"); 
            	databaseSettings.put("url", "mysql.url");
            	databaseSettings.put("username", "mysql.username");
            	databaseSettings.put("password", "mysql.password");
                break;
            default:
                throw new RuntimeException("Error! Unknown Database type");
        }
        return databaseSettings;
    }    

}
