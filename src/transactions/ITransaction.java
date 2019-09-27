package transactions;

import java.sql.Connection;

public interface ITransaction {
	
    public Connection openConnection();

    public void commit();

    public void rollback();

    public Connection close();

}
