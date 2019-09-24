package storages;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import org.postgresql.jdbc.*;

import annotations.DBField;
import annotations.DBModel;
import models.TestModel;

//TODO - возможность передать в запрос имя табл. +++

import service.Settings;

/**
 * Class implements interaction with database
 */
public class MyORM implements DataStorage, AutoCloseable {

	private Connection connection;

	public Connection getConnection() {
		return connection;
	}

	private static final String QUERY_CREATE_TABLE = "CREATE TABLE ? ( test_id serial PRIMARY KEY, test_field VARCHAR(20) );";
	private static final String QUERY_DROP_TABLE = "DROP TABLE (?);";
	private static final String QUERY_INSERT = "INSERT INTO test (test_field) VALUES (?);";
	private static final String QUERY_DELETE = "DELETE FROM ?;";
	private static final String QUERY_UPDATE = "UPDATE test AS test SET test_field = ? WHERE test.test_id = ?;";
	private static final String QUERY_DROP_AND_CREATE_TABLE = "DROP TABLE ?;" + "CREATE TABLE ?;";

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

	public void createTable(Class entity) {

		Statement statement = null;
		try {
			statement = connection.createStatement();
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

	public void deleteTable(String tableName) {

		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_DROP_TABLE)) {
			statement.setString(1, tableName.trim());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Method creates some record in test table
	 */
	public void testCreateRecordInDB(Object testModel) {

		/* here we get name of table, where we need to push record */
		DBModel modelAnnotation = testModel.getClass().getAnnotation(DBModel.class);
		final String TABLE_NAME = modelAnnotation.tableName().trim();

		String fieldName = "<null>";
		String fieldValue = "<null>";
		try {
			Field parsedField = testModel.getClass().getDeclaredField("field");
			/* getting name of column we need to push record */
			DBField fieldAnnotation = parsedField.getAnnotation(DBField.class);
			fieldName = fieldAnnotation.fieldName().trim();

			parsedField.setAccessible(true);
			/* getting value that we need to push */
			fieldValue = ((String) parsedField.get(testModel)).trim();

		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}

		final String QUERY_CREATE_ON_TABLE = "INSERT INTO " + TABLE_NAME + "(" + fieldName + ")" + " VALUES (?);";

		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_CREATE_ON_TABLE)) {
			statement.setString(1, fieldValue);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateData(String tableName, int elementId, String newElementName) {
		final String QUERY_UPDATE_ON_TABLE = "UPDATE " + tableName
				+ " AS test SET test_field = ? WHERE test.test_id = ?;";
		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_UPDATE_ON_TABLE)) {
			statement.setString(1, newElementName.trim());
			statement.setInt(2, elementId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Method deletes some record in test table by field "id"
	 */
	public void testDeleteRecordFromDB(Object testModel) {

		/* here we get name of table, where we need to delete record */
		DBModel modelAnnotation = testModel.getClass().getAnnotation(DBModel.class);
		final String TABLE_NAME = modelAnnotation.tableName().trim();

		String fieldName = "<null>";
		Integer fieldValue = -1;
		try {
			Field parsedField = testModel.getClass().getDeclaredField("id");
			/* getting name of the column by which we need to delete record */
			DBField fieldAnnotation = parsedField.getAnnotation(DBField.class);
			fieldName = fieldAnnotation.fieldName().trim();

			parsedField.setAccessible(true);
			/* getting value by which we need to delete record */
			fieldValue = ((Integer) parsedField.get(testModel));

		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}

		final String QUERY_DELETE_ON_TABLE = "DELETE FROM " + TABLE_NAME + " AS test WHERE test." + fieldName + " = ?;";

		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_DELETE_ON_TABLE)) {
			statement.setInt(1, fieldValue);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteAllData(String tableName) {
		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_DELETE)) {
			statement.setString(1, tableName.trim());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Method creates SQL request for creating new table.
	private String getSQLRequest(Class entity, List<String> fields) {

		String primaryKey = "<null>";
		DBModel modelAnnotation = (DBModel) entity.getAnnotation(DBModel.class);
		primaryKey = modelAnnotation.primaryKey().trim();

		StringBuilder SQLRequest = new StringBuilder(
				"CREATE TABLE " + entity.getSimpleName().toLowerCase() + " (" + primaryKey + " INTEGER not NULL, ");

		// StringBuilder SQLRequest = new StringBuilder(
		// "CREATE TABLE " + entity.getSimpleName().toLowerCase() + " (" );

//		System.out.println(entity.getSimpleName().toLowerCase());

		for (String name : fields) {
			if (!name.equals(primaryKey)) {
				SQLRequest.append(name).append(" VARCHAR(45), ");
			}
		}
		SQLRequest.append("PRIMARY KEY (id))");
		return SQLRequest.toString();
	}

	// Get fields name from entity class with reflection
	private List<String> getFieldsNames(Class entity) {
		Field[] allFields = entity.getDeclaredFields();
		List<String> nameFields = new ArrayList<>();
		for (Field f : allFields) {
			if (f.isAnnotationPresent(DBField.class)) {
				nameFields.add(f.getName());
			}
		}
		return nameFields;
	}

	// Connection only for PostgreSQL. Need create flexible method for another DB
	// private Connection getConnection() {
	// Connection connection = null;
	//
	// try{
	// connection =
	// DriverManager.getConnection("jdbc:postgresql://localhost:5432/project_test",
	// "root", "root");
	// return connection;
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// return connection;
	// }

}
