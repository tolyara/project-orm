package test;

import demo.models.Client;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import storages.MyORM;

import java.sql.SQLException;

public class MyORMTest {

    private static MyORM orm;

    @BeforeClass
    public static void createMyORM(){
        orm = new MyORM();
    }

    @Test(expected = NullPointerException.class)
    public void parameterEqualsToNullShouldThrowsException() {
        orm.createTable(null);
    }

    @Test
    public void methodShouldReturnTrueIfTableCreated() {
        Assert.assertTrue(orm.createTable(Client.class));
    }

    @Test
    public void methodShouldReturnFalseIfTableAlreadyExist() {
        orm.createTable(Client.class);
        Assert.assertFalse(orm.createTable(Client.class));
    }

    @Test
    public void connectionShouldBeClosed() {
        orm.close();
        try {
            Assert.assertTrue(orm.getConnection().isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}