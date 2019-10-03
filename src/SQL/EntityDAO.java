package SQL;

import annotations.Model;
import storages.Entity;
import storages.PGConnectionPool;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import annotations.Column;

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

	public boolean deleteRecordInTableByPK(Entity entity, int keyValue) {

		boolean flag = false;
		final String QUERY_DELETE_ON_TABLE = "DELETE FROM " + entity.tableName() + " WHERE " + entity.primaryKey()
				+ " = " + keyValue;

		try (final PreparedStatement statement = connection.prepareStatement(QUERY_DELETE_ON_TABLE)) {
			statement.executeUpdate();
			flag = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}

	// TODO elements of list must be unique
	public List<Entity> readAllRecordsOrderedByPK(Entity entity) {
		final String TABLE_NAME = entity.tableName();
		final String PK_NAME = entity.primaryKey();
		final String QUERY_READ_FROM_TABLE = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + PK_NAME + ";";

		List<Entity> entities = new ArrayList<>();

		try (final Statement statement = connection.createStatement();
				final ResultSet rs = statement.executeQuery(QUERY_READ_FROM_TABLE)) {
			while (rs.next()) {
				Entity en = new Entity(entity.getEntityObject());
				en = selectEntityById(entity, rs.getInt(PK_NAME));
				// Entity en = selectEntityById(entity, rs.getInt(PK_NAME));
				entities.add(en);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return entities;
	}

	public Entity selectEntityById(Entity entity, int id) {
		String QUERY_SELECT_BY_ID = "SELECT * FROM " + entity.tableName() + " WHERE " + entity.primaryKey() + " = "
				+ id;
		try (final PreparedStatement statement = connection.prepareStatement(QUERY_SELECT_BY_ID)) {
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			for (Field parsedField : entity.getEntityClass().getDeclaredFields()) {

				try {
					parsedField.setAccessible(true);
					if (parsedField.getName().equals(entity.primaryKey())) {
						parsedField.set(entity.getEntityObject(), resultSet.getInt(entity.primaryKey()));
					} else if(parsedField.getAnnotation(Column.class) != null) {

						parsedField.set(entity.getEntityObject(), resultSet.getString(parsedField.getAnnotation(Column.class).fieldName()));
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return entity;
	}

	public void updateEntity(Entity entity) {
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
