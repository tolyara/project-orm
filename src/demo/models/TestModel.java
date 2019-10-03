package demo.models;

import annotations.Column;
import annotations.ForeignKey;
import annotations.Model;
import annotations.PrimaryKey;
import storages.Actions;

import java.util.Date;

@Model(tableName = "test", primaryKey = "id")
public class TestModel {

	@PrimaryKey
	private int id;

	@Column(fieldName = "test_field")
	private String field;

	@Column(fieldName = "test_sex")
	private boolean sex;

	@Column(fieldName = "birth_date")
	private Date birthDate;

	@Column(fieldName = "age")
	private int age;

	@Column(fieldName = "client_id")
	@ForeignKey(entity = "Client", column = "id", onUpdate = Actions.CASCADE, onDelete = Actions.CASCADE)
	private int clientId;


	public TestModel() {
	}

	public TestModel(String field) {
		this.field = field;
	}

	public TestModel(int id) {
		this.id = id;
	}	

	public TestModel(String field, Date birthDate) {
		super();
		this.field = field;
		this.birthDate = birthDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
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
		TestModel other = (TestModel) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		return true;
	}

	public TestModel(String field, int age){
		this.field = field;
		this.age = age;
	}

}

