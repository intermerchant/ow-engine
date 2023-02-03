package com.orderwire.data;

import com.orderwire.logging.UtilLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataShutdownEngine {

    public DataShutdownEngine(){}


    public Boolean shutdownEngine(){
        Boolean shutdownProc = true;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();
        String owThreadName = "Shutdown Thread";

        try {
            pStmt = newConn.prepareStatement("UPDATE ow_orderwire_parameters SET parameter_value = ? WHERE parameter_name = ?");
            pStmt.setString(1, "SHUTDOWN" );
            pStmt.setString(2, "propRunCycle" );
            Integer rowsAffected = pStmt.executeUpdate();

        } catch (SQLException sqle) {
            shutdownProc = false;
            UtilLogger.setDbStatus(owThreadName, "DataShutdownEngine", "shutdownEngine", "SQLException", sqle.getMessage(), "");
        } catch (Exception exce){
            shutdownProc = false;
            UtilLogger.setDbStatus(owThreadName, "DataShutdownEngine", "shutdownEngine", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }

        return shutdownProc;
    }


}
