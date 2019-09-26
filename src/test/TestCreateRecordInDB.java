package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.*;

import org.junit.Before;
import org.junit.Test;

import demo.MainClass;
import storages.MyORM;
import annotations.*;

public class TestCreateRecordInDB {
	
	private MyORM myORM;

	@Before
	public void setUp() throws Exception {
		myORM = new MyORM(MainClass.POSTGRESQL_DRIVER);
	}

	@Test
	public void testCreate() {

		final String QUERY_SELECT = "SELECT * FROM test;";
		int sizeBefore = 0;
		int sizeAfter = 0;

		try (final Statement statement = myORM.getConnection().createStatement();
				final ResultSet rs = statement.executeQuery(QUERY_SELECT)) {
			// sizeBefore = rs.getFetchSize();
			while (rs.next()) {
				// System.out.println(rs.getInt("test_id"));
				sizeBefore++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		myORM.testCreateRecordInDB("testValue1");

		try (final Statement statement2 = myORM.getConnection().createStatement();
				final ResultSet rs = statement2.executeQuery(QUERY_SELECT)) {
			while (rs.next()) {
				// System.out.println(rs.getInt("test_id"));
				sizeAfter++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println(sizeBefore + " " + sizeAfter);
		assertEquals(sizeAfter, (sizeBefore + 1));

	}

}
