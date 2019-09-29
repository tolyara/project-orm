package storages;

import java.util.HashMap;
import java.util.Map;

public class DataTypes {
    private static DataTypes instance;
    private static Map<String, String> dataTypes;
    private DataTypes(){}

    public static DataTypes getInstance() {
        if(instance == null){
            instance = new DataTypes();

        }
        return instance;
    }

    public Map<String, String> getDataTypes()
    {
        if(dataTypes == null || dataTypes.isEmpty()){
            dataTypes = new HashMap<>();
            addDefaultDataTypes();
        }
        return dataTypes;
    }


    private static void addDefaultDataTypes(){

        dataTypes.put("Byte", "SMALLINT");
        dataTypes.put("byte", "SMALLINT");
        dataTypes.put("Short", "SMALLINT");
        dataTypes.put("short", "SMALLINT");
        dataTypes.put("Integer", "INTEGER");
        dataTypes.put("int", "INTEGER");
        dataTypes.put("Long", "BIGINT");
        dataTypes.put("long", "BIGINT");
        dataTypes.put("Float", "DOUBLE PRECISION");
        dataTypes.put("float", "DOUBLE PRECISION");
        dataTypes.put("Double", "DOUBLE PRECISION");
        dataTypes.put("double", "DOUBLE PRECISION");
        dataTypes.put("Character", "CHAR(5)");
        dataTypes.put("char", "CHAR(5)");
        dataTypes.put("String", "VARCHAR(45)");
        dataTypes.put("Date", "DATE");
        dataTypes.put("boolean", "BOOLEAN");

    }


}
