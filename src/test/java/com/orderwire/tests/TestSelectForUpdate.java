package com.orderwire.tests;

import com.orderwire.data.LocalDatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestSelectForUpdate {

    public TestSelectForUpdate(){
    }

    public static void main(String[] args) {

        Boolean procStatus = SelectForUpdate();
    }

    private static Boolean SelectForUpdate(){
        Boolean procStatus = true;
        Connection newConn = null;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;

        try{
            LocalDatabaseConnection ldc = new LocalDatabaseConnection();
            newConn = ldc.getOrderwireConnection();
            newConn.setAutoCommit(false);

            pStmt = newConn.prepareStatement("SELECT ow_orderwire_task_subscribe_id, ow_orderwire_task_id, task_status " +
                    "FROM ow_orderwire_task_subscribed " +
                    "WHERE task_status = ? AND active_flag = ? " +
                    "ORDER BY ow_task_next_time ASC, ow_orderwire_task_subscribe_id ASC LIMIT 1 FOR UPDATE SKIP LOCKED",ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pStmt.setInt(1, 1 );
            pStmt.setInt(2, 1 );
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                System.out.println("pause");
                rSet.updateInt("task_status", 0);
                rSet.updateRow();
                newConn.commit();
            }

        } catch (SQLException sqlex) {
            System.out.println("SQLException :: " + sqlex.getMessage());

        } catch (Exception exce){
            System.out.println("Exception :: " + exce.getMessage());
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }

        return procStatus;
    }
}
