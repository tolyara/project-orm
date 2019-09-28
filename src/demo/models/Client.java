package demo.models;

import annotations.Field;
import annotations.Model;

@Model(tableName = "client", primaryKey = "id")
public class Client {
	
	private int id;

	@Field(fieldName = "surname")
	private String surname;
	
	@Field(fieldName = "name")
	private String name;
	
	@Field(fieldName = "isgirl")
	private String isGirl = "<null>";

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

}
