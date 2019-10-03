package demo.models;

import annotations.Column;
import annotations.Model;
import annotations.PrimaryKey;

@Model(tableName = "worker", primaryKey = "worker_id")
public class ImmutableWorker {

	@PrimaryKey
	private final int id;

	@Column(fieldName = "worker_surname")
	private final String surname;
	
	@Column(fieldName = "worker_address")
	private final boolean hasAddress;
	
	@Column(fieldName = "worker_salary")
	private final double salary;

	public ImmutableWorker(int id, String surname, boolean hasAddress, double salary) {
		super();
		this.id = id;
		this.surname = surname;
		this.hasAddress = hasAddress;
		this.salary = salary;
	}	
	
}
