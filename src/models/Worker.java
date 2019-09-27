package models;

import annotations.DBField;
import annotations.DBModel;

@DBModel(tableName = "worker", primaryKey = "number")
public class Worker {
	
	@DBField (fieldName = "number", isAutoIncrement = true)
	private int number;

	@DBField (fieldName = "surname")
	private String surname;
	
	@DBField (fieldName = "address")
	private String address;

	public Worker(int number, String surname, String address) {
		super();
		this.number = number;
		this.surname = surname;
		this.address = address;
	}

	public Worker(int number) {
		super();
		this.number = number;
	}

	public Worker() {
		super();
	}

	public Worker(String surname, String address) {
		super();
		this.surname = surname;
		this.address = address;
	}

	public int getNumber() {
		return number;
	}

	public String getSurname() {
		return surname;
	}

	public String getAddress() {
		return address;
	}

}
