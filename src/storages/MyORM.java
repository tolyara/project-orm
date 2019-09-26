package storages;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import org.postgresql.jdbc.*;

import annotations.DBField;
import annotations.DBModel;
import models.TestModel;

import service.Settings;

/**
 * Class implements interaction with database
 */
public class MyORM implements DataStorage, AutoCloseable {

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

	public void deleteTable(Class entity) {
		
		DBModel modelAnnotation = (DBModel) entity.getAnnotation(DBModel.class);
		final String TABLE_NAME = modelAnnotation.tableName();
		
		final String QUERY_DELETE_TABLE = "DROP TABLE ? RESTRICT;";
		System.out.println(QUERY_DELETE_TABLE);

<<<<<<< HEAD
		
=======
		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_DELETE_TABLE)) {
			statement.setString(1, TABLE_NAME);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
>>>>>>> refs/heads/connections

	}

	/*
	 * Method creates some record in table
	 */
<<<<<<< HEAD
	public void createRecordInTable(Object testModel) {
=======
	public int сreateRecordInTable(Object model) {
>>>>>>> refs/heads/connections

		/* here we get name and PK of table, where we need to push record */
		DBModel modelAnnotation = model.getClass().getAnnotation(DBModel.class);
		final String TABLE_NAME = modelAnnotation.tableName().toLowerCase();
<<<<<<< HEAD
		String primaryKey = modelAnnotation.primaryKey();

		String fieldName = "<null>";
		String fieldValue = "<null>";
		StringBuilder preparedColumns = new StringBuilder();
		StringBuilder preparedValues = new StringBuilder();

		for (Field parsedField : testModel.getClass().getDeclaredFields()) {
			fieldName = parsedField.getName(); /* getting name of column we need to push record */
			if (!fieldName.toLowerCase().equals(primaryKey)) { /* skip field that is PK */
				try {
					preparedColumns.append(fieldName.toLowerCase() + ", ");

					parsedField.setAccessible(true);
					/* getting value that we need to push */
					fieldValue = ((String) parsedField.get(testModel)).trim();
					preparedValues.append(fieldValue + ", ");

				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		preparedColumns = new StringBuilder(preparedColumns.toString().trim());
		preparedValues = new StringBuilder(preparedValues.toString().trim());
		preparedColumns.delete(preparedColumns.toString().length() - 1, preparedColumns.toString().length()); // deleting
																												// last
																												// comma
		preparedValues.delete(preparedValues.toString().length() - 1, preparedValues.toString().length()); // deleting
																											// last
																											// comma

		final String QUERY_CREATE_ON_TABLE = "INSERT INTO " + TABLE_NAME + " (" + preparedColumns.toString() + ")"
				+ " VALUES (" + preparedValues.toString() + ");";

		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_CREATE_ON_TABLE)) {
=======
		String primaryKey = modelAnnotation.primaryKey().toLowerCase();
		int addedRecordId = -1;
		
		final String preparedColumns = getPreparedColumns(model, primaryKey);
		final String preparedValues = getPreparedValues(model, primaryKey);
		final String QUERY_CREATE_ON_TABLE = "INSERT INTO " + TABLE_NAME + " (" + preparedColumns + ")"
				+ " VALUES (" + preparedValues + ");";
		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_CREATE_ON_TABLE, Statement.RETURN_GENERATED_KEYS)) {			
>>>>>>> refs/heads/connections
			statement.executeUpdate();
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
				addedRecordId = generatedKeys.getInt(1);
				} else {
					throw new IllegalStateException("Could not return PK of added client!");
				}
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return addedRecordId;
	}

	public void updateDataOnTestTable(String tableName, int elementId, String newElementName) {
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
	 * Method deletes record in table by value of field that is primary key
	 */
<<<<<<< HEAD
	public void deleteRecordFromTable(Object model) {
=======
	public void deleteRecordInTableByPK(Class entity, int keyValue) {
>>>>>>> refs/heads/connections

<<<<<<< HEAD
=======
		/* here we get name of table, where we need to delete record */
		DBModel modelAnnotation = (DBModel) entity.getAnnotation(DBModel.class);
		final String TABLE_NAME = modelAnnotation.tableName();
		String primaryKey = modelAnnotation.primaryKey();

		final String QUERY_DELETE_ON_TABLE = "DELETE FROM " + TABLE_NAME + " WHERE " + primaryKey + " = ?;";

		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_DELETE_ON_TABLE)) {
			statement.setInt(1, keyValue);
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
>>>>>>> refs/heads/connections
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

		/* here we get the name of primary key of table */
		String primaryKey = "<null>";
		DBModel modelAnnotation = (DBModel) entity.getAnnotation(DBModel.class);
		primaryKey = modelAnnotation.primaryKey();

		StringBuilder SQLRequest = new StringBuilder(
<<<<<<< HEAD
				"CREATE TABLE " + entity.getSimpleName().toLowerCase() + " (" + primaryKey + " serial, ");
=======
				"CREATE TABLE " + modelAnnotation.tableName().toLowerCase() + " (" + primaryKey + " serial, ");
>>>>>>> refs/heads/connections

		for (String name : fields) {
			if (!name.equals(primaryKey)) {
				SQLRequest.append(name).append(" VARCHAR(45), ");
			}
		}
		SQLRequest.append("PRIMARY KEY (" + primaryKey + "))");
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
	
	private String getPreparedColumns(Object model, String primaryKey) {
		
		String fieldName = "<null>"; 
		StringBuilder preparedColumns = new StringBuilder();
		String columns = "<null>";

		for (Field parsedField : model.getClass().getDeclaredFields()) {
			fieldName = parsedField.getName(); /* getting name of column we need to push record */
			if (!fieldName.toLowerCase().equals(primaryKey)) { /* skip field that is PK */
				try {
					preparedColumns.append(fieldName.toLowerCase() + ", ");
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		preparedColumns = new StringBuilder(preparedColumns.toString().trim());
		/* deleting last comma */
		preparedColumns.delete(preparedColumns.toString().length() - 1, preparedColumns.toString().length()); 
		columns = preparedColumns.toString();
		return columns;		
	}
	
	private String getPreparedValues(Object model, String primaryKey) {
		
		String fieldName = "<null>"; 
		String fieldValue = "<null>";
		StringBuilder preparedValues = new StringBuilder();
		String values = "<null>";

		for (Field parsedField : model.getClass().getDeclaredFields()) {
			fieldName = parsedField.getName(); /* getting name of column we need to push record */
			if (!fieldName.toLowerCase().equals(primaryKey)) { /* skip field that is PK */
				try {
					parsedField.setAccessible(true);
					/* getting value that we need to push */
					fieldValue = ((String) parsedField.get(model)).trim();
					preparedValues.append("'" + fieldValue + "'" + ", ");
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		preparedValues = new StringBuilder(preparedValues.toString().trim());
		/* deleting last comma */
		preparedValues.delete(preparedValues.toString().length() - 1, preparedValues.toString().length()); 
		values = preparedValues.toString();
		return values;		
	}

}
