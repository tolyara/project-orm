package test;

import demo.models.Client;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
}
