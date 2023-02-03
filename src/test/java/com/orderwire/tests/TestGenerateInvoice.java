package com.orderwire.tests;

import com.orderwire.data.DataCommon;
import com.orderwire.data.DataConnection;
import com.orderwire.data.LocalDatabaseConnection;
import com.orderwire.excp.InvoiceNotDataFoundException;
import com.orderwire.logging.UtilLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class TestGenerateInvoice {
    private String owThreadName = "TestGenerateInvoice";
    private Integer owTaskSubscribeId = -53;

    public TestGenerateInvoice(){
    }

    public void main(String[] args) {

        Boolean procStatus = TestCase();

    }


    private Boolean TestCase(){
        Boolean procStatus = false;
        Connection newConn = null;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;

        try{
            DataCommon dc = new DataCommon(owTaskSubscribeId, owThreadName);

            // get invoice with generate invoice flag = 'Y' and status code = 0
            Integer invoiceId = dc.getInvoiceGenerate();
            if (invoiceId.equals(-1)) {
                throw new InvoiceNotDataFoundException();
            }



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


    public HashMap getRemainInvoice(Integer invoiceId){
        HashMap<String,String> remainInvoiceMap = new HashMap<>();
        Connection newConn = null;
        PreparedStatement pStmt = null;
        ResultSet rSet = null;

        try {
            LocalDatabaseConnection ldc = new LocalDatabaseConnection();
            newConn = ldc.getOrderwireConnection();


            pStmt = newConn.prepareStatement("SELECT order_id, po_line_no, remain_qty, invoice_qty FROM invoices WHERE invoices_id = ?");
            pStmt.setInt(1, invoiceId);
            rSet = pStmt.executeQuery();
            if ( rSet.next() ){
                remainInvoiceMap.put("order_id", String.valueOf(rSet.getString("order_id")));
                remainInvoiceMap.put("po_line_no", String.valueOf(rSet.getInt("po_line_no")));

                Double _remainQty = rSet.getDouble("remain_qty");
                Double _invoiceQty = rSet.getDouble("invoice_qty");
                Double _newRemainQty = (_remainQty - _invoiceQty);
                remainInvoiceMap.put("remain_qty", String.valueOf(_newRemainQty));

                UtilLogger.setDbStatus(owThreadName,"DataGenerateInvoice", "getRemainInvoice", "", "New Remain Qty", String.valueOf(_newRemainQty));
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getRemainInvoice", "No Result Set", "", "");
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getRemainInvoice", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getRemainInvoice", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }
        return remainInvoiceMap;
    }

}
