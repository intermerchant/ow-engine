package com.orderwire.data;

import com.orderwire.logging.UtilLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataConnection {

    public DataConnection(){   
    }
    
    
    public Connection getOrderwireConnection() {
        Connection mysqlConn = null;
        //String jdbcUrl = "jdbc:mysql://162.254.66.177:3306/ow?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=CST6CDT";
        // String jdbcUrl = "jdbc:mysql://192.168.0.245:3306/ow?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=MST7MDT";

        String jdbcUrl = "jdbc:mysql://localhost:3306/ow?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=CST6CDT";
        String userId = "owuser";
        String passcode = "Detr4$detr";
        Integer retryCount = 6;
        boolean connectionMade = false;
        do{
            try {
                //Class.forName ("com.mysql.cj.jdbc.Driver").newInstance();
                mysqlConn = DriverManager.getConnection(jdbcUrl, userId, passcode);
                connectionMade = true;
            } catch ( SQLException sqlEx) {
                sqlEx.printStackTrace();

                String sqlState = sqlEx.getSQLState();
                if(sqlState.equals("08S01")){
                    try {
                        Thread.sleep(9050);
                    } catch (InterruptedException ex) {}
                    UtilLogger.setGenericStatus("owDataConnection.log", " | DataConnection | getOrderwireConnection | SQL State | " + sqlEx.getSQLState());   
                    UtilLogger.setGenericStatus("owDataConnection.log", " | DataConnection | getOrderwireConnection | SQL State Retry Count | " + retryCount);
                    retryCount--;
                }else{
                    UtilLogger.setGenericStatus("owDataConnection.log", " | DataConnection | getOrderwireConnection | SQL State | " + sqlEx.getSQLState()); 
                    UtilLogger.setGenericStatus("owDataConnection.log", " | DataConnection | getOrderwireConnection | SQL Exception | " + sqlEx.getMessage() );                    
                }   
            }
        }while(!connectionMade && retryCount > 0);
        if (!connectionMade && retryCount.equals(0)){
            UtilLogger.setGenericStatus("owDataConnection.log", " | DataConnection | getOrderwireConnection | SQL Exception | FATAL | 6 Tries and Fail" );                    
        }        
        return mysqlConn;
    }    
    
 
}
