package service;

import java.io.*;

public class MyOut {

	private static final PrintStream out = System.out;

	private MyOut() {
		
	}

	/**
	 * SingletonHolder is loaded on the first execution of MyOut.getInstance() or
	 * the first access to OutHolder.INSTANCE, not before.
	 */
	private static class OutHolder {
		private static final MyOut INSTANCE = new MyOut();
	}

	public static MyOut getInstance() {
		return OutHolder.INSTANCE;
	}
	
	public PrintStream getOut() {
		return out;
	}

}
