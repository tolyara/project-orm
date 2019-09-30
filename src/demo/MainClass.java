package demo;

import java.lang.reflect.Field;
import java.util.List;

import SQL.EntityDAO;
import demo.models.Client;
import demo.models.TestModel;
import demo.models.Worker;
import storages.Entity;
import storages.MyORM;
import storages.Table;
import transactions.Transaction;

/**
 * Class demonstrates the job with project main entities
 */
public class MainClass {

	public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	private static final String VERSION = "beta version";
	public static final MyORM myORM = new MyORM(POSTGRESQL_DRIVER);
	private static final MyORM myORMwithConnectionPool = new MyORM();

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
		myORM.close();
		myORMwithConnectionPool.close();
	}

	private static void printHeader() {
		System.out.println("PROJECT MY-ORM : " + VERSION);
	}

	private static void doDemo() throws Exception {
		 Table.createTableFromEntity(new Entity(Worker.class));
//		myORM.deleteTable(Worker.class);
		printReceivedObjects(Table.readAllDataFromTable(new Entity(Worker.class)));
		Table.createRecordInTable(new Entity(new Worker(12, "test", "ddd")));
		Table.createTableFromEntity(new Entity(Client.class));
		Table.createTableFromEntity(new Entity(TestModel.class));
		Table.deleteEntityTable("test");
		EntityDAO.getInstance().updateEntity(new Entity(new Worker(12, "super_test", "ddww")));
	}

	private static void printReceivedObjects(List<Entity> objects)
			throws IllegalArgumentException, IllegalAccessException {
		for (Object o : objects) {
			for (Field field : o.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				System.out.printf("%14s", field.get(o));
			}
			System.out.println();
		}

	}

	private static void tryTransaction() throws Exception {
		Transaction tx = new Transaction();
		tx.openConnection();
		Table.createRecordInTable(new Entity(new Worker(-1, "Nut", "Nut")));
//		myORM.close();
		//myORM.updateRecordInTable(new Worker(2, "456", "123"));
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