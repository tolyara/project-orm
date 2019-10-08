package demo.models;

import annotations.Column;
import annotations.ManyToMany;
import annotations.Model;
import annotations.PrimaryKey;

import java.util.*;

@Model(tableName = "worker", primaryKey = "id")
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

	@ManyToMany(table = "client")
	private Set<Client> clients = new HashSet<>();

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
	
	

	public Worker(int id, String surname, boolean hasAddress, double salary, int someFieldWithoutAnnotation) {
		super();
		this.id = id;
		this.surname = surname;
		this.hasAddress = hasAddress;
		this.salary = salary;
		this.someFieldWithoutAnnotation = someFieldWithoutAnnotation;
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

	public Set<Client> getClients() {
		return clients;
	}

	public void setClients(Set<Client> clients) {
		this.clients = clients;
	}

	@Override
	public String toString() {
		return "Worker [id=" + id + ", surname=" + surname + ", hasAddress=" + hasAddress + ", salary=" + salary + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Worker worker = (Worker) o;
		return id == worker.id &&
				hasAddress == worker.hasAddress &&
				Double.compare(worker.salary, salary) == 0 &&
				someFieldWithoutAnnotation == worker.someFieldWithoutAnnotation &&
				surname.equals(worker.surname) &&
				clients.equals(worker.clients);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, surname, hasAddress, salary, someFieldWithoutAnnotation, clients);
	}
}
