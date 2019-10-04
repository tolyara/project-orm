package storages;

import java.sql.*;
import java.util.*;

import annotations.Model;
import connections.DatabaseTypeManager;
import demo.MainClass;
import service.Settings;

/**
 * Class implements interaction with database
 */
public class MyConnection implements AutoCloseable {

	private Connection connection;

	public Connection getConnection() {
		return connection;
	}

	public MyConnection() {
		setConnectionPool();		
	}

	public MyConnection(boolean isConnectionPool) {
		final Settings settings = Settings.getInstance();
		HashMap<String, String> databaseSettings = DatabaseTypeManager.getDatabaseSettings();
		if (isConnectionPool) {
			setConnectionPool();
		}
		else {
			try {
				Class.forName(settings.getValue(databaseSettings.get("driver")));
				this.connection = DriverManager.getConnection(settings.getValue(databaseSettings.get("url")),
						settings.getValue(databaseSettings.get("username")), settings.getValue(databaseSettings.get("password")));
			} catch (SQLException e) {
				throw new IllegalStateException(e);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
//		try {
//			Class.forName(driver);
//			this.connection = DriverManager.getConnection(settings.getValue("postgres.url"),
//					settings.getValue("postgres.username"), settings.getValue("postgres.password"));
//		} catch (SQLException e) {
//			throw new IllegalStateException(e);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
	}
	
	private void setConnectionPool() {
		try {
			this.connection = PGConnectionPool.getInstance().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
