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
	
	

	public ImmutableWorker() {
		super();
		this.id = 0;
		this.surname = "";
		this.hasAddress = false;
		this.salary = 0;
	}

	public ImmutableWorker(int id, String surname, boolean hasAddress, double salary) {
		super();
		this.id = id;
		this.surname = surname;
		this.hasAddress = hasAddress;
		this.salary = salary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImmutableWorker other = (ImmutableWorker) obj;
		if (id != other.id)
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public String getSurname() {
		return surname;
	}	
	
	
	
}
