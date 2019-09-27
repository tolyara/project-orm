package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.*;

import org.junit.Before;
import org.junit.Test;

import demo.MainClass;
import models.Worker;
import storages.MyORM;
import annotations.*;

public class TestCreateRecordInDB {
	
	private MyORM myORM;
	private Worker worker;

	@Before
	public void setUp() throws Exception {
		myORM = new MyORM(MainClass.POSTGRESQL_DRIVER);
		worker = new Worker("qwerty", "zxc");
	}

	@Test
	public void testCreate() {
		int id = myORM.createRecordInTable(worker);		
		Worker workerFromDB = new Worker();
		workerFromDB = (Worker) myORM.getRecordById(Worker.class, id);
		assertEquals(worker.getNumber(), workerFromDB.getNumber());
		assertEquals(worker.getAddress(), workerFromDB.getAddress());
	}
		
}
