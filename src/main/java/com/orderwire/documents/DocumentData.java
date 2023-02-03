package com.orderwire.documents;

import com.orderwire.data.DataConnection;
import com.orderwire.logging.UtilLogger;

import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class DocumentData {
    private final String owThreadName;

    public DocumentData(String threadName){
        owThreadName = threadName;
    }

    public Boolean updateResponseMessage(Integer invoiceXMLId, HttpResponse response){
        Boolean updateStatus = true;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        String responseMessage = response.body().toString();

        try {
            pStmt = newConn.prepareStatement("UPDATE ow_orderwire_invoice_xmls SET xml_response_message = ? WHERE ow_orderwire_invoice_xml_id = ?");
            pStmt.setString(1, responseMessage);
            pStmt.setInt(2, invoiceXMLId);
            Integer rowAffected = pStmt.executeUpdate();

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateResponseMessage", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateResponseMessage", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }


        return updateStatus;
    }

    public Boolean updateInvoiceStatus(Integer invoiceId, Integer statusId, String gdateTimestamp){
        Boolean procStatus = true;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();
        String _generateInvoiceFlag = "P";

        try {
            pStmt = newConn.prepareStatement("UPDATE invoices SET ow_invoice_status_code_id = ?, gdate = ?, generate_invoice = ? WHERE invoices_id = ?");
            pStmt.setInt(1, statusId);
            pStmt.setString(2, gdateTimestamp);
            pStmt.setString(3, _generateInvoiceFlag);
            pStmt.setInt(4, invoiceId);
            Integer updateResult = pStmt.executeUpdate();

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateInvoiceStatus", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateInvoiceStatus", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return procStatus;
    }

    public Boolean updateInvoiceStatus(Integer invoiceId, Integer statusId){
        Boolean procStatus = true;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();
        HashMap<String, String> messageTempMap = new HashMap<>();

        try {
            pStmt = newConn.prepareStatement("UPDATE invoices SET ow_invoice_status_code_id = ? WHERE invoices_id = ?");
            pStmt.setInt(1, statusId);
            pStmt.setInt(2, invoiceId);
            Integer updateResult = pStmt.executeUpdate();

            UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateInvoiceStatus", "", "updateResult", updateResult.toString());

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateInvoiceStatus", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateInvoiceStatus", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return procStatus;
    }

    public Boolean updateCreditMemoStatus(Integer invoiceId, Integer statusId, String gdateTimestamp){
        Boolean procStatus = true;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();
        String _generateCreditMemoFlag = "P";

        try {
            pStmt = newConn.prepareStatement("UPDATE invoices SET ow_invoice_status_code_id = ?, gdate = ?, generate_credit = ? WHERE invoices_id = ?");
            pStmt.setInt(1, statusId);
            pStmt.setString(2, gdateTimestamp);
            pStmt.setString(3, _generateCreditMemoFlag);
            pStmt.setInt(4, invoiceId);
            Integer updateResult = pStmt.executeUpdate();

            if ( !updateResult.equals(1) ){
                UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateCreditMemoStatus", "", "UpdateResult Not Equal 1", updateResult.toString());
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateCreditMemoStatus", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateCreditMemoStatus", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return procStatus;
    }

    public Boolean updateCreditMemoStatus(Integer invoiceId, Integer statusId){
        Boolean procStatus = true;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("UPDATE invoices SET ow_invoice_status_code_id = ? WHERE invoices_id = ?");
            pStmt.setInt(1, statusId);
            pStmt.setInt(2, invoiceId);
            Integer updateResult = pStmt.executeUpdate();

            if ( !updateResult.equals(1) ){
                UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateCreditMemoStatus", "", "UpdateResult Not Equal 1", updateResult.toString());
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateCreditMemoStatus", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DocumentData", "updateCreditMemoStatus", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return procStatus;
    }

}
