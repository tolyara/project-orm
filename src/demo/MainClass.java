package demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import models.Client;
import models.TestModel;
import storages.DataStorage;
import storages.MyORM;

/**
 * Class demonstrates the job with project main entities
 */
public class MainClass {

	public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	private static final String VERSION = "beta version";
	private static final MyORM myORM = new MyORM (POSTGRESQL_DRIVER);
	private static final MyORM myORMwithConnectionPool = new MyORM();
	
<<<<<<< HEAD
	private static final Client CLIENT = new Client("Melchenko", "Anatoliy", "false");
=======
	private static final TestModel TEST_MODEL_1 = new TestModel("value124");
	private static final TestModel TEST_MODEL_2 = new TestModel(6);
	private static final Client CLIENT = new Client("Petrov", "Petr", "false");
>>>>>>> refs/heads/connections

	private static final int ITERATION_NUMBER = 10_000;

	public static void main(String[] args) throws IOException {

		printHeader();		
<<<<<<< HEAD
		doTest();
=======
		doDemo();
>>>>>>> refs/heads/connections
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

<<<<<<< HEAD
	private static void doTest() {
		myORM.createTable(Client.class);
		myORM.createRecordInTable(CLIENT);
=======
	private static void doDemo() {
		myORM.createTable(TestModel.class);
//		System.out.println(myORM.сreateRecordInTable(CLIENT));

//		myORM.deleteRecordInTableByPK(Client.class, 7);
//		myORM.deleteTable(TestModel.class);
//		myORM.testDeleteRecordFromDB(testModel2);
>>>>>>> refs/heads/connections
	}

	/*
	 * Method checks time of execution various ways of connection
	 */
	private static void checkProcuctivity() { 

		long start = System.currentTimeMillis();
		for (int i = 0; i < ITERATION_NUMBER; i++) {
			myORM.updateDataOnTestTable("test", 4, "value");
		}
		long finish = System.currentTimeMillis();
		System.out.println("Finished in : " + (finish - start) + " millis");

		start = System.currentTimeMillis();
		for (int i = 0; i < ITERATION_NUMBER; i++) {
			myORMwithConnectionPool.updateDataOnTestTable("test", 4, "value");
		}
		finish = System.currentTimeMillis();
		System.out.println("Finished in : " + (finish - start) + " millis");

	}

}
