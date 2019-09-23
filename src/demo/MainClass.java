package demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import models.TestModel;
import service.MyOut;
import storages.DataStorage;
import storages.MyORM;

/**
 * Class demonstrates the job with project main entities
 */
public class MainClass {

	private static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	private static final String VERSION = "beta version";
	private static final MyORM myORM = new MyORM (POSTGRESQL_DRIVER);
	private static final MyORM myORMwithConnectionPool = new MyORM();

	private static final int ITERATION_NUMBER = 10_000;

	public static void main(String[] args) throws IOException {

		printHeader();
		// initializeWorkingEntities();
		// checkProcuctivity();
		TestModel testModel = new TestModel();
		 myORM.testCreateData(testModel);
		// myORM.deleteTable("test");
//		myORM.updateData("test", 4, "value111");
		// myORM.deleteAllData("test");
		closeResources();

	}

	/*
	 * Method is needed for closing ORM objects after use
	 */
	private static void closeResources() {
		myORM.close();
		myORMwithConnectionPool.close();
	}

	/*
	 * Method is needed so that ORM objects will be closed automatically
	 */
	// private static void initializeWorkingEntities() {
	// try (final MyORM tempMyORM = new MyORM(POSTGRESQL_DRIVER);
	// final MyORM tempMyORMwithConnectionPool = new MyORM()) {
	// myORM = tempMyORM;
	// myORMwithConnectionPool = tempMyORMwithConnectionPool;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	private static void printHeader() {
		System.out.println("PROJECT MY-ORM : " + VERSION);
	}

	private static void doTest() {
		// myORM.createTable("TestTable");
		// myORM.deleteTable("TestTable");
	}

	/*
	 * Method checks time of execution various ways of connection
	 */
	private static void checkProcuctivity() {

		long start = System.currentTimeMillis();
		for (int i = 0; i < ITERATION_NUMBER; i++) {
			myORM.updateData("test", 4, "value");
		}
		long finish = System.currentTimeMillis();
		System.out.println("Finished in : " + (finish - start) + " millis");

		start = System.currentTimeMillis();
		for (int i = 0; i < ITERATION_NUMBER; i++) {
			myORMwithConnectionPool.updateData("test", 4, "value");
		}
		finish = System.currentTimeMillis();
		System.out.println("Finished in : " + (finish - start) + " millis");

	}

}
