package demo.models;

import annotations.Column;
import annotations.Model;
import annotations.OneToOne;

@Model(tableName = "client", primaryKey = "id")
public class Client {
	//@Column(fieldName = "id")
	private int id;

	@Column(fieldName = "surname")
	private String surname;
	
	@Column(fieldName = "name")
	private String name;
	
	@Column(fieldName = "isgirl")
	private String isGirl;

	@OneToOne(table = "worker", column = "id")
	Worker worker;

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
