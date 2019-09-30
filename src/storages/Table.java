package storages;

import SQL.EntityDAO;
import SQL.SQLBuilder;

import java.sql.*;
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
        if(isTableExist(tableName)) {
            final String QUERY_DELETE_TABLE = "DROP TABLE " + tableName +" RESTRICT;";

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

        List<Entity> objects;
        objects = EntityDAO.getInstance().readAllRecordsOrderedByPK(entity);

        return objects;

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
