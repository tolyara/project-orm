package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import SQL.EntityDAO;
import demo.MainClass;
import demo.models.ImmutableWorker;
import demo.models.TestModel;
import demo.models.Worker;
import storages.Entity;
import storages.MyConnection;
import storages.Table;

public class TestCRUD {
	
	private MyConnection connection;
	private EntityDAO entityDAO;
	private static ImmutableWorker worker = new ImmutableWorker(-1, "aaa1", true, 500.5);	
	private static int idOfAddedEntity = Table.createRecordInTable(new Entity(worker));;

	@Before
	public void setUp() throws Exception {
		connection = new MyConnection(MainClass.POSTGRESQL_DRIVER);
		worker = new ImmutableWorker(-1, "aaa1", true, 500.5);
		entityDAO = EntityDAO.getInstance();
//		id = Table.createRecordInTable(new Entity(worker));
	}

	@Test 
	public void testCreate() { 
//		idOfAddedEntity = Table.createRecordInTable(new Entity(worker));
		ImmutableWorker workerFromDB = (ImmutableWorker) entityDAO.selectEntityById(new Entity(ImmutableWorker.class), idOfAddedEntity).getEntityObject();
		assertEquals(workerFromDB.getId(), idOfAddedEntity);
		assertEquals(workerFromDB.getSurname(), "aaa1");
		System.out.println(idOfAddedEntity);
//		entityDAO.deleteRecordInTableByPK(new Entity(new ImmutableWorker(id,"test_update", false, 1000.7)));
//		Object workerFromDBafter = entityDAO.selectEntityById(ImmutableWorker.class, id);
//		assertNotEquals(workerFromDB.getClass(), workerFromDBafter.getClass());
	}

	@Test
	public void testUpdate() {  
		System.out.println(idOfAddedEntity);
		ImmutableWorker workerFromDB_1 = (ImmutableWorker) entityDAO.selectEntityById(new Entity(ImmutableWorker.class), idOfAddedEntity).getEntityObject();
		String workerSurnameBeforeUpdate = workerFromDB_1.getSurname();
		entityDAO.updateRecordInTable(new Entity(new ImmutableWorker(idOfAddedEntity,"test_update", false, 1000.7)));
		ImmutableWorker workerFromDB_2 = (ImmutableWorker) entityDAO.selectEntityById(new Entity(ImmutableWorker.class), idOfAddedEntity).getEntityObject();
		String workerSurnameAfterUpdate = workerFromDB_2.getSurname();
		assertNotEquals(workerSurnameAfterUpdate, workerSurnameBeforeUpdate);
		assertEquals(workerSurnameAfterUpdate, "test_update");
	}
	
	@After
	public void tearDown() throws Exception {
		connection.close();
	}
	
//	class ID {
//		
//		private int id;
//		
//		
//		
//	}
		
}
