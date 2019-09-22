package demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import service.MyOut;

public class MainClass {

	private static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	private static final String VERSION = "beta version";
	private static final PrintStream myOut = MyOut.getInstance().getOut();

	public static void main(String[] args) throws IOException {

		printHeader();

	}

	private static void printHeader() {
		
		myOut.println("PROJECT MY-ORM : " + VERSION);
//		FileOutputStream

	}

}
