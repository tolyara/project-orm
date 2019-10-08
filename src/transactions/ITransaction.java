package transactions;

import java.sql.Connection;

public interface ITransaction {
	
    public Connection getConnection();

    public Connection close();

    public void setConnection(Connection connection);

    public void commit();

    public void rollback();


}
