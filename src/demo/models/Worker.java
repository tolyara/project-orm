package demo.models;

import annotations.Column;
import annotations.Model;
import annotations.PrimaryKey;

@Model(tableName = "worker", primaryKey = "worker_id")
public class Worker {
	
	@PrimaryKey
	private int id;

	@Column(fieldName = "worker_surname")
	private String surname;
	
	@Column(fieldName = "worker_address")
	private boolean hasAddress;
	
	@Column(fieldName = "worker_salary")
	private double salary;
	
	private int someFieldWithoutAnnotation;

	public Worker(int id, String surname, boolean address) {
		super();
		this.id = id;
		this.surname = surname;
		this.hasAddress = address;
	}

	public Worker(int id) {
		super();
		this.id = id;
	}

	public Worker() {
		super();
	}	

	public Worker(int id, String surname, boolean hasAddress, double salary) {
		super();
		this.id = id;
		this.surname = surname;
		this.hasAddress = hasAddress;
		this.salary = salary;
	}

	public Worker(String surname, boolean address) {
		super();
		this.surname = surname;
		this.hasAddress = address;
	}

	public int getId() {
		return id;
	}

	public String getSurname() {
		return surname;
	}

	public boolean getAddress() {
		return hasAddress;
	}

	@Override
	public String toString() {
		return "Worker [id=" + id + ", surname=" + surname + ", hasAddress=" + hasAddress + ", salary=" + salary + "]";
	}

}
