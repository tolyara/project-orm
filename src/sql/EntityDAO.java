package sql;

import annotations.Column;
import annotations.ManyToOne;
import annotations.PrimaryKey;
import connections.MyConnection;
import storages.Entity;
import storages.PGConnectionPool;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityDAO implements AutoCloseable {

    private static EntityDAO instance;
    Connection connection;

    private EntityDAO() {
        connection = (new MyConnection(false)).getConnection();
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

    public boolean deleteAllRecordsInTable(Entity entity) {

        boolean flag = false;
        final String QUERY_DELETE_ON_TABLE = "TRUNCATE TABLE " + entity.tableName() + ";";
        try (final Statement statement = connection.createStatement()) {
            statement.executeUpdate(QUERY_DELETE_ON_TABLE);
            flag = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public List<Entity> readAllRecordsOrderedByPK(Entity entity) {

        final String TABLE_NAME = entity.tableName();
        String PK_NAME = entity.primaryKey();

        final String QUERY_READ_FROM_TABLE = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + PK_NAME + ";";

        List<Entity> entities = new ArrayList<>();

        try (final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery(QUERY_READ_FROM_TABLE)) {
            while (resultSet.next()) {
                Entity en = setFieldsValue(entity, resultSet, PK_NAME);
                en.loadForeignKeys();
                entities.add(en);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entities;
    }

    public ResultSet getEntityResultSet(Entity entity) throws SQLException {

        final Statement statement = PGConnectionPool.getInstance().getConnection().createStatement();
        final ResultSet resultSet = statement.executeQuery("SELECT * FROM " + entity.getModelAnnotation().tableName() + " WHERE " + entity.primaryKey() + " = " + entity.getPrimaryKeyValue());

        return resultSet;
    }

    public Set<Object> getMappedObjectList(Entity entity, Entity mappedEntity, Field manyToOneField){

        Set<Object> entities = new HashSet<>();
        Entity localEntity;
        try (final Statement statement = PGConnectionPool.getInstance().getConnection().createStatement();
             final ResultSet resultSet = statement.executeQuery("SELECT * FROM " + mappedEntity.getModelAnnotation().tableName() + " WHERE " + manyToOneField.getAnnotation(ManyToOne.class).joinColumn() + " = " + entity.getPrimaryKeyValue())) {
            while (resultSet.next()) {
                localEntity = setFieldsValue(mappedEntity, resultSet, mappedEntity.getModelAnnotation().primaryKey());
                entities.add(localEntity.getEntityObject());
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return entities;
    }

	/*
	 * Method works correct only if we request all columns in query
	 */
	// TODO make it for custom columns
	public List<Entity> executeCustomRequest(String query, Entity entity) {
		String PK_NAME = entity.primaryKey();
		List<Entity> entities = new ArrayList<>();
		try (final Statement statement = this.connection.createStatement();
				final ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				Entity en = setFieldsValue(entity, resultSet, PK_NAME);
				entities.add(en);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return entities;
	}

	public Entity selectEntityById(Entity entity, int id) {

        final String TABLE_NAME = entity.tableName();
        String PK_NAME = entity.primaryKey();
        Entity localEntity = new Entity(entity);

		String QUERY_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE " + PK_NAME + " = " + id;
		try (final Statement statement = connection.createStatement();
				final ResultSet resultSet = statement.executeQuery(QUERY_SELECT_BY_ID)) {
			while (resultSet.next()) {
				localEntity = setFieldsValue(entity, resultSet, PK_NAME);
                localEntity.loadForeignKeys();
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return localEntity;
	}

	public Entity setFieldsValue(Entity entity, ResultSet resultSet, String primaryKey) throws SQLException {
		Entity localEntity = new Entity(entity.getEntityClass());
		try {
			for (Field parsedField : entity.getEntityClass().getDeclaredFields()) {
				parsedField.setAccessible(true);
				if (parsedField.isAnnotationPresent(PrimaryKey.class)) {
					parsedField.set(localEntity.getEntityObject(), resultSet.getInt(primaryKey));
				} else if (parsedField.isAnnotationPresent(Column.class)) {
					final String COLUMN_NAME = parsedField.getAnnotation(Column.class).fieldName();
					parsedField.set(localEntity.getEntityObject(), resultSet.getObject(COLUMN_NAME));
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();

		}
		return localEntity;
	}

    @Deprecated
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

	@Override
	public void close() throws Exception {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
