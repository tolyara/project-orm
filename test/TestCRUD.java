package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import SQL.EntityDAO;
import demo.MainClass;
import demo.models.ImmutableWorker;
import demo.models.TestModel;
import storages.Entity;
import storages.MyConnection;
import storages.Table;

public class TestCRUD {
	
	private MyConnection connection;
	private ImmutableWorker iWorker;
	private EntityDAO entityDAO;
	
	@Before
	public void setUp() throws Exception {
		Table.createTableFromEntity(new Entity(TestModel.class));
		connection = new MyConnection(MainClass.POSTGRESQL_DRIVER);
		iWorker = new ImmutableWorker(-1, "aaa1", true, 500.5);
		entityDAO = EntityDAO.getInstance();
	}

//	@Test
//	public void testCreate() {   
//		int id = Table.createRecordInTable(new Entity(iWorker));
//		ImmutableWorker workerFromDB = (ImmutableWorker) entityDAO.selectEntityById(ImmutableWorker.class, id);
//		assertEquals(workerFromDB.getId(), id);
//		assertEquals(workerFromDB.getSurname(), "aaa1");  
//	}
		
}
