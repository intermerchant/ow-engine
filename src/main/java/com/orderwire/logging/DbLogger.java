package com.orderwire.logging;

import com.orderwire.data.DataConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


public class DbLogger {
    private final String _threadName;
    private final String _packageName;
    private final String _packageMethod;
    private final String _errorClass;
    private final String _processMethod;
    private final String _processMethodValue;
    
    public DbLogger(HashMap logMap){
        _threadName = String.valueOf(logMap.get("_threadName"));
        _packageName = String.valueOf(logMap.get("_packageName"));
        _packageMethod = String.valueOf(logMap.get("_packageMethod"));
        _errorClass = String.valueOf(logMap.get("_errorClass"));
        _processMethod = String.valueOf(logMap.get("_processMethod"));
        _processMethodValue = String.valueOf(logMap.get("_processMethodValue"));
    }

    public void writeProcesLog(){
        Boolean procStatus = true;
        Integer pkId = -1;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();
        String threadName = "";
        
        try{
            pStmt = newConn.prepareStatement("INSERT INTO ow_orderwire_process_logs (thread_name, package_name, package_method, error_class, process_message, process_message_value) " 
                + "VALUES(?,?,?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
            
            pStmt.setString(1, _threadName);
            pStmt.setString(2, _packageName);
            pStmt.setString(3, _packageMethod);
            pStmt.setString(4, _errorClass);
            pStmt.setString(5, _processMethod);
            pStmt.setString(6, _processMethodValue);
            pStmt.executeUpdate();                
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                pkId = rSet.getInt(1);
            } else {
                UtilLogger.setGenericStatus("owDbLogger.log", " | DbLogger | writeProcesLog | No Generated Key ");
            }              
            
        } catch (SQLException sqle) {
            UtilLogger.setGenericStatus("owDbLogger.log", " | DbLogger | writeProcesLog | SQL Exception | " + sqle.getMessage() );
        } catch (Exception exce){
            UtilLogger.setGenericStatus("owDbLogger.log", " | DbLogger | writeProcesLog | Exception | " + exce.getMessage() );
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}                
            } catch (SQLException e){ }
        }

    }
}
