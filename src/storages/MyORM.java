package storages;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//TODO - возможность передать в запрос имя табл.

import service.Settings;

public class MyORM implements DataStorage, AutoCloseable {

	private Connection connection;

	private static final String QUERY_CREATE_TABLE = "CREATE TABLE ? ( test_id serial PRIMARY KEY, test_field VARCHAR(20) );";
	private static final String QUERY_DROP_TABLE = "DROP TABLE (?);";
	private static final String QUERY_INSERT = "INSERT INTO test (test_field) VALUES (?);";
	private static final String QUERY_DELETE = "DELETE FROM ?;";
	private static final String QUERY_UPDATE = "UPDATE test AS test SET test_field = ? WHERE test.test_id = ?;";
	private static final String QUERY_DROP_AND_CREATE_TABLE = "DROP TABLE ?;" + "CREATE TABLE ?;";

	public MyORM() {
		try {
			this.connection = ConnectionPool.getInstance().getConnection();
		} catch (SQLException e) {
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

	public void createTable(String tableName) {

		// try (final PreparedStatement statement =
		// this.connection.prepareStatement(QUERY_CREATE_TABLE)) {
		// statement.setString(1, nameTable.trim());
		// statement.executeUpdate();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }

	}

	public void deleteTable(String tableName) {

		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_DROP_TABLE)) {
			statement.setString(1, tableName.trim());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void createData(String data) {
		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_INSERT)) {
			statement.setString(1, data.trim());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateData(String tableName, int elementId, String newElementName) {
		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_UPDATE)) {
			// statement.setObject(1, tableName);
			statement.setString(1, newElementName.trim());
			statement.setInt(2, elementId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteAllData(String tableName) {
		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_DELETE)) {
			statement.setString(1, tableName.trim());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws Exception {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
