package test;

import demo.models.Client;
import demo.models.Worker;
import org.junit.*;
import storages.Entity;
import storages.Table;

public class TestTable {
    private static Entity client;

    @BeforeClass
    public static void createEntity() {
        client = new Entity(Client.class);
    }

    @Test
    public void methodShouldReturnTrueIfTableCreated() {
        Table.deleteEntityTable(Table.getJoinTableName(client, new Entity(Worker.class)));
        Table.deleteEntityTable(client.tableName());
        Assert.assertTrue(Table.createTableFromEntity(client));
    }

    @Test
    public void methodShouldReturnTrueIfTableAlreadyExist() {
        Table.createTableFromEntity(client);
        Assert.assertTrue(Table.isTableExist(client.tableName()));
    }

    @Test
    public void methodShouldReturnTrueIfTableDeleted() {
        Table.createTableFromEntity(client);
        Assert.assertTrue(Table.deleteEntityTable(client.tableName()));
    }

    @AfterClass
    public static void dropTables() {
        Table.deleteEntityTable("client_worker");
        Table.deleteEntityTable("client");
        Table.deleteEntityTable("worker");
    }
}
