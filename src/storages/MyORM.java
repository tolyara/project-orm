package storages;

import java.sql.*;
import java.util.*;

import annotations.Field;
import annotations.ForeignKey;
import annotations.Model;

import service.Settings;

/**
 * Class implements interaction with database
 */
public class MyORM implements AutoCloseable {
	private Map<String, String> valuesTypesForDB;

	{
		valuesTypesForDB = new HashMap<>();
		valuesTypesForDB.put("Byte", "SMALLINT");
		valuesTypesForDB.put("byte", "SMALLINT");
		valuesTypesForDB.put("Short", "SMALLINT");
		valuesTypesForDB.put("short", "SMALLINT");
		valuesTypesForDB.put("Integer", "INTEGER");
		valuesTypesForDB.put("int", "INTEGER");
		valuesTypesForDB.put("Long", "BIGINT");
		valuesTypesForDB.put("long", "BIGINT");
		valuesTypesForDB.put("Float", "DOUBLE PRECISION");
		valuesTypesForDB.put("float", "DOUBLE PRECISION");
		valuesTypesForDB.put("Double", "DOUBLE PRECISION");
		valuesTypesForDB.put("double", "DOUBLE PRECISION");
		valuesTypesForDB.put("Character", "CHAR(5)");
		valuesTypesForDB.put("char", "CHAR(5)");
		valuesTypesForDB.put("String", "VARCHAR(45)");
		valuesTypesForDB.put("Date", "DATE");
		valuesTypesForDB.put("boolean", "BOOLEAN");
	}


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

	public boolean createTable(Class entity) {
		Model annotation = (Model) entity.getAnnotation(Model.class);
		if (checkTableInDataBase(annotation.tableName())) {
			return false;
		}

		boolean flag = false;
		if (entity == null) {
			throw new NullPointerException("Entity should be not null");
		}

		try (Statement statement = PGConnectionPool.getInstance().getConnection().createStatement();){
			statement.executeUpdate(getSQLRequest(entity));

			flag = true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}

	private boolean checkTableInDataBase(String tableName) {
		boolean flag = false;
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet resultSet = metaData.getTables(null, null, tableName, null);
			while (resultSet.next()) {
				if (resultSet.getString(3).equals(tableName)) {
					flag = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}

	private String getSQLRequest(Class entity) {
		Model tableAnnotation = (Model) entity.getAnnotation(Model.class);
		String tableName = tableAnnotation.tableName();
		String primaryKey = tableAnnotation.primaryKey();
		java.lang.reflect.Field[] allFields = entity.getDeclaredFields();
		List<String> fields = getFieldsNames(allFields);
		StringBuilder SQLRequest = new StringBuilder("CREATE TABLE " + tableName
				+ " (" + primaryKey + " serial, ");
		for (int i = 0; i < fields.size(); i++) {
			SQLRequest.append(fields.get(i));
			SQLRequest.append(" ").append(valuesTypesForDB.get(getFieldTypes(allFields).get(i))).append(", ");
		}
		SQLRequest.append("PRIMARY KEY (").append(primaryKey).append(")");
		List<java.lang.reflect.Field> foreignKeyFields = getForeignKeyFields(allFields);
		if (foreignKeyFields.size() > 0) {
			for (java.lang.reflect.Field f : foreignKeyFields) {
				//TODO move to method
				SQLRequest.append(", FOREIGN KEY ");
				ForeignKey annotation = f.getAnnotation(ForeignKey.class);
				SQLRequest.append("(").append(f.getAnnotation(Field.class).fieldName()).append(")");
				SQLRequest.append(" REFERENCES ").append(annotation.table()).append(" ");
				SQLRequest.append("(").append(annotation.column()).append(")");
				SQLRequest.append(" ON UPDATE ").append(annotation.onUpdate().toString()).append(" ");
				SQLRequest.append(" ON DELETE ").append(annotation.onDelete().toString()).append(" ");
			}
		}
		SQLRequest.append(")");
		return SQLRequest.toString();
	}

	private List<String> getFieldsNames(java.lang.reflect.Field[] allFields) {
		List<String> nameFields = new ArrayList<>();
		for (java.lang.reflect.Field f : allFields) {
			if (f.isAnnotationPresent(Field.class)) {
				nameFields.add(f.getAnnotation(Field.class).fieldName());
			}
		}
		return nameFields;
	}

	private List<String> getFieldTypes(java.lang.reflect.Field[] allFields) {
		List<String> typesFields = new ArrayList<>();
		for (java.lang.reflect.Field f : allFields) {
			if (f.isAnnotationPresent(Field.class)) {
				Class<?> fieldType = f.getType();
				typesFields.add(fieldType.getSimpleName());
			}
		}
		return typesFields;
	}

	private List<java.lang.reflect.Field> getForeignKeyFields(java.lang.reflect.Field[] allFields) {
		List<java.lang.reflect.Field> fKeys = new ArrayList<>();
		for (java.lang.reflect.Field f : allFields) {
			if (f.isAnnotationPresent(ForeignKey.class)) {
				fKeys.add(f);
			}
		}
		return fKeys;
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

	public void deleteTable(Class entity) {

		Model modelAnnotation = (Model) entity.getAnnotation(Model.class);
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
		Model modelAnnotation = model.getClass().getAnnotation(Model.class);
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

		Model modelAnnotation = (Model) entity.getAnnotation(Model.class);
		final String TABLE_NAME = modelAnnotation.tableName();
		String primaryKey = modelAnnotation.primaryKey();

		final String QUERY_READ_FROM_TABLE = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + primaryKey + ";";

		try (final Statement statement = this.connection.createStatement();
				final ResultSet rs = statement.executeQuery(QUERY_READ_FROM_TABLE)) {
			while (rs.next()) {
				Object object = entity.newInstance();
				for (java.lang.reflect.Field parsedField : entity.getDeclaredFields()) {
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

		Model modelAnnotation = model.getClass().getAnnotation(Model.class);
		final String TABLE_NAME = modelAnnotation.tableName();
		String primaryKey = modelAnnotation.primaryKey();

		String columnName = "<null>";
		String fieldValue = "<null>";
		StringBuilder preparedData = new StringBuilder();
		int keyValue = -1;

		for (java.lang.reflect.Field parsedField : model.getClass().getDeclaredFields()) {
			columnName = parsedField.getName();
			//todo refactor
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
		Model modelAnnotation = (Model) entity.getAnnotation(Model.class);
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

	private String getPreparedColumns(Object model, String primaryKey) {

		String fieldName = "<null>";
		StringBuilder preparedColumns = new StringBuilder();
		String columns = "<null>";

		for (java.lang.reflect.Field parsedField : model.getClass().getDeclaredFields()) {
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
		/* TODO deleting last comma */
		preparedColumns.delete(preparedColumns.toString().length() - 1, preparedColumns.toString().length());
		columns = preparedColumns.toString();
		return columns;
	}

	private String getPreparedValues(Object model, String primaryKey) {

		String fieldName = "<null>";
		String fieldValue = "<null>";
		StringBuilder preparedValues = new StringBuilder();
		String values = "<null>";

		for (java.lang.reflect.Field parsedField : model.getClass().getDeclaredFields()) {
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

	@Override
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
