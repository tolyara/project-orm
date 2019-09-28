package demo.models;

import annotations.Field;
import annotations.ForeignKey;
import annotations.Model;
import storages.Actions;

import java.util.Date;

@Model(tableName = "test", primaryKey = "id")
public class TestModel {

	private int id;

	@Field(fieldName = "test_field")
	private String field;

	@Field(fieldName = "test_sex")
	private boolean sex;

	@Field(fieldName = "birth_date")
	private Date birthDate;

	@Field(fieldName = "age")
	private int age;

	@Field(fieldName = "client_id")
	@ForeignKey(table = "client", column = "id", onUpdate = Actions.CASCADE, onDelete = Actions.CASCADE)
	private int clientId;

	public TestModel() {
	}

	public TestModel(String field) {
		this.field = field;
	}

	public TestModel(int id) {
		this.id = id;
	}

}

