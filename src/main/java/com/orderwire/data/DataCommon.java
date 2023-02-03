package com.orderwire.data;

import com.orderwire.logging.UtilLogger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;


public class DataCommon {
    private final String tpSeperator;
    private final Integer owTaskSubscribeId;
    private final String owThreadName;
    
    public DataCommon(){ 
        tpSeperator = " | ";
        owTaskSubscribeId = null;
        owThreadName = null;
    }
    
    public DataCommon(Integer taskSubscribeId, String threadName){ 
        tpSeperator = " | ";
        owTaskSubscribeId = taskSubscribeId;
        owThreadName= threadName;
    }


    public Integer getPurchaseOrderIngestGenerate(){
        Integer logId = -1;
        Connection newConn = null;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;

        try{
            DataConnection dataConn = new DataConnection();
            newConn = dataConn.getOrderwireConnection();
            newConn.setAutoCommit(false);

            pStmt = newConn.prepareStatement("SELECT log_id, ow_orderwire_task_subscribe_id FROM ow_orderwire_logs WHERE ow_log_status_code_id = ? AND ow_orderwire_task_subscribe_id = ? AND payload_type = ? ORDER BY cdate ASC LIMIT 1 FOR UPDATE SKIP LOCKED", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pStmt.setInt(1, 1);
            pStmt.setInt(2, 0);
            pStmt.setString(3, "PO");
            rSet = pStmt.executeQuery();

            if ( rSet.next() ) {
                logId = rSet.getInt("log_id");
                rSet.updateInt("ow_orderwire_task_subscribe_id", owTaskSubscribeId);
                rSet.updateRow();
                newConn.commit();
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getPurchaseOrderIngestGenerate", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getPurchaseOrderIngestGenerate", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return logId;
    }

    public Integer getCreditMemoGenerate(){
        Integer invoiceId = -1;
        Connection newConn = null;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;

        try {
            DataConnection dataConn = new DataConnection();
            newConn = dataConn.getOrderwireConnection();
            pStmt = newConn.prepareStatement("SELECT invoices_id, ow_invoice_status_code_id FROM invoices WHERE generate_credit = ? AND ow_invoice_status_code_id = ? ORDER BY cdate ASC LIMIT 1 FOR UPDATE SKIP LOCKED", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pStmt.setString(1, "Y");
            pStmt.setInt(2, 0);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                invoiceId = rSet.getInt("invoices_id");
                rSet.updateInt("ow_invoice_status_code_id", 1);  // set status code to 1 Invoice Generation in Process
                rSet.updateRow();
                newConn.commit();
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getCreditMemoGenerate", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getCreditMemoGenerate", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return invoiceId;
    }

    public Integer getInvoiceGenerate(){
        HashMap<String, String> messageTempMap = new HashMap<>();
        Integer invoiceId = -1;
        Connection newConn = null;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;

        try {
            DataConnection dataConn = new DataConnection();
            newConn = dataConn.getOrderwireConnection();
            newConn.setAutoCommit(false);
            pStmt = newConn.prepareStatement("SELECT invoices_id, ow_invoice_status_code_id FROM invoices WHERE generate_invoice = ? AND ow_invoice_status_code_id = ? ORDER BY cdate ASC LIMIT 1 FOR UPDATE SKIP LOCKED", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pStmt.setString(1, "Y");
            pStmt.setInt(2, 0);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                invoiceId = rSet.getInt("invoices_id");
                rSet.updateInt("ow_invoice_status_code_id", 1);  // set status code to 1 Invoice Generation in Process
                rSet.updateRow();
                newConn.commit();
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getInvoiceGenerate", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getInvoiceGenerate", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return invoiceId;
    }

    public Integer getGenerateInvoiceFlagCount(){
        Integer gifCount = 1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT count(1) FROM invoices WHERE generate_invoice = ? AND ow_invoice_status_code_id = ?");
            pStmt.setString(1, "Y");
            pStmt.setInt(2, 0);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                gifCount = rSet.getInt(1) + 1;
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "getGenerateInvoiceFlagCount", "", "Generate Invoice Flag Count", gifCount.toString());
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getGenerateInvoiceFlagCount", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getGenerateInvoiceFlagCount", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }


        return gifCount;
    }

    public Integer getActiveGenerateInvoiceTasksCount(){
        Integer agitCount = 1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT count(1) FROM ow_orderwire_task_subscribed WHERE ow_orderwire_task_id = ? AND active_flag = ?");
            pStmt.setInt(1, 53);
            pStmt.setInt(2, 1);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                agitCount = rSet.getInt(1);
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "getActiveGenerateInvoiceTasksCount", "", "Generate Invoice Flag Count", agitCount.toString());
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getActiveGenerateInvoiceTasksCount", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getActiveGenerateInvoiceTasksCount", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }


        return agitCount;
    }

    public boolean setActiveGenerateInvoiceTasks(Integer gifCount, Integer agitCount) {
        boolean procStatus = true;
        Integer incrementCount = 0;
        Integer decrementCount = 0;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        if (gifCount > agitCount) {

            // increment    gifCount - agitCount
            // 100 - 1 = 99   99 add tasks
            incrementCount = (agitCount + gifCount);
            try {
                pStmt = newConn.prepareStatement("UPDATE ow_orderwire_task_subscribed SET active_flag = 1 WHERE ow_orderwire_task_id = ? AND active_flag = 0 ORDER BY ow_orderwire_task_subscribe_id ASC LIMIT " + incrementCount);
                pStmt.setInt(1, 53);
                Integer updateCount = pStmt.executeUpdate();

                UtilLogger.setDbStatus(owThreadName, "DataCommon", "setActiveGenerateInvoiceTasks", "", "Increment Active Generate Invoice Tasks", updateCount.toString());
            } catch (SQLException sqle) {
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "setActiveGenerateInvoiceTasks", "Increment SQLException", sqle.getMessage(), sqle.getSQLState());
            } catch (Exception exce){
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "setActiveGenerateInvoiceTasks", "increment Exception", exce.getMessage(), "");
            }finally {
                try {
                    if (rSet != null){rSet.close();}
                    if (pStmt != null){pStmt.close();}
                    if (newConn != null){newConn.close();}
                } catch (SQLException e){ }
            }
        } else if (agitCount > gifCount) {
            // decrement  agitCount - gifCount
            // 100 - 0  =  100  (100-1) 99 remove tasks
            // 2 - 0 = 2 (2-1) 1 remove task
            // 1 - 0 = 1  (1-1) 0 remove task
            decrementCount = ((agitCount - gifCount) - 1);
            if (decrementCount > 0) {
                try {
                    pStmt = newConn.prepareStatement("UPDATE ow_orderwire_task_subscribed SET active_flag = 0 WHERE ow_orderwire_task_id = ? AND active_flag = 1 ORDER BY ow_orderwire_task_subscribe_id DESC LIMIT " + decrementCount);
                    pStmt.setInt(1, 53);
                    Integer updateCount = pStmt.executeUpdate();

                    UtilLogger.setDbStatus(owThreadName, "DataCommon", "setActiveGenerateInvoiceTasks", "", "Decrement Active Generate Invoice Tasks", updateCount.toString());
                } catch (SQLException sqle) {
                    UtilLogger.setDbStatus(owThreadName, "DataCommon", "setActiveGenerateInvoiceTasks", "Decrement SQLException", sqle.getMessage(), sqle.getSQLState());
                } catch (Exception exce){
                    UtilLogger.setDbStatus(owThreadName, "DataCommon", "setActiveGenerateInvoiceTasks", "Decrement Exception", exce.getMessage(), "");
                }finally {
                    try {
                        if (rSet != null){rSet.close();}
                        if (pStmt != null){pStmt.close();}
                        if (newConn != null){newConn.close();}
                    } catch (SQLException e){ }
                }
            }

        } else {
            // do nothing - close out any open data connection objects
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }

        }

        return procStatus;
    }

    public Integer startTaskSubscribedJournal(){
        PreparedStatement pStmtC=null;
        ResultSet rSetC=null;        
        Integer owTaskSubscribeJournalId = -1;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {

            pStmtC = newConn.prepareStatement("INSERT INTO ow_orderwire_task_subscribed_journal (ow_orderwire_task_subscribe_id) VALUES (?)",PreparedStatement.RETURN_GENERATED_KEYS);
            pStmtC.setInt(1, owTaskSubscribeId );
            pStmtC.executeUpdate();                
            rSetC = pStmtC.getGeneratedKeys();
            if ( rSetC.next() ) {
                owTaskSubscribeJournalId = rSetC.getInt(1);
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "startTaskSubscribedJournal", "", "Insert Task Journal Id", owTaskSubscribeJournalId.toString());
            }  
        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "startTaskSubscribedJournal", "SQLException", sqle.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "startTaskSubscribedJournal", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSetC != null){rSetC.close();}
                if (pStmtC != null){pStmtC.close();}                
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }
        
        return owTaskSubscribeJournalId;
    }
    
    public boolean endTaskSubscribedJournal(Integer taskSubscribeJournalId){
        PreparedStatement pStmtC=null;
        boolean updFlag = false;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmtC = newConn.prepareStatement("UPDATE ow_orderwire_task_subscribed_journal SET task_end_date = current_timestamp WHERE ow_orderwire_task_subscribed_journal_id = ?");
            pStmtC.setInt(1, taskSubscribeJournalId );
            Integer journalUpdate = pStmtC.executeUpdate();
            if ( !journalUpdate.equals(1) ){
                updFlag = false;
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "endTaskSubscribedJournal", "", "Task Subscribed Journal Update Fail", journalUpdate + " :: " + taskSubscribeJournalId);
            }else{
               updFlag = true; 
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "endTaskSubscribedJournal", "SQLException", sqle.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "endTaskSubscribedJournal", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (pStmtC != null){pStmtC.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){ }
        }
        return updFlag;
    }

    public boolean endTaskSubscribedStatus(){
        PreparedStatement pStmt=null;
        boolean updFlag = true;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("UPDATE ow_orderwire_task_subscribed SET task_status = ?, iteration_total_count = iteration_total_count + 1 WHERE ow_orderwire_task_subscribe_id = ?");
            pStmt.setInt(1, 0 );
            pStmt.setInt(2, owTaskSubscribeId );
            Integer taskSubscribeUpdate = pStmt.executeUpdate();
            if ( !taskSubscribeUpdate.equals(1) ){
                updFlag = false;
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "endTaskSubscribedStatus", "SQLException", sqle.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "endTaskSubscribedStatus", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){ }
        }
        return updFlag;
    }

    public HashMap getAccountTaskUpdate(){
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        HashMap<String,String> tsMap = new HashMap<>();
        DataConnection dc = new DataConnection();
        Connection newConn = null;

        try {
            newConn = dc.getOrderwireConnection();
            newConn.setAutoCommit(false);
            pStmt = newConn.prepareStatement("SELECT ow_orderwire_task_subscribe_id, ow_orderwire_task_id, task_status " +
                    "FROM ow_orderwire_task_subscribed " +
                    "WHERE task_status = ? AND active_flag = ? " +
                    "ORDER BY ow_task_next_time ASC, ow_orderwire_task_subscribe_id ASC LIMIT 1 FOR UPDATE SKIP LOCKED",ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pStmt.setInt(1, 0 );
            pStmt.setInt(2, 1 );
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                tsMap.put("owOrderwireTaskSubscribeId", String.valueOf(rSet.getInt("ow_orderwire_task_subscribe_id")));
                tsMap.put("owOrderwireTaskId", String.valueOf(rSet.getInt("ow_orderwire_task_id")));
                rSet.updateInt("task_status", 1);
                rSet.updateRow();
                newConn.commit();
            } else {
                tsMap.put("owOrderwireTaskSubscribeId", "-1");
                tsMap.put("owOrderwireTaskId", "-1");
            }
        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getAccountTaskUpdate", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getAccountTaskUpdate", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }

        return tsMap;
    }

    public HashMap getAccountTask(){
        ResultSet rSet=null;
        PreparedStatement pStmt=null,pStmtU=null;
        HashMap<String,String> tsMap = new HashMap<>();
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();
        try {
            pStmt = newConn.prepareStatement("SELECT ow_orderwire_task_subscribe_id, ow_orderwire_task_id " +
                    "FROM ow_orderwire_task_subscribed " +
                    "WHERE task_status = ? AND active_flag = ? " +
                    "ORDER BY ow_task_next_time ASC, ow_orderwire_task_subscribe_id ASC LIMIT 1");
            pStmt.setInt(1, 0 );
            pStmt.setInt(2, 1 );

            rSet = pStmt.executeQuery(); 
            if ( rSet.next() ){
                tsMap.put("owOrderwireTaskSubscribeId", String.valueOf(rSet.getInt("ow_orderwire_task_subscribe_id")));
                tsMap.put("owOrderwireTaskId", String.valueOf(rSet.getInt("ow_orderwire_task_id")));

                pStmtU = newConn.prepareStatement("UPDATE ow_orderwire_task_subscribed SET task_status = ? WHERE ow_orderwire_task_subscribe_id = ?");
                pStmtU.setInt(1, 1 );
                pStmtU.setInt(2, rSet.getInt("ow_orderwire_task_subscribe_id") );
                pStmtU.executeUpdate();
            } else {
                tsMap.put("owOrderwireTaskSubscribeId", "-1");
                tsMap.put("owOrderwireTaskId", "-1");
            }

        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getAccountTask", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getAccountTask", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (pStmtU != null){pStmtU.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}            
        }
        
        return tsMap;
    }
    
    
    public HashMap getAccountTask(Integer taskId){
        ResultSet rSet=null;
        PreparedStatement pStmt=null,pStmtU=null;
        HashMap<String,String> tsMap = new HashMap<>();
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();
        try {
            pStmt = newConn.prepareStatement("SELECT ow_orderwire_task_subscribe_id, ow_orderwire_task_id " +
                    "FROM ow_orderwire_task_subscribed " +
                    "WHERE task_status = 0 AND active_flag = 1 AND ow_orderwire_task_id = ? " +
                    "ORDER BY mdate ASC LIMIT 1");
            
            pStmt.setInt(1, taskId);
            
            rSet = pStmt.executeQuery(); 
            if ( rSet.next() ){
                tsMap.put("owOrderwireTaskSubscribeId", rSet.getString("ow_orderwire_task_subscribe_id"));
                tsMap.put("owOrderwireTaskId", rSet.getString("ow_orderwire_task_id") );

                pStmtU = newConn.prepareStatement("UPDATE ow_orderwire_task_subscribed SET task_status = 1 WHERE ow_orderwire_task_subscribe_id = ?");
                pStmtU.setInt(1, rSet.getInt("ow_orderwire_task_subscribe_id") );
                pStmtU.executeUpdate();
            } else {
                tsMap.put("owOrderwireTaskSubscribeId", "");
                tsMap.put("owOrderwireTaskId", "");
            }
            

        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getAccountTask", "SQLException", sqlex.getMessage(), "");            
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getAccountTask", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}            
        }
        
        return tsMap;
    }    
    
    
    public Integer getActiveTasksCount(){
        Integer activeTaskCount = 1;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();
        try{
            pStmt = newConn.prepareStatement("SELECT COUNT(1) FROM ow_orderwire_task_subscribed WHERE task_status = 1");
            rSet = pStmt.executeQuery(); 
            if ( rSet.next() ){
                activeTaskCount = rSet.getInt(1);
            }
            // UtilLogger.setDbStatus("OrderwireEngine", "DataCommon", "getActiveTasksCount", "", "Active Task Count", activeTaskCount.toString());
        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus("OrderwireEngine", "DataCommon", "getActiveTasksCount", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus("OrderwireEngine", "DataCommon", "getActiveTasksCount", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}            
        }
        
        return activeTaskCount;
    }
    
    public Integer getActiveTasksCountDelay(){
        Integer activeTaskCountDelay = -1;
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
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getActiveTasksCountDelay", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getActiveTasksCountDelay", "Exception", exce.getMessage(), "");            
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}            
        }
        
        return activeTaskCountDelay;
    }
    

    public Boolean resetActivateTimerTaskStatus(Integer owTaskSubscribeId){
        Boolean resetStatus = true;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();

        try{
            pStmt = newConn.prepareStatement("UPDATE ow_orderwire_task_subscribed SET task_status = 0 WHERE ow_orderwire_task_subscribe_id = ?");
            pStmt.setInt(1, owTaskSubscribeId);
            Integer updateTaskStatus = pStmt.executeUpdate();

        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus("", "DataCommon", "resetActivateTimerTaskStatus", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus("", "DataCommon", "resetActivateTimerTaskStatus", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }

        return resetStatus;

    }

    public Boolean resetTaskStatus(){
        Boolean resetStatus = true;
        PreparedStatement pStmtA=null,pStmtB=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();
        try{
            pStmtA = newConn.prepareStatement("UPDATE ow_orderwire_task_subscribed SET task_status = 0 WHERE active_flag = 1");
            Integer updateTaskCount = pStmtA.executeUpdate();
            
            pStmtB = newConn.prepareStatement("UPDATE ow_orderwire_task_subscribed_journal SET task_end_date = CURDATE() WHERE task_end_date IS NULL");
            Integer updateEndDateCount = pStmtB.executeUpdate();
            
        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus("", "DataCommon", "resetTaskStatus", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus("", "DataCommon", "resetTaskStatus", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (pStmtB != null){pStmtB.close();}
                if (pStmtA != null){pStmtA.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}            
        }
        
        return resetStatus;
    }

    public Boolean getTaskTimerUpdate(Integer owTaskSubscribeId){
        Boolean timerActive = true;
        PreparedStatement psData=null;
        ResultSet rsData=null;
        DataConnection dc = new DataConnection();
        Connection newConn = null;

        try{
            newConn = dc.getOrderwireConnection();
            newConn.setAutoCommit(false);
            psData = newConn.prepareStatement("SELECT ow_orderwire_task_subscribe_id, task_status, ow_task_timer_min, ow_task_timer_max, ow_task_timer_interval, ow_task_next_time FROM ow_orderwire_task_subscribed WHERE ow_orderwire_task_subscribe_id = ? FOR UPDATE SKIP LOCKED",ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            psData.setInt(1, owTaskSubscribeId);
            rsData = psData.executeQuery();
            if (rsData.next()){
                String timerNextTime = rsData.getString("ow_task_next_time");
                Integer timerMin = Integer.valueOf(rsData.getString("ow_task_timer_min"));
                Integer timerMax = Integer.valueOf(rsData.getString("ow_task_timer_max"));

                //Get Random Next timerInterval based on timer_min and timer_max parameters
                Integer timerInterval = getRandomNumberInRange(timerMin,timerMax);

                Date mNow = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date mLater = sdf.parse(timerNextTime);
                // UtilLogger.setDbStatus("owThreadName", "DataCommon", "getTaskTimer", "", "Timers", "mLater :: " + mLater + " | mNow :: " + mNow);

                if(mLater.before(mNow)) {
                    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date fNow = new Date(mNow.getTime() + timerInterval * 1000);
                    String nextTime = sdf.format(fNow);

                    rsData.updateInt("ow_task_timer_interval", timerInterval);
                    rsData.updateTimestamp("ow_task_next_time", Timestamp.valueOf(nextTime));
                    rsData.updateRow();
                    newConn.commit();
                } else {
                    rsData.updateInt("task_status", 0);
                    rsData.updateRow();
                    newConn.commit();
                    timerActive = false;
                }
            }else{
                timerActive = false;
            }

        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus("owThreadName", "DataCommon", "getTaskTimerUpdate", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus("owThreadName", "DataCommon", "getTaskTimerUpdate", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rsData != null) rsData.close();
                if (psData != null) psData.close();
                if (newConn != null) newConn.close();
            } catch (SQLException finex){}
        }

        return timerActive;
    }

    public Boolean getTaskTimer(Integer owTaskSubscribeId){
        Boolean timerActive = true;
        Connection dbConn=null;
        PreparedStatement psData=null;
        PreparedStatement psData1=null;
        PreparedStatement psData2=null;
        ResultSet rsData=null;
        DataConnection mysqlConn = new DataConnection();
        
        try{
            dbConn = mysqlConn.getOrderwireConnection();
            psData = dbConn.prepareStatement("SELECT ow_task_timer_min, ow_task_timer_max, ow_task_timer_interval, IFNULL(ow_task_next_time, '2019-01-01 12:00:00') ow_task_next_time FROM ow_orderwire_task_subscribed WHERE ow_orderwire_task_subscribe_id = ? AND ow_task_timer_interval != ?");
            psData.setInt(1, owTaskSubscribeId);
            psData.setInt(2, 0);
            rsData = psData.executeQuery(); 
            if (rsData.next()){
                String timerNextTime = rsData.getString("ow_task_next_time");
                Integer timerMin = Integer.valueOf(rsData.getString("ow_task_timer_min"));
                Integer timerMax = Integer.valueOf(rsData.getString("ow_task_timer_max"));
                
                //Get Random Next timerInterval based on timer_min and timer_max parameters
                Integer timerInterval = getRandomNumberInRange(timerMin,timerMax);
                
                Date mNow = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date mLater = sdf.parse(timerNextTime);
                // UtilLogger.setDbStatus("owThreadName", "DataCommon", "getTaskTimer", "", "Timers", "mLater :: " + mLater + " | mNow :: " + mNow);

                if(mLater.before(mNow)) {
                    psData1 = dbConn.prepareStatement("UPDATE ow_orderwire_task_subscribed SET ow_task_timer_interval = ?, ow_task_next_time = DATE_ADD(NOW(), INTERVAL " + timerInterval + " SECOND) WHERE ow_orderwire_task_subscribe_id = ?");
                    psData1.setInt(1, timerInterval);
                    psData1.setInt(2, owTaskSubscribeId);
                    psData1.executeUpdate();
                    // UtilLogger.setDbStatus("owThreadName", "DataCommon", "getTaskTimer", "", "Update Timer Interval", "Task Subscribe Id :: " + owTaskSubscribeId + " | Timer Interval :: " + timerInterval);
                }else{
                    // psData2 = dbConn.prepareStatement("UPDATE ow_orderwire_task_subscribed SET task_status = 0 WHERE ow_orderwire_task_subscribe_id = ?");
                    // psData2.setInt(1, owTaskSubscribeId);
                    psData2 = dbConn.prepareStatement("UPDATE ow_orderwire_task_subscribed SET ow_task_timer_interval = ?, ow_task_next_time = DATE_ADD(NOW(), INTERVAL " + timerInterval + " SECOND) WHERE ow_orderwire_task_subscribe_id = ?");
                    psData2.setInt(1, timerInterval);
                    psData2.setInt(2, owTaskSubscribeId);
                    psData2.executeUpdate();
                    timerActive = false;
                }
            }else{
                timerActive = false;
            }

        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus("owThreadName", "DataCommon", "getTaskTimer", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus("owThreadName", "DataCommon", "getTaskTimer", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (psData2 != null) psData2.close();
                if (psData1 != null) psData1.close();
                if (rsData != null) rsData.close();
                if (psData != null) psData.close();
                if (dbConn != null) dbConn.close();
            } catch (SQLException finex){}            
        }
        
        return timerActive;
    }

    public Boolean getCaptureDiscardLogMessageFlag(){
        Boolean _captureDiscarMode = false;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();
        try {
            pStmt = newConn.prepareStatement("SELECT parameter_value FROM ow_orderwire_parameters WHERE parameter_name = 'CaptureDiscardLogMessage'");
            rSet = pStmt.executeQuery();
            if ( rSet.next() ){
                _captureDiscarMode = rSet.getBoolean("parameter_value");
            }

        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "CaptureDiscardLogMessageFlag", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "CaptureDiscardLogMessageFlag", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }
        return _captureDiscarMode;
    }

    public Boolean getDebugMode(){
        Boolean _debugMode = false;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = null;
        try {
            newConn = dc.getOrderwireConnection();
            pStmt = newConn.prepareStatement("SELECT parameter_value FROM ow_orderwire_parameters WHERE parameter_name = 'DebugMode'");
            rSet = pStmt.executeQuery(); 
            if ( rSet.next() ){
               _debugMode = rSet.getBoolean("parameter_value");
            }
            
        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getDebugMode", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getDebugMode", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}            
        }        
        return _debugMode;
    }    
    
    public String getPropRunCycle(){
        String propRunCycle = "ON";
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();
        try {
            pStmt = newConn.prepareStatement("SELECT parameter_value FROM ow_orderwire_parameters WHERE parameter_name = 'propRunCycle'");
            rSet = pStmt.executeQuery(); 
            if ( rSet.next() ){
               propRunCycle = rSet.getString("parameter_value");
            }
            
        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getPropRunCycle", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getPropRunCycle", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}            
        }        
        return propRunCycle;
    }
    
    public Integer getPropThreadCount(){
        Integer propThreadCount = 0;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();
        try {
            pStmt = newConn.prepareStatement("SELECT parameter_value FROM ow_orderwire_parameters WHERE parameter_name = 'propThreadCount'");
            rSet = pStmt.executeQuery(); 
            if ( rSet.next() ){
               propThreadCount = Integer.valueOf(rSet.getString("parameter_value"));
            }
            
        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getpropThreadCount", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getpropThreadCount", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}            
        }        
        return propThreadCount;
    }

    public Integer getPropMaxThreadCount(){
        Integer propMaxThreadCount = 0;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();
        try {
            pStmt = newConn.prepareStatement("SELECT parameter_value FROM ow_orderwire_parameters WHERE parameter_name = 'propMaxThreadCount'");
            rSet = pStmt.executeQuery();
            if ( rSet.next() ){
                propMaxThreadCount = Integer.valueOf(rSet.getString("parameter_value"));
            }

        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getPropMaxThreadCount", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getPropMaxThreadCount", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }
        return propMaxThreadCount;
    }

    
    public Integer getMonitorThreadDelay(){
        Integer monitorThreadDelay = 0;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();
        try {
            pStmt = newConn.prepareStatement("SELECT parameter_value FROM ow_orderwire_parameters WHERE parameter_name = 'MonitorThreadDelay'");
            rSet = pStmt.executeQuery(); 
            if ( rSet.next() ){
               monitorThreadDelay = Integer.valueOf(rSet.getString("parameter_value"));
            }
            
        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getMonitorThreadDelay", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getMonitorThreadDelay", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}            
        }        
        return monitorThreadDelay;
    }
    
    public String getPOUploadFilePathLinux(){
        String POUploadFilePathLinux = "";
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();
        try {
            pStmt = newConn.prepareStatement("SELECT parameter_value FROM ow_orderwire_parameters WHERE parameter_name = 'POUploadFilePathLinux'");
            rSet = pStmt.executeQuery(); 
            if ( rSet.next() ){
               POUploadFilePathLinux = rSet.getString("parameter_value");
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "getPOUploadFilePathLinux", "Parameter Value Not Found", "Parameter POUploadFilePathLinux", POUploadFilePathLinux);
            }
            
        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getPOUploadFilePathLinux", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getPOUploadFilePathLinux", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}            
        }        
        return POUploadFilePathLinux;
    }

    public String getEdiFilePath(){
        String ediPathFile = "";
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();

        try{
            pStmt = newConn.prepareStatement("SELECT parameter_value FROM ow_orderwire_parameters WHERE parameter_name = ?");
            pStmt.setString(1,"ediFilePath");
            rSet = pStmt.executeQuery();
            if ( rSet.next() ){
                ediPathFile = rSet.getString("parameter_value");
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "getEdiFilePath", "Parameter Value Not Found", "Parameter ediFilePath", "");
            }
        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getEdiFilePath", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getEdiFilePath", "Exception", exce.getMessage(),"");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }

        return ediPathFile;
    }

    public String getEdiArchivePath(){
        String ediArchivePath = "";
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        DataConnection dc = new DataConnection();
        Connection newConn = dc.getOrderwireConnection();

        try{
            pStmt = newConn.prepareStatement("SELECT parameter_value FROM ow_orderwire_parameters WHERE parameter_name = ?");
            pStmt.setString(1,"ediArchivePath");
            rSet = pStmt.executeQuery();
            if ( rSet.next() ){
                ediArchivePath = rSet.getString("parameter_value");
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "ediArchivePath", "Parameter Value Not Found", "Parameter ediFilePath", "");
            }
        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "ediArchivePath", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "ediArchivePath", "Exception", exce.getMessage(),"");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }

        return ediArchivePath;
    }


    private int getRandomNumberInRange(int min, int max) {

        Random r = new Random();
	    Integer randomNumber = r.ints(min, (max + 1)).limit(1).findFirst().getAsInt();
        return randomNumber;

	}

    public Integer getRunLoopDelayParameter(){
        Integer runCycleLoopDelay = 0;
        Connection newConn = null;
        PreparedStatement pStmt = null;
        ResultSet rSet = null;


        try{
            DataConnection dc = new DataConnection();
            newConn = dc.getOrderwireConnection();
            pStmt = newConn.prepareStatement("SELECT parameter_value FROM ow_orderwire_parameters WHERE parameter_name = ?");
            pStmt.setString(1,"RunLoopDelayParameter");
            rSet = pStmt.executeQuery();
            if ( rSet.next() ){
                runCycleLoopDelay = rSet.getInt("parameter_value");
            }

        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getRunLoopDelayParameter", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getRunLoopDelayParameter", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }

        return runCycleLoopDelay;
    }
    
}
