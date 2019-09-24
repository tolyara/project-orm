package storages;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.postgresql.jdbc.*;

import annotations.DBField;
import annotations.DBModel;
import models.TestModel;

//TODO - возможность передать в запрос имя табл. +++

import service.Settings;

/**
 * Class implements interaction with database
 */
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
			this.connection = PGConnectionPool.getInstance().getConnection();
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

	public void testCreateRecordInDB(TestModel testModel) {

		// Annotation[] annotations = testModel.getClass().getAnnotations();
		// for (Annotation annotation : annotations) {
		// System.out.println(annotation);
		// }
		// Class tmClass = TestModel.class;

		// Method[] methods = testModel.getClass().getMethods();
		// for (Method method : methods) {
		// System.out.println(method);
		// }

		DBModel modelAnnotation = testModel.getClass().getAnnotation(DBModel.class);
		final String TABLE_NAME = modelAnnotation.tableName().trim();
		System.out.println(TABLE_NAME);

		String fieldName = "<null>";
		String fieldValue = "<null>";
		try {
			Field parsedField = testModel.getClass().getDeclaredField("field");

			DBField fieldAnnotation = parsedField.getAnnotation(DBField.class);
			fieldName = fieldAnnotation.fieldName().trim();
			System.out.println(fieldName);

			parsedField.setAccessible(true);
			fieldValue = ((String) parsedField.get(testModel)).trim();
			System.out.println(fieldValue);

		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}

		final String QUERY_CREATE_ON_TABLE = "INSERT INTO " + TABLE_NAME + "(" + fieldName + ")" + " VALUES (?);";

		// PgPreparedStatement pg;

		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_CREATE_ON_TABLE)) {
//			System.out.println(statement.getClass());
//			statement.setString(1, fieldName);
			statement.setString(1, fieldValue);
			
//			statement.setString(2, "'" + fieldValue + "'");			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateData(String tableName, int elementId, String newElementName) {
		final String QUERY_UPDATE_ON_TABLE = "UPDATE " + tableName
				+ " AS test SET test_field = ? WHERE test.test_id = ?;";
		try (final PreparedStatement statement = this.connection.prepareStatement(QUERY_UPDATE_ON_TABLE)) {
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
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
