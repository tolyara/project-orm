package demo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import models.Client;
import models.Worker;
import storages.MyORM;
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
		 myORM.createTable(Worker.class); 
//		myORM.deleteTable(Worker.class);
		printReceivedObjects(myORM.readAllDataFromTable(Worker.class));

	}

	private static void printReceivedObjects(List<Object> objects)
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
		myORM.createRecordInTable(new Worker(-1, "Nut", "Nut"));
//		myORM.close();
		myORM.updateRecordInTable(new Worker(2, "456", "123"));
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
			myORM.createRecordInTable(CLIENT);
		}
		long finish = System.currentTimeMillis();
		System.out.println("Finished in : " + (finish - start) + " millis");

		start = System.currentTimeMillis();
		for (int i = 0; i < ITERATION_NUMBER; i++) {
			myORMwithConnectionPool.createRecordInTable(CLIENT);
		}
		finish = System.currentTimeMillis();
		System.out.println("Finished in : " + (finish - start) + " millis");

	}

}