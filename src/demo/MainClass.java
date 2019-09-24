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

	public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	private static final String VERSION = "beta version";
	private static final MyORM myORM = new MyORM (POSTGRESQL_DRIVER);
	private static final MyORM myORMwithConnectionPool = new MyORM();
	
	private static final TestModel testModel1 = new TestModel("value123");
	private static final TestModel testModel2 = new TestModel(6);

	private static final int ITERATION_NUMBER = 10_000;

	public static void main(String[] args) throws IOException {

		printHeader();
		
//		TestModel testModel = new TestModel("value50");
		
//		doTest();
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

	private static void doTest() {
		myORM.testCreateRecordInDB(testModel1);
//		myORM.testDeleteRecordFromDB(testModel2);
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
