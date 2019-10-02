package test;

import SQL.SQLBuilder;
import demo.models.Client;
import demo.models.TestModel;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import storages.Entity;
import storages.Table;

public class TestSQLBuilder {

    private static Entity client;

    @BeforeClass
    public static void createEntity() {
        client = new Entity(Client.class);
    }

    @Test
    public void methodShouldReturnCorrectSQLRequest() {
        String correctSQLRequest = "CREATE TABLE client " +
                "(id serial, " +
                "surname VARCHAR(45), " +
                "name VARCHAR(45), " +
                "isgirl VARCHAR(45), " +
                "test_id INTEGER, " +
                "PRIMARY KEY (id))";
        Assert.assertEquals(correctSQLRequest, SQLBuilder.buildCreateTableRequest(client));
    }

    @Test
    public void methodShouldCreateDependentTableIfItIsNotExist() throws NoSuchFieldException {
        String requestTableName = new Entity(TestModel.class).tableName();
        Table.deleteEntityTable(requestTableName);
        SQLBuilder.buildCreateForeignKeyRequest(client, Client.class.getDeclaredField("testId"));
        Assert.assertTrue(Table.isTableExist(requestTableName));
    }
}
