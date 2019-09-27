package storages;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import annotations.DBField;
import annotations.DBModel;

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

		final String QUERY_DELETE_TABLE = "DROP TABLE " + TABLE_NAME + " RESTRICT;";

		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_DELETE_TABLE)) {
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Method creates some record in table
	 */
	public int createRecordInTable(Object model) {

		/* here we get name and PK of table, where we need to push record */
		DBModel modelAnnotation = model.getClass().getAnnotation(DBModel.class);
		final String TABLE_NAME = modelAnnotation.tableName().toLowerCase();
		String primaryKey = modelAnnotation.primaryKey().toLowerCase();
		int addedRecordId = -1;

		final String preparedColumns = getPreparedColumns(model, primaryKey);
		final String preparedValues = getPreparedValues(model, primaryKey);
		final String QUERY_CREATE_ON_TABLE = "INSERT INTO " + TABLE_NAME + " (" + preparedColumns + ")" + " VALUES ("
				+ preparedValues + ");";
		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_CREATE_ON_TABLE,
				Statement.RETURN_GENERATED_KEYS)) {
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

	public List<Object> readAllDataFromTable(Class entity) throws InstantiationException, IllegalAccessException {

		List<Object> objects = new ArrayList<Object>();

		DBModel modelAnnotation = (DBModel) entity.getAnnotation(DBModel.class);
		final String TABLE_NAME = modelAnnotation.tableName();
		String primaryKey = modelAnnotation.primaryKey();

		final String QUERY_READ_FROM_TABLE = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + primaryKey + ";";

		try (final Statement statement = this.connection.createStatement();
				final ResultSet rs = statement.executeQuery(QUERY_READ_FROM_TABLE)) {
			while (rs.next()) {
				Object object = entity.newInstance();
				for (Field parsedField : entity.getDeclaredFields()) {
					if (parsedField.getName().equals(primaryKey)) {
						parsedField.setAccessible(true);
						try {
							parsedField.set(object, rs.getInt(primaryKey));
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
					} else {
						parsedField.setAccessible(true);
						try {
							parsedField.set(object, rs.getString(parsedField.getName()));
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
				objects.add(object);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return objects;

	}

	public void updateRecordInTable(Object model) {

		DBModel modelAnnotation = model.getClass().getAnnotation(DBModel.class);
		final String TABLE_NAME = modelAnnotation.tableName();
		String primaryKey = modelAnnotation.primaryKey();

		String columnName = "<null>";
		String fieldValue = "<null>";
		StringBuilder preparedData = new StringBuilder();
		int keyValue = -1;

		for (Field parsedField : model.getClass().getDeclaredFields()) {
			columnName = parsedField.getName();
			if (!columnName.toLowerCase().equals(primaryKey)) { /* skip field that is PK */
				try {
					parsedField.setAccessible(true);
					fieldValue = ((String) parsedField.get(model));
					preparedData.append(columnName + " = '" + fieldValue + "'" + ", ");
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			} else {
				try {
					parsedField.setAccessible(true);
					keyValue = ((Integer) parsedField.get(model));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		preparedData = new StringBuilder(preparedData.toString().trim());
		/* deleting last comma */
		preparedData.delete(preparedData.toString().length() - 1, preparedData.toString().length());

		final String QUERY_UPDATE_ON_TABLE = "UPDATE " + TABLE_NAME + " SET " + preparedData + " WHERE " + primaryKey
				+ " = ?;";
		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_UPDATE_ON_TABLE)) {
			statement.setInt(1, keyValue);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Method deletes record in table by value of field that is primary key
	 */
	public void deleteRecordInTableByPK(Class entity, int keyValue) {

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
				"CREATE TABLE " + modelAnnotation.tableName().toLowerCase() + " (" + primaryKey + " serial, ");

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

	public Object getRecordById(Class entity, int id) {
		return null;		
	}

}
