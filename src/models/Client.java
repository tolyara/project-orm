package models;

import annotations.DBField;
import annotations.DBModel;

@DBModel(tableName = "Client", primaryKey = "id")
public class Client {
	
	@DBField (fieldName = "id", isAutoIncrement = true)
	private int id;

	@DBField (fieldName = "surname")
	private String surname;
	
	@DBField (fieldName = "name")
	private String name;
	
	@DBField (fieldName = "isgirl")
	private String isGirl;

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
