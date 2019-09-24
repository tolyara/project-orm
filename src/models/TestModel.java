package models;

import annotations.DBField;
import annotations.DBModel;

@DBModel(tableName = "test")
public class TestModel {
	
	@DBField (fieldName = "test_id", isAutoIncrement = true)
	private int id;

	@DBField (fieldName = "test_field")
	private String field;

	public TestModel() {

	}
	
	public TestModel(String field) {
		this.field = field;
	}

}
