package demo;

import java.lang.reflect.Field;
import java.util.List;

import SQL.EntityDAO;
import demo.models.Client;
import demo.models.ImmutableWorker;
import demo.models.TestModel;
import demo.models.Worker;
import storages.Entity;
import storages.MyConnection;
import storages.Table;
import transactions.Transaction;

/**
 * Class demonstrates the job with project main entities
 */
public class MainClass {

	public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	private static final String VERSION = "beta version";
	public static final MyConnection connection = new MyConnection(POSTGRESQL_DRIVER);
	private static final MyConnection connectionViaConnectionPool = new MyConnection();

	private static final Client CLIENT = new Client("Ivanov", "Ivan", "false");
	private static final Client CLIENT2 = new Client(1, "333", "456", "true");

	private static final int ITERATION_NUMBER = 10;

	public static void main(String[] args) throws Exception {

		printHeader();
		doDemo();
		closeResources();

	}

	/*
	 * Method is needed for closing ORM objects after use
	 */
	private static void closeResources() {
		connection.close();
		connectionViaConnectionPool.close();
	}

	private static void printHeader() {
		System.out.println("PROJECT MY-ORM : " + VERSION);
	}

	private static void doDemo() throws Exception {
		// Table.createTableFromEntity(new Entity(Worker.class));
		
		printReceivedObjects(EntityDAO.getInstance().readAllRecordsOrderedByPK((Worker.class)));

//		 Object en = EntityDAO.getInstance().selectEntityById(Worker.class, 7);
//		 System.out.println(en);

//		 Table.createRecordInTable(new Entity(new ImmutableWorker(12, "tes65", true, 600.5)));
//		Table.createRecordInTable(new Entity(new Worker(12, "test9", false, 999, 9)));
		// Table.createTableFromEntity(new Entity(Client.class));
		// Table.deleteEntityTable("worker");
//		 EntityDAO.getInstance().updateRecordInTable(new Entity(new Worker(10, "super_test4",
//		 false, 1000, 23)));
//		 EntityDAO.getInstance().deleteRecordInTableByPK(new Entity(new Worker(10)));
	}

	private static void printReceivedObjects(List<Object> objects)
			throws IllegalArgumentException, IllegalAccessException {
		for (Object o : objects) {
//			Worker o = (Worker) entity.getEntityObject();
			System.out.println(((Worker) o).getId());
			// for (Field field : o.getClass().getDeclaredFields()) {
			// field.setAccessible(true);
			// System.out.printf("%14s", field.get(o));
			// }
			// System.out.println();
		}
//		System.out.println(objects.size());
	}

	private static void tryTransaction() throws Exception {
		Transaction tx = new Transaction();
		tx.openConnection();
		Table.createRecordInTable(new Entity(new Worker(12, "test1", false, 1200.5)));
		// myORM.close();
		EntityDAO.getInstance().updateRecordInTable(new Entity(new Worker(12, "test1", false, 1200.5)));
		try {
			tx.commit();
		} catch (Throwable e) {
			tx.rollback();
			e.printStackTrace();
		}
	}

	/*
	 * Method checks time of execution various ways of connection
	 */
	private static void checkProcuctivity() {

		long start = System.currentTimeMillis();
		for (int i = 0; i < ITERATION_NUMBER; i++) {
			Table.createRecordInTable(new Entity(CLIENT));
		}
		long finish = System.currentTimeMillis();
		System.out.println("Finished in : " + (finish - start) + " millis");

		start = System.currentTimeMillis();
		for (int i = 0; i < ITERATION_NUMBER; i++) {
			Table.createRecordInTable(new Entity(CLIENT));
		}
		finish = System.currentTimeMillis();
		System.out.println("Finished in : " + (finish - start) + " millis");

	}

}