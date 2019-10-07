package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import connections.MyConnection;
import demo.MainClass;
import demo.models.ImmutableWorker;
import demo.models.TestModel;
import demo.models.Worker;
import sql.EntityDAO;
import storages.Entity;
import storages.Table;

public class TestCRUD {
	
	private MyConnection connection;
	private EntityDAO entityDAO;
	private static ImmutableWorker worker = new ImmutableWorker(-1, "TestWorker", true, 500.5); 	

	@Before
	public void setUp() throws Exception {
		connection = new MyConnection(false);
		entityDAO = EntityDAO.getInstance(); 
	}

	@Test 
	public void testCreate() { 
		int idOfAddedEntity = Table.createRecordInTable(new Entity(worker)); 
		ImmutableWorker workerFromDB = (ImmutableWorker) entityDAO.selectEntityById(new Entity(ImmutableWorker.class), idOfAddedEntity).getEntityObject();
		assertEquals(workerFromDB.getId(), idOfAddedEntity);
		assertEquals(workerFromDB.getSurname(), "TestWorker");
		entityDAO.deleteRecordInTableByPK(new Entity(new ImmutableWorker(idOfAddedEntity, "", false, 0)));
		connection.close();
	} 

	@Test
	public void testUpdate() {  
		int idOfAddedEntity = Table.createRecordInTable(new Entity(worker));
		ImmutableWorker workerFromDB_1 = (ImmutableWorker) entityDAO.selectEntityById(new Entity(ImmutableWorker.class), idOfAddedEntity).getEntityObject();
		String workerSurnameBeforeUpdate = workerFromDB_1.getSurname();
		entityDAO.updateRecordInTable(new Entity(new ImmutableWorker(idOfAddedEntity,"test_update", false, 1000.7)));
		ImmutableWorker workerFromDB_2 = (ImmutableWorker) entityDAO.selectEntityById(new Entity(ImmutableWorker.class), idOfAddedEntity).getEntityObject();
		String workerSurnameAfterUpdate = workerFromDB_2.getSurname();
		assertNotEquals(workerSurnameAfterUpdate, workerSurnameBeforeUpdate);
		assertEquals(workerSurnameAfterUpdate, "test_update");
		entityDAO.deleteRecordInTableByPK(new Entity(new ImmutableWorker(idOfAddedEntity, "", false, 0)));
		connection.close();
	}
	
	@Test
	public void testDelete() {
		int idOfAddedEntity = Table.createRecordInTable(new Entity(worker));
		entityDAO.deleteRecordInTableByPK(new Entity(new ImmutableWorker(idOfAddedEntity, "", false, 0)));
		ImmutableWorker workerFromDB = (ImmutableWorker) entityDAO.selectEntityById(new Entity(ImmutableWorker.class), idOfAddedEntity).getEntityObject();
		assertNull(workerFromDB);
		connection.close();
	}
	
	@After
	public void tearDown() throws Exception {
		
	}	
		
}
