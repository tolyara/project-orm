package storages;

import java.sql.*;
import java.util.*;

import annotations.Model;

import service.Settings;

/**
 * Class implements interaction with database
 */
public class MyORM implements AutoCloseable {


	private Connection connection;

	public Connection getConnection() {
		return connection;
	}

	public MyORM() {
		try {
			this.connection = PGConnectionPool.getInstance().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public MyORM(String driver) {
		final Settings settings = Settings.getInstance();
		try {
			Class.forName(driver);
			this.connection = DriverManager.getConnection(settings.getValue("postgres.url"),
					settings.getValue("postgres.username"), settings.getValue("postgres.password"));
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}




	/*
    private String addForeignKeys(Class entity) {
        int number = 1;
        Field[] allFields = entity.getDeclaredFields();
        DBModel tableAnnotation = (DBModel) entity.getAnnotation(DBModel.class);
        String tableName = tableAnnotation.tableName();
        StringBuilder SQLRequest = new StringBuilder("ALTER TABLE tests ADD CONSTRAINT fk_name_key");
        List<Field> foreignKeyFields = getForeignKeyFields(allFields);
        if (foreignKeyFields.size() > 0) {
            SQLRequest.append("ALTER TABLE ").append(tableName).append(" ADD CONSTRAINT ");
            for(Field f : foreignKeyFields){
                SQLRequest.append("fk_").append(tableName).append(number++);
                SQLRequest.append(" FOREIGN KEY ");
                DBForeignKey annotation = f.getAnnotation(DBForeignKey.class);
                SQLRequest.append("(").append(f.getAnnotation(DBField.class).fieldName()).append(")");
                SQLRequest.append(" REFERENCES ").append(annotation.table()).append(" ");
                SQLRequest.append("(").append(annotation.column()).append(")");
                SQLRequest.append(" ON UPDATE ").append(annotation.onUpdate().toString()).append(" ");
                SQLRequest.append(" ON DELETE ").append(annotation.onDelete().toString()).append(" ");
            }
        }
        return SQLRequest.toString();
    }*/






	/*
	 * Method deletes record in table by value of field that is primary key
	 */




	@Override
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
