package com.orderwire.logging;

import com.orderwire.data.DataConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


public class MonitorLogger {
    private final String _dateStamp;
    private final Integer _poolSize;
    private final Integer _corePoolSize;
    private final Integer _activeCount;
    private final Long _completedTaskCount;
    private final Long _taskCount;
    private final Boolean _isShutdown;
    private final Boolean _isTerminated;
    
    public MonitorLogger(HashMap monitorMap){
        _dateStamp = monitorMap.get("_dateStamp").toString();
        _poolSize = Integer.valueOf(monitorMap.get("_poolSize").toString());
        _corePoolSize = Integer.valueOf(monitorMap.get("_corePoolSize").toString());
        _activeCount = Integer.valueOf(monitorMap.get("_activeCount").toString());
        _completedTaskCount = Long.valueOf(monitorMap.get("_completedTaskCount").toString());  
        _taskCount = Long.valueOf(monitorMap.get("_taskCount").toString());  
        _isShutdown = Boolean.valueOf(monitorMap.get("_isShutdown").toString());  
        _isTerminated = Boolean.valueOf(monitorMap.get("_isTerminated").toString());  
    }
    
    
    public void writeMonitorLog(){
        Boolean procStatus = true;
        Integer pkId = -1;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();
        String threadName = "";        
        
        
        try{
            pStmt = newConn.prepareStatement("INSERT INTO ow_orderwire_monitor_thread_logs (date_timestamp, pool_size, core_pool_size, active_count, completed_task_count, task_count, isShutdown, isTerminated) " 
                + "VALUES(?,?,?,?,?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
            
            pStmt.setString(1, _dateStamp);
            pStmt.setInt(2, _poolSize);
            pStmt.setInt(3, _corePoolSize);
            pStmt.setInt(4, _activeCount);
            pStmt.setLong(5, _completedTaskCount);
            pStmt.setLong(6, _taskCount);
            pStmt.setBoolean(7, _isShutdown);
            pStmt.setBoolean(8, _isTerminated);
            
            pStmt.executeUpdate();                
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                pkId = rSet.getInt(1);
            } else {
                UtilLogger.setGenericStatus("owMonitorLogger.log", " | MonitorLogger | writeMonitorLog | No Generated Key ");
            }              
            
        } catch (SQLException sqle) {
            UtilLogger.setGenericStatus("owMonitorLogger.log", " | MonitorLogger | writeMonitorLog | SQL Exception | " + sqle.getMessage() );
        } catch (Exception exce){
            UtilLogger.setGenericStatus("owDbLogger.log", " | MonitorLogger | writeMonitorLog | Exception | " + exce.getMessage() );
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}                
            } catch (SQLException e){ }
        }
    }
}
