package demo.models;

import annotations.Column;
import annotations.Model;

@Model(tableName = "worker", primaryKey = "worker_id")
public class Worker {
	
	@Column(fieldName = "worker_id")
	private int id;

	@Column(fieldName = "worker_surname")
	private String surname;
	
	@Column(fieldName = "worker_address")
	private String address;

	public Worker(int number, String surname, String address) {
		super();
		this.id = number;
		this.surname = surname;
		this.address = address;
	}

	public Worker(int number) {
		super();
		this.id = number;
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
		return id;
	}

	public String getSurname() {
		return surname;
	}

	public String getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return "Worker [id=" + id + ", surname=" + surname + ", address=" + address + "]";
	}
	
	

}
