package storages;

import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.ds.PGPoolingDataSource;

import service.Settings;

public class PGConnectionPool {
	
	@SuppressWarnings("deprecation")
	private PGPoolingDataSource source;
	
	private PGConnectionPool() {
		final Settings settings = Settings.getInstance();
		source = new PGPoolingDataSource();
        source.setDataSourceName("A Data Source");
        source.setServerName(settings.getValue("postgres.host"));
        source.setDatabaseName(settings.getValue("postgres.database"));
        source.setUser(settings.getValue("postgres.username"));
        source.setPassword(settings.getValue("postgres.password"));
        source.setMaxConnections(30); //Максимальное значение
        source.setInitialConnections(20); //Сколько соединений будет сразу открыто
	}

	private static PGConnectionPool instance = null;

	public static PGConnectionPool getInstance() {
		if (instance == null)
			instance = new PGConnectionPool();
		return instance;
	}

	public Connection getConnection() throws SQLException {
		return source.getConnection();
	}

}
