package storages;

import SQL.EntityDAO;
import SQL.SQLBuilder;
import annotations.ManyToMany;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

                createManyToManyDependency(entity, statement);

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

    // TODO Refactor if method from EntityDAO will work
    public static List<Entity> readAllDataFromTable(Entity entity) {

        List<Entity> objects = new ArrayList<Entity>();
//        objects = EntityDAO.getInstance().readAllRecordsOrderedByPK(entity);
        return objects;

    }

    private static void createManyToManyDependency(Entity firstEntity, Statement statement) {
        List<Field> manyToManyFields = firstEntity.getManyToManyFields();
        if (manyToManyFields.size() > 0) {
            for (Field desiredField : manyToManyFields) {
                Entity secondEntity = getEntityFromField(desiredField);
                if (!isTableExist(secondEntity.tableName())) {
                    createTableFromEntity(secondEntity);
                }
                String joinTableName1 = firstEntity.tableName() + "_" + secondEntity.tableName();
                String joinTableName2 = secondEntity.tableName() + "_" + firstEntity.tableName();
                if (!isTableExist(joinTableName1) && !isTableExist(joinTableName2)) {
                    try {
                        statement.executeUpdate(SQLBuilder.buildJoinTableRequest(firstEntity, secondEntity, joinTableName1));
                        statement.executeUpdate(SQLBuilder.buildForeignKeyRequest(firstEntity, desiredField, joinTableName1));
                        statement.executeUpdate(SQLBuilder.buildForeignKeyRequest(secondEntity, desiredField, joinTableName1));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //TODO change name method, have to do refactor
    public static void doMagic(Entity main, Entity dependent, int id1, int id2) {
        List<Field> fields =  main.getManyToManyFields();
        for (Field field: fields){
            field.setAccessible(true);
            try {
                List<Object> list = (List<Object>) field.get(main.getEntityObject());
                list.add(dependent.getEntityObject());
                field.set(main.getEntityObject(), list);
                String joinTableName1 = main.tableName() + "_" + dependent.tableName();
                String joinTableName2 = dependent.tableName() + "_" + main.tableName();
                String columnName1 = main.tableName() + "_id";
                String columnName2 = dependent.tableName() + "_id";
                try (Statement statement = PGConnectionPool.getInstance().getConnection().createStatement()) {
                    String requestName = "";
                    if (isTableExist(joinTableName1)) {
                        requestName = joinTableName1;
                    } else if (isTableExist(joinTableName2)) {
                        requestName = joinTableName2;
                    }
                    String request = "INSERT INTO " + requestName
                            + " (" + columnName1 + ", " + columnName2 + ") "
                            + " VALUES (" + id1 + ", " + id2 + ")";
                    statement.executeUpdate(request);
                }
            } catch (IllegalAccessException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static Entity getEntityFromField(Field field) {
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

    private static Connection getConnection() throws SQLException {
        return PGConnectionPool.getInstance().getConnection();
    }


}
