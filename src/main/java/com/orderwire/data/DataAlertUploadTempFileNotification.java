package com.orderwire.data;

import com.orderwire.logging.UtilLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DataAlertUploadTempFileNotification {
    private final String owThreadName;

    public DataAlertUploadTempFileNotification(String _threadName) {
        owThreadName = _threadName;
    }

    public String getUploadTempAlertEmailTo(){
        String emailTo = "";
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        Connection newConn = null;
        DataConnection dc = new DataConnection();

        try {
            newConn = dc.getOrderwireConnection();
            pStmt = newConn.prepareStatement("SELECT parameter_value FROM ow_orderwire_parameters WHERE parameter_name = 'UploadTempAlertEmailTo'");
            rSet = pStmt.executeQuery();
            if ( rSet.next() ){
                emailTo = rSet.getString("parameter_value");
            }

        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataAlertUploadTempFileNotification", "getUploadTempAlertEmailTo", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "DataAlertUploadTempFileNotification", "getUploadTempAlertEmailTo", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }

        return emailTo;
    }



}
