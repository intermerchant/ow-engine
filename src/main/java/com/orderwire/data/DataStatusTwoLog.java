package com.orderwire.data;

import com.orderwire.logging.UtilLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataStatusTwoLog {
    private final String owThreadName;

    public DataStatusTwoLog(String threadName){
        owThreadName = threadName;
    }


    public List<HashMap> getStatusTwoLogMapList(){
        List<HashMap> StatusTwoMapList = new ArrayList<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = null;

        try {
            newConn = dataConn.getOrderwireConnection();
            pStmt = newConn.prepareStatement("SELECT log_id, payload_id, payload_timestamp, payload_type, order_id, supplier_id, payload_filename "
                    + "FROM ow_orderwire_logs WHERE ow_log_status_code_id = 2 AND mdate < DATE_ADD(NOW(), INTERVAL -30 MINUTE)");
            rSet = pStmt.executeQuery();

            while ( rSet.next() ) {
                HashMap<String, String> StatusTwoMap = new HashMap<>();
                StatusTwoMap.put("log_id", String.valueOf(rSet.getInt("log_id")));
                StatusTwoMap.put("payload_id", rSet.getString("payload_id"));
                StatusTwoMap.put("payload_timestamp", rSet.getString("payload_timestamp"));
                StatusTwoMap.put("payload_type", rSet.getString("payload_type"));
                StatusTwoMap.put("order_id", rSet.getString("order_id"));
                StatusTwoMap.put("supplier_id", rSet.getString("supplier_id"));
                StatusTwoMap.put("payload_filename", rSet.getString("payload_filename"));
                StatusTwoMapList.add(StatusTwoMap);
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataStatusTwoLog", "getStatusTwoLogMap", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataStatusTwoLog", "getStatusTwoLogMap", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return StatusTwoMapList;
    }


}
