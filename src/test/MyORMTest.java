package test;

import demo.models.Client;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import storages.MyORM;
import storages.Table;

import java.sql.SQLException;

public class MyORMTest {

    private static MyORM orm;
    /*
    @BeforeClass
    public static void createMyORM(){
        orm = new MyORM();
    }

    @Test(expected = NullPointerException.class)
    public void parameterEqualsToNullShouldThrowsException() {
        Table.createTableFromEntity(null);
    }

    @Test
    public void methodShouldReturnTrueIfTableCreated() {
        Assert.assertTrue(Table.createTableFromEntity(Client.class));
    }

    @Test
    public void methodShouldReturnFalseIfTableAlreadyExist() {
        Table.createTableFromEntity(Client.class);
        Assert.assertFalse(Table.createTableFromEntity(Client.class));
    }

    @Test
    public void connectionShouldBeClosed() {
        orm.close();
        try {
            Assert.assertTrue(orm.getConnection().isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
}