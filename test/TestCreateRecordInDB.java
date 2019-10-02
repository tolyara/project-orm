package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import demo.MainClass;
import demo.models.Worker;
import storages.Entity;
import storages.MyConnection;
import storages.Table;

public class TestCreateRecordInDB {
	
	private MyConnection myORM;
	private Worker worker;

	@Before
	public void setUp() throws Exception {
		myORM = new MyConnection(MainClass.POSTGRESQL_DRIVER);
		worker = new Worker("qwerty", "zxc");
	}

	@Test
	public void testCreate() {   
		int id = Table.createRecordInTable(new Entity(worker));
		Worker workerFromDB = new Worker();
//		workerFromDB = (Worker) myORM.getRecordById(Worker.class, id);
		assertEquals(worker.getNumber(), workerFromDB.getNumber());
		assertEquals(worker.getAddress(), workerFromDB.getAddress());
	}
		
}
