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

    //todo refactor
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
                    SQLRequest.append("ALTER TABLE ").append(entity.tableName()).append(" ADD CONSTRAINT ");
                    SQLRequest.append("fk_").append(entity.tableName()).append("_").append(field.getName());
                    SQLRequest.append(" FOREIGN KEY ");
                    SQLRequest.append("(").append(field.getAnnotation(ManyToOne.class).joinColumn()).append(")");
                    SQLRequest.append(" REFERENCES ").append(entityRequest.getModelAnnotation().tableName()).append(" ");
                    SQLRequest.append("(").append(entityRequest.getModelAnnotation().primaryKey()).append(")");
                    SQLRequest.append(" ON UPDATE ").append(field.getAnnotation(ManyToOne.class).onUpdate().toString()).append(" ");
                    SQLRequest.append(" ON DELETE ").append(field.getAnnotation(ManyToOne.class).onDelete().toString()).append(" ");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                try (final PreparedStatement statement = getConnection().prepareStatement(SQLRequest.toString())) {
                    statement.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void createManyToManyDependency(Entity firstEntity, Statement statement) {
        List<Field> manyToManyFields = firstEntity.getManyToManyFields();

        for (Field desiredField : manyToManyFields) {
            try {
                Entity secondEntity = getEntityFromFieldName(desiredField);
                if (!isTableExist(secondEntity.tableName())) {
                    createTableFromEntity(secondEntity);
                }
                String helpTableName1 = firstEntity.tableName() + "_" + secondEntity.tableName();
                String helpTableName2 = secondEntity.tableName() + "_" + firstEntity.tableName();
                if (!isTableExist(helpTableName1) && !isTableExist(helpTableName2)) {
                    String firstColumnName = firstEntity.tableName() + "_id";
                    String secondColumnName = secondEntity.tableName() + "_id";
                    String createTable = "CREATE TABLE " + helpTableName1 + "(" + firstColumnName + " INTEGER, " + secondColumnName + " INTEGER)";
                    statement.executeUpdate(createTable);
                    statement.executeUpdate(SQLBuilder.buildForeignKeyRequest(firstEntity, desiredField, helpTableName1));
                    statement.executeUpdate(SQLBuilder.buildForeignKeyRequest(secondEntity, desiredField, helpTableName1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static Entity getEntityFromFieldName(Field field) {
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
