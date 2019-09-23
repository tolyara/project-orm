package demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import service.MyOut;
import storages.DataStorage;
import storages.MyORM;

public class MainClass {

	private static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	private static final String VERSION = "beta version";
//	private static final PrintStream myOut = MyOut.getInstance().getOut();
	private static final MyORM myORM = new MyORM(POSTGRESQL_DRIVER);
	private static final MyORM myORMwithConnectionPool = new MyORM();
	
	private static final int ITERATION_NUMBER = 10_000;

	public static void main(String[] args) throws IOException {

		printHeader();
		checkProcuctivity();
//		myORM.createData("value4");
//		myORM.deleteTable("test");
//		myORM.updateData("test", 4, "value14");
//		myORM.deleteAllData("test");

	}

	private static void printHeader() {		
		System.out.println("PROJECT MY-ORM : " + VERSION);
	}
	
	private static void doTest() {
//		myORM.createTable("TestTable");
//		myORM.deleteTable("TestTable");
	}
	
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
