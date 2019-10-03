package test;

import demo.models.Client;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import storages.Entity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestEntity {
    private static Entity client;

    @BeforeClass
    public static void createEntity() {
        client = new Entity(Client.class);
    }

    @Test
    public void getFieldsNameShouldReturnCorrectList() {
        List<String> correctList = Arrays.asList("surname", "name", "isgirl", "test_id");
        Assert.assertEquals(correctList, client.getFieldsNames());
    }

    @Test
    public void getFieldTypesShouldReturnCorrectList() {
        List<String> correctList = Arrays.asList("String", "String", "String", "int");
        Assert.assertEquals(correctList, client.getFieldTypes());
    }

    @Test
    public void getForeignKeyFieldsShouldReturnCorrectList() throws NoSuchFieldException {
        List<Field> correctValue = Collections.singletonList(Client.class.getDeclaredField("testId"));
        Assert.assertEquals(correctValue, client.getForeignKeyFields());
    }

    @Test
    public void getParsedFieldsLineShouldReturnCorrectLine() {
        String correctLine = "surname, name, isgirl, testid";
        Assert.assertEquals(correctLine, client.getParsedFieldsLine());
    }

    @Test
    public void getParsedValuesLineShouldReturnCorrectLine() {
        String correctLine = "surname, name, isgirl, testid";
        Assert.assertEquals(correctLine, client.getParsedFieldsLine());
    }
}
