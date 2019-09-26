package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.*;

import org.junit.Before;
import org.junit.Test;

import demo.MainClass;
import storages.MyORM;
import annotations.*;

public class TestCreateRecordInDB {
	
	private MyORM myORM;

	@Before
	public void setUp() throws Exception {
		myORM = new MyORM(MainClass.POSTGRESQL_DRIVER);
	}

	@Test
	public void testCreate() {
		
	}
		
}
