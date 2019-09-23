package com.projectorm.test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CreateTable {
    public static void create(Class entity){
        Statement statement = null;
        try{
            statement = getConnection().createStatement();

            statement.executeUpdate(getSQLRequest(entity, getFieldsNames(entity)));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


//    Method creates SQL request for creating new table.
    private String getSQLRequest (Class entity, List<String> fields) {
        StringBuilder SQLRequest = new StringBuilder("CREATE TABLE " + entity.getSimpleName().toLowerCase()
                + " (id INTEGER not NULL, ");
        for (String name : fields){
            SQLRequest.append(name).append(" VARCHAR(45), ");
        }
        SQLRequest.append("PRIMARY KEY (id))");
        return SQLRequest.toString();
    }

//    Get fields name from entity class with reflection
    private List<String> getFieldsNames (Class entity){
        Field[] allFields = entity.getDeclaredFields();
        List<String> nameFields = new ArrayList<>();
        for (Field f: allFields){
            if (f.isAnnotationPresent(com.projectorm.annotation.Field.class)){
                nameFields.add(f.getName());
            }
        }
        return nameFields;
    }

//    Connection only for PostgreSQL. Need create flexible method for another DB
    private Connection getConnection() {
        Connection connection = null;

        try{
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project_test", "root", "root");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
