package storages;




import java.lang.reflect.Field;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import annotations.ManyToOne;
import connections.MyConnection;
import sql.EntityDAO;
import sql.SQLBuilder;

/**
 * Class for working with DB tables
 */

public class Table {

    public static boolean createTableFromEntity(Entity entity) {

        boolean flag = false;

        if (!isTableExist(entity.tableName())) {
            try (Statement statement = new MyConnection(false).getConnection().createStatement()) {
                statement.executeUpdate(SQLBuilder.buildCreateTableRequest(entity));

                List<Field> foreignKeyFields = entity.getForeignKeyFields();
                if (foreignKeyFields.size() > 0) {
                    for (Field field : foreignKeyFields) {
                        statement.executeUpdate(SQLBuilder.buildCreateForeignKeyRequest(entity, field));
                    }
                }
                createManyToOneDependency(entity);
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

            DatabaseMetaData metaData = new MyConnection(false).getConnection().getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, tableName, null);

            if (isResultContainsTableName(resultSet, tableName)) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
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

    private static void createManyToOneDependency(Entity entity) {
        StringBuilder SQLRequest = new StringBuilder();

        for (Field field : entity.getEntityObject().getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ManyToOne.class)) {


                try {
                    Entity entityRequest = new Entity(Class.forName(field.getType().getName()));
                    if (!Table.isTableExist(entityRequest.tableName()))
                        Table.createTableFromEntity(entityRequest);
                    //alter parent table id column to child table
                    try (final PreparedStatement statement = getConnection().prepareStatement(SQLBuilder.alterIntFieldLine(entity.getModelAnnotation().tableName(), entityRequest.getModelAnnotation().tableName() + "_id"))) {
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //create fk from child to parent by id
                    try (final PreparedStatement statement = getConnection().prepareStatement(SQLBuilder.buildFkForManyToOne(entity, entityRequest, field))) {
                        statement.executeUpdate();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private static void createManyToManyDependency(Entity parent) {
        List<Field> manyToManyFields = parent.getManyToManyFields();
        if (manyToManyFields.size() > 0) {
            for (Field desiredField : manyToManyFields) {
                Entity child = getEntityFromFieldWithCollection(desiredField);
                if (!Table.isTableExist(child.tableName())) {
                    Table.createTableFromEntity(child);
                }
                String joinTableName = Table.getJoinTableName(parent, child);
                if (!Table.isTableExist(joinTableName)) {
                    executeManyToManyRequest(parent, child, desiredField, joinTableName);
                }
            }
        }
    }

    private static void executeManyToManyRequest(Entity parent, Entity child, Field field, String tableName) {
        try (final Statement statement = new MyConnection(false).getConnection().createStatement()) {
            statement.executeUpdate(SQLBuilder.buildJoinTableRequest(parent, child, tableName));
            statement.executeUpdate(SQLBuilder.buildForeignKeyRequest(parent, field, tableName));
            statement.executeUpdate(SQLBuilder.buildForeignKeyRequest(child, field, tableName));
        } catch (SQLException e) {
            e.printStackTrace();
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

    private static boolean isResultContainsTableName(ResultSet resultSet, String tableName) throws SQLException {
        final byte TABLE_NAME_COLUMN_INDEX = 3;

        while (resultSet.next()) {
            if (resultSet.getString(TABLE_NAME_COLUMN_INDEX).equals(tableName)) {
                return true;
            }
        }
        return false;
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


    private static Connection getConnection() throws SQLException {
        return PGConnectionPool.getInstance().getConnection();
    }


}
