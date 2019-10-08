package test;

import demo.models.Client;
import demo.models.Worker;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sql.SQLBuilder;
import storages.Entity;
import storages.Table;

import java.lang.reflect.Field;

public class TestSQLBuilder {

    private static Entity client;
    private static Entity worker;

    @BeforeClass
    public static void createEntity() {
        client = new Entity(Client.class);
        worker = new Entity(Worker.class);
    }

    @Test
    public void buildCreateTableRequestShouldReturnCorrectSQLRequest() {
        String correctSQLRequest = "CREATE TABLE client " +
                "(id serial, " +
                "surname VARCHAR(45), " +
                "name VARCHAR(45), " +
                "isgirl BOOLEAN, " +
                "PRIMARY KEY (id))";
        Assert.assertEquals(correctSQLRequest, SQLBuilder.buildCreateTableRequest(client));
    }

    @Test
    public void buildForeignKeyRequestShouldReturnCorrectSQLRequest() throws NoSuchFieldException {
        String correctSQLRequest = "ALTER TABLE client_worker " +
                "ADD CONSTRAINT fk_client_idclient_worker " +
                "FOREIGN KEY (client_id) REFERENCES client (id) " +
                "ON UPDATE NO ACTION  ON DELETE NO ACTION ";
        Field field = client.getEntityClass().getDeclaredField("workers");
        String joinTableName = Table.getJoinTableName(client, worker);
        Assert.assertEquals(correctSQLRequest, SQLBuilder.buildForeignKeyRequest(client, field, joinTableName));
    }

    @Test
    public void buildJoinTableRequestShouldReturnCorrectSQLRequest() {
        String correctSQLRequest = "CREATE TABLE client_worker" +
                "(id serial, client_id INTEGER, worker_id INTEGER, " +
                "PRIMARY KEY (id))";
        String joinTableName = Table.getJoinTableName(client, worker);
        Assert.assertEquals(correctSQLRequest, SQLBuilder.buildJoinTableRequest(client, worker, joinTableName));
    }

    @Test
    public void buildCreateRecordInJoinTableRequestShouldReturnCorrectSQLRequest() {
        String correctSQLRequest = "INSERT INTO client_worker (client_id, worker_id) VALUES (1, 1)";
        Assert.assertEquals(correctSQLRequest, SQLBuilder.buildCreateRecordInJoinTableRequest(client, worker, 1, 1));
    }


    @AfterClass
    public static void dropTables() {
        Table.deleteEntityTable("client_worker");
        Table.deleteEntityTable("client");
        Table.deleteEntityTable("worker");
    }
}
