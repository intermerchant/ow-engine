package com.orderwire.tests;

import com.orderwire.data.LocalDatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestCaseTemplate {

    public TestCaseTemplate(){
    }

    public static void main(String[] args) {

        Boolean procStatus = TestCase();

    }


    private static Boolean TestCase(){
        Boolean procStatus = false;
        Connection newConn = null;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;

        try{
            LocalDatabaseConnection ldc = new LocalDatabaseConnection();
            newConn = ldc.getOrderwireConnection();
            newConn.setAutoCommit(false);

            pStmt = newConn.prepareStatement("" );
            pStmt.setInt(1, 1 );
            pStmt.setInt(2, 1 );
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                System.out.println("pause");
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
