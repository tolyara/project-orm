package test;

import demo.models.Client;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sql.SQLBuilder;
import storages.Entity;

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
}
