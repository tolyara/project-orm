package demo.models;

import annotations.Column;
import annotations.Model;
import annotations.PrimaryKey;

@Model(tableName = "for_test", primaryKey = "test_id")
public class ForTest {

	@PrimaryKey
	private int id;

	@Column(fieldName = "test_surname")
	private String surname;

	@Column(fieldName = "test_address")
	private String hasAddress;

	@Column(fieldName = "test_salary")
	private double salary;

	public ForTest(int id, String surname, String hasAddress, double salary) {
		super();
		this.id = id;
		this.surname = surname;
		this.hasAddress = hasAddress;
		this.salary = salary;
	}	

}