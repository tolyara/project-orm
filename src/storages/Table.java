package storages;

import java.lang.reflect.Field;

import java.sql.*;
import java.util.*;

import sql.EntityDAO;
import sql.SQLBuilder;

/*
 * Class for working with DB tables
 */

public class Table {

    public static boolean createTableFromEntity(Entity entity) {

        boolean flag = false;

        if (isTableExist(entity.tableName())) {
            flag = false;
        } else {

            try (Statement statement = PGConnectionPool.getInstance().getConnection().createStatement()) {
                statement.executeUpdate(SQLBuilder.buildCreateTableRequest(entity));

                List<Field> foreignKeyFields = entity.getForeignKeyFields();
                if (foreignKeyFields.size() > 0) {
                    for (Field field : foreignKeyFields) {
                        statement.executeUpdate(SQLBuilder.buildForeignKeyRequest(entity, field));
                    }
                }

                createManyToManyDependency(entity);

                flag = true;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static boolean isTableExist(String tableName) {
        boolean flag = false;

        try {

            DatabaseMetaData metaData = getConnection().getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, tableName, null);

            if (isResultContainsTableName(resultSet, tableName)) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    private static boolean isResultContainsTableName(ResultSet resultSet, String tableName) throws SQLException {
        final byte TABLE_NAME_COLUMN_INDEX = 3;

        while (resultSet.next()) {
            if (resultSet.getString(TABLE_NAME_COLUMN_INDEX).equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Method creates some record in table
     */

    public static int createRecordInTable(Entity entity) {

        return EntityDAO.getInstance().createRecordInTable(entity);

    }


    public static boolean deleteEntityTable(String tableName) {

        boolean flag = false;
        if (isTableExist(tableName)) {
            final String QUERY_DELETE_TABLE = "DROP TABLE " + tableName + " RESTRICT ;";

            try (final PreparedStatement statement = getConnection().prepareStatement(QUERY_DELETE_TABLE)) {
                statement.executeUpdate();
                flag = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;

    }

    public static List<Entity> readAllDataFromTable(Entity entity) {
        List<Entity> objects = new ArrayList<Entity>();
        return objects;
    }

    private static void createManyToManyDependency(Entity parent) {
        List<Field> manyToManyFields = parent.getManyToManyFields();
        if (manyToManyFields.size() > 0) {
            for (Field desiredField : manyToManyFields) {
                Entity child = getEntityFromFieldWithCollection(desiredField);
                if (!isTableExist(child.tableName())) {
                    createTableFromEntity(child);
                }
                String joinTableName = getJoinTableName(parent, child);
                if (!isTableExist(joinTableName)) {
                    executeManyToManyRequest(parent, child, desiredField, joinTableName);
                }
            }
        }
    }

    public static Entity getEntityFromFieldWithCollection(Field field) {
        Class dependentClassName = null;
        try {
            String fullDesiredFieldName = field.getGenericType().toString();
            String genericClassNameFormList = fullDesiredFieldName.substring(fullDesiredFieldName.indexOf("<") + 1, fullDesiredFieldName.indexOf(">"));
            dependentClassName = Class.forName(genericClassNameFormList);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Entity(dependentClassName);
    }

    public static String getJoinTableName (Entity parentEntity, Entity childEntity) {
        String joinTableName1 = parentEntity.tableName() + "_" + childEntity.tableName();
        String joinTableName2 = childEntity.tableName() + "_" + parentEntity.tableName();
        if (isTableExist(joinTableName1)) {
            return joinTableName1;
        } else if (isTableExist(joinTableName2)) {
            return joinTableName2;
        } else return joinTableName1;
    }

    private static void executeManyToManyRequest(Entity parent, Entity child, Field field, String tableName) {
        try (final Statement statement = PGConnectionPool.getInstance().getConnection().createStatement()) {
            statement.executeUpdate(SQLBuilder.buildJoinTableRequest(parent, child, tableName));
            statement.executeUpdate(SQLBuilder.buildForeignKeyRequest(parent, field, tableName));
            statement.executeUpdate(SQLBuilder.buildForeignKeyRequest(child, field, tableName));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return PGConnectionPool.getInstance().getConnection();
    }
}
