package demo;

import java.io.IOException;

import models.Client;
import storages.MyORM;

/**
 * Class demonstrates the job with project main entities
 */
public class MainClass {

	public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	private static final String VERSION = "beta version";
	private static final MyORM myORM = new MyORM (POSTGRESQL_DRIVER);
	private static final MyORM myORMwithConnectionPool = new MyORM();
	
	private static final Client CLIENT = new Client("Petrov", "Petr", "false");
	private static final Client CLIENT2 = new Client(4, "Sidorova", "Lena", "true");

	private static final int ITERATION_NUMBER = 10;

	public static void main(String[] args) throws IOException {

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

	private static void doDemo() {
//		myORM.createTable(Client.class);
//		int id = myORM.createRecordInTable(CLIENT);
		myORM.updateRecordInTable(CLIENT2);
//		myORM.deleteRecordInTableByPK(Client.class, 9);
//		checkProcuctivity();
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
			myORM.createRecordInTable(CLIENT);
		}
		finish = System.currentTimeMillis();
		System.out.println("Finished in : " + (finish - start) + " millis");

	}

}