package demo.models;

import annotations.Column;
import annotations.Model;

@Model(tableName = "worker", primaryKey = "worker_id")
public class Worker {
	
	private int worker_id;

	@Column(fieldName = "worker_surname")
	private String surname;
	
	@Column(fieldName = "worker_address")
	private String address;

	public Worker(int number, String surname, String address) {
		super();
		this.worker_id = number;
		this.surname = surname;
		this.address = address;
	}

	public Worker(int number) {
		super();
		this.worker_id = number;
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
		return worker_id;
	}

	public String getSurname() {
		return surname;
	}

	public String getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return "Worker [id=" + worker_id + ", surname=" + surname + ", address=" + address + "]";
	}
	
	

}
