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
	private static final PrintStream myOut = MyOut.getInstance().getOut();
	private static final MyORM myORM = new MyORM();

	public static void main(String[] args) throws IOException {

		printHeader();
//		myORM.createData("value1");
//		myORM.deleteTable("test");
		myORM.updateData(3, "value3");
//		myORM.deleteAllData("test");

	}

	private static void printHeader() {		
		myOut.println("PROJECT MY-ORM : " + VERSION);
	}
	
	private static void doTest() {
//		myORM.createTable("TestTable");
//		myORM.deleteTable("TestTable");
	}

}
