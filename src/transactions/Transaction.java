package transactions;

import storages.MyORM;


import java.sql.Connection;
import java.sql.SQLException;

public class Transaction implements ITransaction {

    private static Connection connection;


    @Override
    public Connection openConnection() {
        if (connection == null) {
            try {
                connection = MyORM.getConnection();
                disableAutoCommit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    private void disableAutoCommit(){
        try {
             connection.setAutoCommit(false);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void enableAutoCommit(){
        try {
            connection.setAutoCommit(true);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void commit() {
        try {
            if (connection.getAutoCommit() == false) {
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollback() {
        try {
            if(connection.getAutoCommit() == false){
                connection.rollback();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public Connection close() {
        try {
            if(!connection.getAutoCommit())
                enableAutoCommit();

            if (!connection.isClosed() && connection != null) {
                connection.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }
}
