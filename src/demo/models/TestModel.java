package demo.models;

import annotations.Column;
import annotations.ForeignKey;
import annotations.Model;
import storages.Actions;

import java.util.Date;

@Model(tableName = "test", primaryKey = "id")
public class TestModel {

	private int id;

	@Column(fieldName = "test_field")
	private String field;

	@Column(fieldName = "test_sex")
	private boolean sex;

	@Column(fieldName = "birth_date")
	private Date birthDate;

	@Column(fieldName = "age")
	private int age;

	@Column(fieldName = "client_id")
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

