package com.orderwire.data;

import com.orderwire.data.DataConnection;
import com.orderwire.logging.UtilLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDatabaseConnection {


    public static void main(String[] args) {

        Boolean testDB = testDatabaseConnection();
        System.out.println("pause");

    }

    public static Boolean testDatabaseConnection() {
        Integer activeTaskCountDelay = -1;
        Boolean statusProc = true;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;

        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();

        try{
            pStmt = newConn.prepareStatement("SELECT parameter_value FROM ow_orderwire_parameters WHERE parameter_name = 'ActiveTaskCountDelay'");
            rSet = pStmt.executeQuery();
            if ( rSet.next() ){
                activeTaskCountDelay = rSet.getInt("parameter_value");
            }

        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus("TestDatabaseConnection", "Test", "testDatabaseConnection", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus("TestDatabaseConnection", "Test", "testDatabaseConnection", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }


        return statusProc;
    }

}
