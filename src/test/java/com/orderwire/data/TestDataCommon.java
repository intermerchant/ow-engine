package com.orderwire.data;

import com.orderwire.logging.UtilLogger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class TestDataCommon {
    private final String tpSeperator;
    private final Integer owTaskSubscribeId;
    private final String owThreadName;

    public TestDataCommon(){
        tpSeperator = " | ";
        owTaskSubscribeId = null;
        owThreadName = null;
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
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getAccountTaskUpdate", "1", "", "");
            if ( rSet.next() ){
                tsMap.put("owOrderwireTaskSubscribeId", String.valueOf(rSet.getInt("ow_orderwire_task_subscribe_id")));
                tsMap.put("owOrderwireTaskId", String.valueOf(rSet.getInt("ow_orderwire_task_id")));
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "getAccountTaskUpdate", "2", "SID", String.valueOf(rSet.getInt("ow_orderwire_task_subscribe_id")));
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "getAccountTaskUpdate", "2", "TID", String.valueOf(rSet.getInt("ow_orderwire_task_id")));
                rSet.updateInt("task_status", 1);
                rSet.updateRow();
                newConn.commit();
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "getAccountTaskUpdate", "3", "", "");
            } else {
                tsMap.put("owOrderwireTaskSubscribeId", "-1");
                tsMap.put("owOrderwireTaskId", "-1");
                UtilLogger.setDbStatus(owThreadName, "DataCommon", "getAccountTaskUpdate", "4", "", "");
            }
            UtilLogger.setDbStatus(owThreadName, "DataCommon", "getAccountTaskUpdate", "5", "", "");
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

    public Boolean getTaskTimerUpdate(){
        Boolean timerActive = true;
        PreparedStatement psData=null;
        ResultSet rsData=null;
        Connection newConn = null;

        Integer owTaskSubscribeId = 2;

        try{
            LocalDatabaseConnection ldc = new LocalDatabaseConnection();
            newConn = ldc.getOrderwireConnection();
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
                System.out.println("Pause :: ");
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
            System.out.println("SQLException :: " + sqlex.getMessage());
        } catch (Exception exce) {
            System.out.println("Exception :: " + exce.getMessage());
        }finally{
            try {
                if (rsData != null) rsData.close();
                if (psData != null) psData.close();
                if (newConn != null) newConn.close();
            } catch (SQLException finex){}
        }

        return timerActive;
    }

    private int getRandomNumberInRange(int min, int max) {

        Random r = new Random();
        Integer randomNumber = r.ints(min, (max + 1)).limit(1).findFirst().getAsInt();
        return randomNumber;

    }

}
