package SQL;

import annotations.Model;
import storages.Entity;
import storages.PGConnectionPool;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import annotations.Column;
import annotations.Model;
import annotations.PrimaryKey;

public class EntityDAO {

	private static EntityDAO instance;
	Connection connection;

	private EntityDAO() {
		try {
			connection = PGConnectionPool.getInstance().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static EntityDAO getInstance() {

		if (instance == null)
			instance = new EntityDAO();

		return instance;
	}

	public int createRecordInTable(Entity entity) {
		int addedRecordId = -1;
		final String TABLE_NAME = entity.tableName().toLowerCase();
		final String QUERY_CREATE_ON_TABLE = "INSERT INTO " + TABLE_NAME + " (" + entity.getParsedFieldsLine() + ")"
				+ " VALUES (" + entity.getParsedValuesLine() + ");";

		try (final PreparedStatement statement = connection.prepareStatement(QUERY_CREATE_ON_TABLE,
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

	public boolean deleteRecordInTableByPK(Entity entity) {

		boolean flag = false;
		final String QUERY_DELETE_ON_TABLE = "DELETE FROM " + entity.tableName() + " WHERE " + entity.primaryKey()
				+ " = " + entity.getPrimaryKeyValue();

		try (final PreparedStatement statement = connection.prepareStatement(QUERY_DELETE_ON_TABLE)) {
			statement.executeUpdate();
			flag = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}

	public List<Object> readAllRecordsOrderedByPK(Class<?> entityClass) {

		Model modelAnnotation = (Model) entityClass.getAnnotation(Model.class);
		final String TABLE_NAME = modelAnnotation.tableName();
		String PK_NAME = modelAnnotation.primaryKey();

		final String QUERY_READ_FROM_TABLE = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + PK_NAME + ";";

		List<Object> objects = new ArrayList<>();

		try (final Statement statement = connection.createStatement();
				final ResultSet resultSet = statement.executeQuery(QUERY_READ_FROM_TABLE)) {
			while (resultSet.next()) {
				Object object = getNewInstance(entityClass);
				object = setFieldsValue(entityClass, resultSet, PK_NAME);
				objects.add(object);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return objects;
	}

	public Object selectEntityById(Class<?> entity, int id) {

		Model modelAnnotation = (Model) entity.getAnnotation(Model.class);
		final String TABLE_NAME = modelAnnotation.tableName();
		String PK_NAME = modelAnnotation.primaryKey();
		Object object = new Object();

		String QUERY_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE " + PK_NAME + " = " + id;
		try (final Statement statement = connection.createStatement();
				final ResultSet resultSet = statement.executeQuery(QUERY_SELECT_BY_ID)) {
			resultSet.next();
			object = setFieldsValue(entity, resultSet, PK_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return object;
	}

	private Object setFieldsValue(Class<?> entityClass, ResultSet resultSet, String primaryKey) throws SQLException {
		Object object = getNewInstance(entityClass);
		try {
			for (Field parsedField : entityClass.getDeclaredFields()) {
				parsedField.setAccessible(true);
				if (parsedField.isAnnotationPresent(PrimaryKey.class)) {
					parsedField.set(object, resultSet.getInt(primaryKey));
				} else if (parsedField.isAnnotationPresent(Column.class)) {
					final String COLUMN_NAME = parsedField.getAnnotation(Column.class).fieldName();
					parsedField.set(object, resultSet.getObject(COLUMN_NAME));
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();

		}
		return object;
	}

	private Object getNewInstance(Class<?> entityClass) {
		Object o = null;
		try {
			o = entityClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		return o;
	}

	public void updateRecordInTable(Entity entity) {
		String preparedData = SQLBuilder.buildFieldValuesLine(entity);

		final String QUERY_UPDATE_ON_TABLE = "UPDATE " + entity.tableName() + " SET " + preparedData + " WHERE "
				+ entity.primaryKey() + " = " + entity.getPrimaryKeyValue();
		try (final PreparedStatement statement = PGConnectionPool.getInstance().getConnection()
				.prepareStatement(QUERY_UPDATE_ON_TABLE)) {
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public Connection getConnection() {
		return connection;
	}
}
