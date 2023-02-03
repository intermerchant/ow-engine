package com.orderwire.data;

import com.orderwire.logging.UtilLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DataGenerateInvoice {
    private final String owThreadName;

    public DataGenerateInvoice(String threadName){
        owThreadName = threadName;
    }

    public Boolean RemainInvoice(Integer invoiceId){
        Boolean procStatus = true;

        try {
            //get hashmap of current invoiceId
            HashMap<String,String> currentInvoice = getRemainInvoice(invoiceId);

            Double _remainQty = Double.valueOf(currentInvoice.get("remain_qty"));

            if (!_remainQty.equals(0.0)){
                Integer remainInvoiceId = insertRemainInvoice(currentInvoice);
                UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "RemainInvoice", "", "Insert Remaining Invoice", remainInvoiceId.toString());
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "RemainInvoice", "", "No Remaining Quantity to Invoice", "");
            }
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "RemainInvoice", "Exception", exce.getMessage(), "");
        }finally { }

        return procStatus;

    }

    public HashMap getRemainInvoice(Integer invoiceId){
        HashMap<String,String> remainInvoiceMap = new HashMap<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
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

                UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getRemainInvoice", "", "New Remain Quantity", String.valueOf(_newRemainQty));
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

    public Integer insertRemainInvoice(HashMap<String,String> remainMap){
        Integer primaryId = -1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            /// if duplicates are still being created ... we may need to add a soft constraint to run a select query to ensure this insert statement will not create a duplicate.
            //  select invoice_id from invoices where order_id = ? and po_line_no = ? and remain_qty = ?

            pStmt = newConn.prepareStatement("INSERT INTO invoices(order_id, po_line_no, remain_qty) VALUES(?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setString(1, remainMap.get("order_id"));
            pStmt.setInt(2, Integer.valueOf(remainMap.get("po_line_no")));
            pStmt.setDouble(3, Double.valueOf(remainMap.get("remain_qty")));
            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                primaryId = rSet.getInt(1);
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "insertRemainInvoice", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "insertRemainInvoice", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return primaryId;
    }

    public HashMap getHeaderElements(Integer invoiceId){
        HashMap headerMap = new HashMap<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT inv.invoices_id, inv.order_id, "
                    + "inv.invoice_line_no, inv.invoice_qty, inv.invoice_no, inv.generate_invoice, inv.generate_credit, inv.po_line_no, inv.invoice_amount, "
                    + "logs.payload_id, "
                    + "oh.order_date, oh.supplier_no, "
                    + "sp.supplier_identity supplier_from, sp.supplier_domain supplier_from_domain, sp.supplier_sharedsecret sender_sharedsecret, "
                    + "spto.supplier_identity supplier_to, spto.supplier_domain supplier_to_domain, "
                    + "sp.supplier_identity supplier_sender, sp.supplier_sharedsecret sharedsecret_sender, "
                    + "ip.supplier_inv_addr_id, ip.supplier_inv_name "
                    + "FROM invoices inv "
                    + "INNER JOIN ow_orderwire_logs logs ON inv.order_id = logs.order_id "
                    + "INNER JOIN order_headers oh ON inv.order_id = oh.order_id "
                    + "INNER JOIN suppliers sp ON oh.supplier_no = sp.supplier_no "
                    + "INNER JOIN suppliers spto ON sp.supplier_id_to = spto.supplier_id "
                    + "INNER JOIN suppliers ip ON oh.supplier_no = ip.supplier_no "
                    + "WHERE inv.invoices_id = ?");
            pStmt.setInt(1, invoiceId);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                headerMap.put("owThreadName", owThreadName);
                headerMap.put("invoices_id", String.valueOf(rSet.getInt("invoices_id")));
                headerMap.put("order_id", String.valueOf(rSet.getString("order_id")));
                headerMap.put("invoice_line_no", String.valueOf(rSet.getInt("invoice_line_no")));
                headerMap.put("invoice_qty", String.valueOf(rSet.getDouble("invoice_qty")));
                headerMap.put("invoice_no", String.valueOf(rSet.getString("invoice_no")));
                headerMap.put("generate_invoice", String.valueOf(rSet.getString("generate_invoice")));
                headerMap.put("generate_credit", String.valueOf(rSet.getString("generate_credit")));
                headerMap.put("po_line_no", String.valueOf(rSet.getString("po_line_no")));
                headerMap.put("invoice_amount", String.valueOf(rSet.getString("invoice_amount")));
                headerMap.put("payload_id", String.valueOf(rSet.getString("payload_id")));
                headerMap.put("order_date", String.valueOf(rSet.getString("order_date")));
                headerMap.put("supplier_from", String.valueOf(rSet.getString("supplier_from")));
                headerMap.put("supplier_from_domain", String.valueOf(rSet.getString("supplier_from_domain")));
                headerMap.put("supplier_to", String.valueOf(rSet.getString("supplier_to")));
                headerMap.put("supplier_to_domain", String.valueOf(rSet.getString("supplier_to_domain")));
                headerMap.put("supplier_sender", String.valueOf(rSet.getString("supplier_sender")));
                headerMap.put("sharedsecret_sender", String.valueOf(rSet.getString("sharedsecret_sender")));
                headerMap.put("supplier_inv_addr_id", String.valueOf(rSet.getString("supplier_inv_addr_id")));
                headerMap.put("supplier_inv_name", String.valueOf(rSet.getString("supplier_inv_name")));
                headerMap.put("useragent", "support@infomeld.com");


                HashMap timeMap = invoiceTimestamp();
                headerMap.put("invoiceTimestamp", String.valueOf(timeMap.get("invoiceTimestamp")));
                headerMap.put("gdateTimestamp", String.valueOf(timeMap.get("gdateTimestamp")));

            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getHeaderElements", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getHeaderElements", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return headerMap;
    }

    public HashMap getCatalogElements(String supplierNo, String supplierPn){
        HashMap catalogMap = new HashMap<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT manuf_pn, manuf_name "
                    + "FROM catalog_items "
                    + "WHERE supplier_no = ? "
                    + "AND supplier_pn = ?");
            pStmt.setInt(1, Integer.valueOf(supplierNo));
            pStmt.setString(2, supplierPn);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
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

    public HashMap getRequestElements(Integer invoiceId, Integer lineNumber){
        HashMap detailMap = new HashMap<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT inv.invoices_id, inv.line_ship_charge, "
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
                detailMap.put("item_uom", String.valueOf(rSet.getString("item_uom")));
                detailMap.put("unit_price", String.valueOf(rSet.getDouble("unit_price")));
                detailMap.put("supplier_no", String.valueOf(rSet.getInt("supplier_no")));
                detailMap.put("supplier_pn", String.valueOf(rSet.getString("supplier_pn")));
                detailMap.put("line_ship_charge", String.valueOf(rSet.getDouble("line_ship_charge")));
                detailMap.put("item_desc", String.valueOf(rSet.getString("item_desc")));
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getRequestElements", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getRequestElements", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return detailMap;
    }

    private HashMap invoiceTimestamp(){
        HashMap timesMap = new HashMap<>();
        SimpleDateFormat invoiceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        SimpleDateFormat gdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String invoiceTimestamp = invoiceFormat.format(new Date());
        String gdateTimestamp = gdateFormat.format(new Date());


        timesMap.put("invoiceTimestamp", invoiceTimestamp);
        timesMap.put("gdateTimestamp", gdateTimestamp);


        return timesMap;
    }

    public Integer insertInvoiceXML(Integer invoiceId, String xmlDocument){
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
                UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "insertInvoiceXML", "", "Invoice XML Document Insert", invoice_xml_id.toString());
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "insertInvoiceXML", "", "No Generated Key", "");
            }
        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "insertInvoiceXML", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "insertInvoiceXML", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return invoice_xml_id;
    }

    public HashMap getRuntimeParameters(){
        HashMap<String,String> runtimeParametersMap = new HashMap<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {

            pStmt = newConn.prepareStatement("SELECT parameter_name, parameter_value FROM ow_orderwire_parameters where parameter_name = ? "
                    +  "UNION "
                    +  "SELECT parameter_name, parameter_value FROM ow_orderwire_parameters where parameter_name like ?");
            pStmt.setString(1, "EnvironmentRuntimeMode");
            pStmt.setString(2, "Outgoing%");
            rSet = pStmt.executeQuery();

            while ( rSet.next() ){
                runtimeParametersMap.put(rSet.getString("parameter_name"), rSet.getString("parameter_value"));
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getRuntimeParameters", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataGenerateInvoice", "getRuntimeParameters", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return runtimeParametersMap;
    }



}