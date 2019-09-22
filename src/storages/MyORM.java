package storages;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import service.Settings;

public class MyORM implements DataStorage {

	private Connection connection;

	private static final String QUERY_CREATE_DATABASE = "CREATE DATABASE ?;";
	private static final String QUERY_CREATE_TABLE = "CREATE TABLE ?;";
	private static final String QUERY_DROP_AND_CREATE_DATABASE = "DROP DATABASE ?;" + "CREATE DATABASE ?;";
	private static final String QUERY_DROP_AND_CREATE_TABLE = "DROP TABLE ?;" + "CREATE TABLE ?;";

	public MyORM() {
		// final Settings settings = Settings.getInstance();
		try {
			Class.forName("org.postgresql.Driver");
			this.connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/MyORM", "postgres",
					"123321");
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public MyORM(String driver) {
		final Settings settings = Settings.getInstance();
		try {
			Class.forName(driver);
			this.connection = DriverManager.getConnection(settings.getValue("postgres.url"),
					settings.getValue("postgres.username"), settings.getValue("postgres.password"));
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void createDatabase(String nameDB) {
		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_CREATE_DATABASE)) {
			statement.setString(1, nameDB);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createTable(String nameTable) {
		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_CREATE_TABLE)) {
			statement.setString(1, nameTable);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
