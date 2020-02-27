package test;

import demo.models.Client;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import storages.Entity;
import storages.Table;

import java.util.Arrays;
import java.util.List;

public class TestEntity {
    private static Entity client;

    @BeforeClass
    public static void createEntity() {
        client = new Entity(Client.class);
    }

    @Test
    public void getFieldsNameShouldReturnCorrectList() {
        List<String> correctList = Arrays.asList("surname", "name", "isgirl");
        Assert.assertEquals(correctList, client.getFieldsNames());
    }

    @Test
    public void getFieldTypesShouldReturnCorrectList() {
        List<String> correctList = Arrays.asList("String", "String", "boolean");
        Assert.assertEquals(correctList, client.getFieldTypes());
    }

    @Test
    public void getParsedFieldsLineShouldReturnCorrectLine() {
        String correctLine = "surname, name, isgirl";
        Assert.assertEquals(correctLine, client.getParsedFieldsLine());
    }

    @Test
    public void getParsedValuesLineShouldReturnCorrectLine() {
        String correctLine = "surname, name, isgirl";
        Assert.assertEquals(correctLine, client.getParsedFieldsLine());
    }

    @AfterClass
    public static void dropTables() {
        Table.deleteEntityTable("client_worker");
        Table.deleteEntityTable("client");
        Table.deleteEntityTable("worker");
    }
}
