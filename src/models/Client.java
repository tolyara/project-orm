package models;

import annotations.DBField;
import annotations.DBModel;

@DBModel(tableName = "Client", primaryKey = "id")
public class Client {
	
	@DBField (fieldName = "client_id", isAutoIncrement = true)
	private int id;

	@DBField (fieldName = "client_surname")
	private String surname;
	
	@DBField (fieldName = "client_name")
	private String name;
	
	@DBField (fieldName = "client_is_girl")
	private boolean isGirl;

	public Client() {

	}
	
	public Client(String surname) {
		this.surname = surname;
	}
	
	public Client(int id) {
		this.id = id;
	}

	public Client(int id, String surname, String name, boolean isGirl) {
		super();
		this.id = id;
		this.surname = surname;
		this.name = name;
		this.isGirl = isGirl;
	}	

}
