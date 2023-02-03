package com.orderwire.data;

import com.orderwire.logging.UtilLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DataGenerateCreditMemo {
    private final String owThreadName;

    public DataGenerateCreditMemo(String threadName){
        owThreadName = threadName;
    }

    public HashMap getCatalogElements(String supplierNo, String supplierPn){
        HashMap catalogMap = new HashMap<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT contract_no, manuf_pn, manuf_name "
                    + "FROM catalog_items "
                    + "WHERE supplier_no = ? "
                    + "AND supplier_pn = ?");
            pStmt.setInt(1, Integer.valueOf(supplierNo));
            pStmt.setString(2, supplierPn);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                catalogMap.put("contract_no", rSet.getString("contract_no"));
                catalogMap.put("manuf_pn", rSet.getString("manuf_pn"));
                catalogMap.put("manuf_name", rSet.getString("manuf_name"));
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getCatalogElements", "No Data Found Exception", "No Data Found", "No Data Found");
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getCatalogElements", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getCatalogElements", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return catalogMap;
    }

    public HashMap getParentInvoiceElements(String orderId, String itemLineNo){
        HashMap<String,String> parentMap = new HashMap<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT invoices_id, invoice_no parent_invoice_no, invoice_date parent_invoice_date "
                    + "FROM invoices "
                    + "WHERE order_id = ? AND po_line_no = ? "
                    + "ORDER BY cdate ASC LIMIT 1;");
            pStmt.setString(1, orderId);
            pStmt.setInt(2, Integer.valueOf(itemLineNo));
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                parentMap.put("invoices_id", String.valueOf(rSet.getInt("invoices_id")));
                parentMap.put("parent_invoice_no", rSet.getString("parent_invoice_no"));
                parentMap.put("parent_invoice_date", rSet.getString("parent_invoice_date"));
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "getParentElements", "", "No Parent Invoice Data Found", "");
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "getParentElements", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "getParentElements", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return parentMap;
    }

    public HashMap getHeaderElements(Integer invoiceId){
        HashMap headerMap = new HashMap<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT inv.order_id, "
                    + "inv.invoice_line_no, inv.invoice_qty, inv.invoice_no, inv.generate_invoice, inv.generate_credit, inv.po_line_no, "
                    + "logs.payload_id, "
                    + "oh.order_date, oh.supplier_no, "
                    + "sp.supplier_identity supplier_from, sp.supplier_domain supplier_from_domain, sp.supplier_sharedsecret sender_sharedsecret, "
                    + "spto.supplier_identity supplier_to, spto.supplier_domain supplier_to_domain, "
                    + "sp.supplier_identity supplier_sender, sp.supplier_sharedsecret sharedsecret_sender "
                    + "FROM invoices inv "
                    + "INNER JOIN ow_orderwire_logs logs ON inv.order_id = logs.order_id "
                    + "INNER JOIN order_headers oh ON inv.order_id = oh.order_id "
                    + "INNER JOIN suppliers sp ON oh.supplier_no = sp.supplier_no "
                    + "INNER JOIN suppliers spto ON sp.supplier_id_to = spto.supplier_id "
                    + "WHERE inv.invoices_id = ?");
            pStmt.setInt(1, invoiceId);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                headerMap.put("owThreadName", owThreadName);
                headerMap.put("order_id", String.valueOf(rSet.getString("order_id")));
                headerMap.put("invoice_line_no", String.valueOf(rSet.getInt("invoice_line_no")));
                headerMap.put("invoice_qty", String.valueOf(rSet.getDouble("invoice_qty")));
                headerMap.put("invoice_no", String.valueOf(rSet.getString("invoice_no")));
                headerMap.put("generate_invoice", String.valueOf(rSet.getString("generate_invoice")));
                headerMap.put("generate_credit", String.valueOf(rSet.getString("generate_credit")));
                headerMap.put("po_line_no", String.valueOf(rSet.getString("po_line_no")));
                headerMap.put("payload_id", String.valueOf(rSet.getString("payload_id")));
                headerMap.put("order_date", String.valueOf(rSet.getString("order_date")));
                headerMap.put("supplier_from", String.valueOf(rSet.getString("supplier_from")));
                headerMap.put("supplier_from_domain", String.valueOf(rSet.getString("supplier_from_domain")));
                headerMap.put("supplier_to", String.valueOf(rSet.getString("supplier_to")));
                headerMap.put("supplier_to_domain", String.valueOf(rSet.getString("supplier_to_domain")));
                headerMap.put("supplier_sender", String.valueOf(rSet.getString("supplier_sender")));
                headerMap.put("sharedsecret_sender", String.valueOf(rSet.getString("sharedsecret_sender")));
                headerMap.put("useragent", "support@infomeld.com");



                HashMap timeMap = creditMemoTimestamp();
                headerMap.put("invoiceTimestamp", String.valueOf(timeMap.get("invoiceTimestamp")));
                headerMap.put("gdateTimestamp", String.valueOf(timeMap.get("gdateTimestamp")));

            } else {
                UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "getHeaderElements", "No Data Found Exception", "No Data Found", "No Data Found");
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "getHeaderElements", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "getHeaderElements", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return headerMap;
    }


    public HashMap getRequestElements(Integer invoiceId, Integer lineNumber){
        HashMap detailMap = new HashMap<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT inv.invoices_id, inv.line_ship_charge, inv.invoice_amount, "
                    + "od.item_uom, od.unit_price, od.supplier_no, od.supplier_pn, od.item_desc "
                    + "FROM invoices inv "
                    + "INNER JOIN order_details od ON inv.order_id = od.order_id "
                    + "WHERE inv.invoices_id = ? "
                    + "AND od.po_line_no = ?");
            pStmt.setInt(1, invoiceId);
            pStmt.setInt(2, lineNumber);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                detailMap.put("invoices_id", String.valueOf(rSet.getInt("invoices_id")));
                detailMap.put("invoice_amount", String.valueOf(rSet.getString("invoice_amount")));
                detailMap.put("item_uom", String.valueOf(rSet.getString("item_uom")));
                detailMap.put("unit_price", String.valueOf(rSet.getDouble("unit_price")));
                detailMap.put("supplier_no", String.valueOf(rSet.getInt("supplier_no")));
                detailMap.put("supplier_pn", String.valueOf(rSet.getString("supplier_pn")));
                detailMap.put("line_ship_charge", String.valueOf(rSet.getDouble("line_ship_charge")));
                detailMap.put("item_desc", String.valueOf(rSet.getString("item_desc")));
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "getRequestElements", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "getRequestElements", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }


        return detailMap;
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
                UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "updateCreditMemoStatus", "", "UpdateResult Not Equal 1", updateResult.toString());
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "updateCreditMemoStatus", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "updateCreditMemoStatus", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return procStatus;
    }

    public Integer insertCreditMemoXML(Integer invoiceId, String xmlDocument){
        Integer invoice_xml_id = -1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("INSERT INTO ow_orderwire_invoice_xmls(invoices_invoices_id, xml_document) VALUES(?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setInt(1, invoiceId);
            pStmt.setString(2, xmlDocument);
            Integer rowAffected = pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                invoice_xml_id = rSet.getInt(1);
                UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "insertCreditMemoXML", "", "Credit Memo XML Document Insert", invoice_xml_id.toString());
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "insertCreditMemoXML", "", "No Generated Key", "");
            }
        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "insertCreditMemoXML", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateCreditMemo", "insertCreditMemoXML", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return invoice_xml_id;
    }


    private HashMap creditMemoTimestamp(){
        HashMap timesMap = new HashMap<>();
        SimpleDateFormat invoiceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        SimpleDateFormat gdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String invoiceTimestamp = invoiceFormat.format(new Date());
        String gdateTimestamp = gdateFormat.format(new Date());


        timesMap.put("invoiceTimestamp", invoiceTimestamp);
        timesMap.put("gdateTimestamp", gdateTimestamp);


        return timesMap;
    }


}
