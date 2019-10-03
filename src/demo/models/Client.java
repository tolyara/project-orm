package demo.models;

import annotations.Column;
import annotations.Model;
import annotations.PrimaryKey;
import annotations.ForeignKey;
import storages.Actions;

@Model(tableName = "client", primaryKey = "id")
public class Client {
	
	@PrimaryKey
	private int id;

	@Column(fieldName = "surname")
	private String surname;
	
	@Column(fieldName = "name")
	private String name;
	
	@Column(fieldName = "isgirl")
	private String isGirl;

	@Column(fieldName = "test_id")
	@ForeignKey(entity = "TestModel", column = "id", onDelete = Actions.CASCADE)
	private int testId;

	public Client() {

	}
	
	public Client(String surname) {
		this.surname = surname;
	}
	
	public Client(int id) {
		this.id = id;
	}

	public Client(int id, String surname, String name, String isGirl) {
		super();
		this.id = id;
		this.surname = surname;
		this.name = name;
		this.isGirl = isGirl;
	}

	public Client(String surname, String name, String isGirl) {
		super();
		this.surname = surname;
		this.name = name;
		this.isGirl = isGirl;
	}

	public Client(String surname, String name, String isGirl, int testId) {
		this.surname = surname;
		this.name = name;
		this.isGirl = isGirl;
		this.testId = testId;
	}
}
