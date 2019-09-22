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
		myORM.createTable("testTable");

	}

	private static void printHeader() {
		
		myOut.println("PROJECT MY-ORM : " + VERSION);
//		FileOutputStream

	}

}
