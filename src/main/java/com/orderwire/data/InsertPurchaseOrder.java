package com.orderwire.data;

import com.orderwire.logging.UtilLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


public class InsertPurchaseOrder {
    private final HashMap TransactionMap;
    private final Connection newConn;
    private final String owThreadName;


    public InsertPurchaseOrder(HashMap _transactionMap, Connection _dataConnection){
        TransactionMap = _transactionMap;
        newConn = _dataConnection;
        owThreadName = TransactionMap.get("_threadName").toString();
    }



    public Integer insertOrderHeader(){
        Integer pkId = -1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;

        try{
            pStmt = newConn.prepareStatement("INSERT INTO order_headers (supplier_no, order_timestamp, order_id, payload_filename, order_type, order_date, order_total, "
                    + "shipto_address_country_code, shipto_address_id, "
                    + "shipto_name, shipto_deliverto, shipto_address_street1, shipto_address_street2, shipto_city, shipto_state, shipto_zip, shipto_country, shipto_email, "
                    + "shipto_phone_country_code, shipto_phone_area_code, shipto_phone_number, shipto_phone_extension, "
                    + "billto_address_country_code, billto_address_id, "
                    + "billto_name, billto_deliverto, billto_address_street1, billto_address_street2, billto_city, billto_state, billto_zip, billto_country, billto_email, "
                    + "billto_phone_country_code, billto_phone_area_code, billto_phone_number, billto_phone_extension, "
                    + "ship_charge, ship_charge_desc, order_contact_name, order_contact_email, order_contact_phone_country_code, order_contact_phone_area_code, order_contact_phone_number, order_contact_phone_extension) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);

            pStmt.setInt(1, Integer.valueOf(TransactionMap.get("_supplierNo").toString()));
            pStmt.setString(2, String.valueOf(TransactionMap.get("_payloadTimestamp").toString()));
            pStmt.setString(3, String.valueOf(TransactionMap.get("_orderId").toString()));
            pStmt.setString(4, String.valueOf(TransactionMap.get("_payloadFilename").toString()));
            pStmt.setString(5, String.valueOf(TransactionMap.get("_orderType").toString()));
            pStmt.setString(6, String.valueOf(TransactionMap.get("_orderDate").toString()));
            pStmt.setDouble(7, Double.valueOf(TransactionMap.get("_orderTotal").toString()));
            pStmt.setString(8, String.valueOf(TransactionMap.get("_shipToAddressIsoCountryCode").toString()));
            pStmt.setString(9, String.valueOf(TransactionMap.get("_shipToAddressId").toString()));
            pStmt.setString(10, String.valueOf(TransactionMap.get("_shipToName").toString()));
            pStmt.setString(11, String.valueOf(TransactionMap.get("_shipToDeliverTo").toString()));
            pStmt.setString(12, String.valueOf(TransactionMap.get("_shipToStreetA").toString()));
            pStmt.setString(13, String.valueOf(TransactionMap.get("_shipToStreetB").toString()));
            pStmt.setString(14, String.valueOf(TransactionMap.get("_shipToCity").toString()));
            pStmt.setString(15, String.valueOf(TransactionMap.get("_shipToState").toString()));
            pStmt.setString(16, String.valueOf(TransactionMap.get("_shipToPostalCode").toString()));
            pStmt.setString(17, String.valueOf(TransactionMap.get("_shipToCountry").toString()));
            pStmt.setString(18, String.valueOf(TransactionMap.get("_shipToEmail").toString()));
            pStmt.setString(19, String.valueOf(TransactionMap.get("_shipToPhoneCountryCode").toString()));
            pStmt.setString(20, String.valueOf(TransactionMap.get("_shipToPhoneAreaCode").toString()));
            pStmt.setString(21, String.valueOf(TransactionMap.get("_shipToPhoneNumber").toString()));
            pStmt.setString(22, String.valueOf(TransactionMap.get("_shipToPhoneExtension").toString()));
            pStmt.setString(23, String.valueOf(TransactionMap.get("_billToAddressIsoCountryCode").toString()));
            pStmt.setString(24, String.valueOf(TransactionMap.get("_billToAddressId").toString()));
            pStmt.setString(25, String.valueOf(TransactionMap.get("_billToName").toString()));
            pStmt.setString(26, String.valueOf(TransactionMap.get("_billToDeliverTo").toString()));
            pStmt.setString(27, String.valueOf(TransactionMap.get("_billToStreetA").toString()));
            pStmt.setString(28, String.valueOf(TransactionMap.get("_billToStreetB").toString()));
            pStmt.setString(29, String.valueOf(TransactionMap.get("_billToCity").toString()));
            pStmt.setString(30, String.valueOf(TransactionMap.get("_billToState").toString()));
            pStmt.setString(31, String.valueOf(TransactionMap.get("_billToPostalCode").toString()));
            pStmt.setString(32, String.valueOf(TransactionMap.get("_billToCountry").toString()));
            pStmt.setString(33, String.valueOf(TransactionMap.get("_billToEmail").toString()));
            pStmt.setString(34, String.valueOf(TransactionMap.get("_billToPhoneCountryCode").toString()));
            pStmt.setString(35, String.valueOf(TransactionMap.get("_billToPhoneAreaCode").toString()));
            pStmt.setString(36, String.valueOf(TransactionMap.get("_billToPhoneNumber").toString()));
            pStmt.setString(37, String.valueOf(TransactionMap.get("_billToPhoneExtension").toString()));
            pStmt.setDouble(38, Double.valueOf(TransactionMap.get("_shipCharge").toString()));
            pStmt.setString(39, String.valueOf(TransactionMap.get("_shipDescription").toString()));
            pStmt.setString(40, String.valueOf(TransactionMap.get("_contactName").toString()));
            pStmt.setString(41, String.valueOf(TransactionMap.get("_contactEmail").toString()));
            pStmt.setString(42, String.valueOf(TransactionMap.get("_contactPhoneCountryCode").toString()));
            pStmt.setString(43, String.valueOf(TransactionMap.get("_contactPhoneAreaCode").toString()));
            pStmt.setString(44, String.valueOf(TransactionMap.get("_contactPhoneNumber").toString()));
            pStmt.setString(45, String.valueOf(TransactionMap.get("_contactPhoneExtension").toString()));

            Integer rowAffected = pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                pkId = rSet.getInt(1);
                UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertOrderHeader", "", "PO Header Insert", pkId.toString());
            } else {
                UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertOrderHeader", "", "No Generated Key", "");
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertOrderHeader", "SQLException", sqle.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertOrderHeader", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
            } catch (SQLException e){ }
        }

        return pkId;
    }


    public Integer insertOrderDetail(){
        Integer pkId = -1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;

        try {
            pStmt = newConn.prepareStatement("INSERT INTO order_details(order_header_id, order_id, po_line_no, supplier_no, supplier_pn, supplier_aux_pn, "
                    + "item_qty, unit_price, item_uom, item_desc, acct_name, acct_charge) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setInt(1, Integer.valueOf(TransactionMap.get("_orderHeaderPK").toString()));
            pStmt.setString(2, String.valueOf(TransactionMap.get("_orderId").toString()));
            pStmt.setInt(3, Integer.valueOf(TransactionMap.get("_poLineNumber").toString()));
            pStmt.setInt(4, Integer.valueOf(TransactionMap.get("_supplierNo").toString()));
            pStmt.setString(5, String.valueOf(TransactionMap.get("_itemIdSupplierPartId").toString()));
            pStmt.setString(6, String.valueOf(TransactionMap.get("_itemIdSupplierPartAuxiliaryId").toString()));
            pStmt.setDouble(7, Integer.valueOf(TransactionMap.get("_itemQuantity").toString()));
            pStmt.setDouble(8, Double.valueOf(TransactionMap.get("_unitPrice").toString()));
            pStmt.setString(9, String.valueOf(TransactionMap.get("_unitOfMeasure").toString()));
            pStmt.setString(10, String.valueOf(TransactionMap.get("_itemDescription").toString()));
            pStmt.setString(11, String.valueOf(TransactionMap.get("_accountingName").toString()));
            pStmt.setString(12, String.valueOf(TransactionMap.get("_charge").toString()));

            Integer rowAffected = pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                pkId = rSet.getInt(1);
                UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertOrderDetail", "", "PO Detail Insert", pkId.toString());
            } else {
                UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertOrderDetail", "", "No Generated Key", "");
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertOrderDetail", "SQLException", sqle.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertOrderDetail", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
            } catch (SQLException e){ }
        }

        return pkId;
    }


    public Integer insertOrderSegment(){
        Integer pkId = -1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;

        try {
            pStmt = newConn.prepareStatement("INSERT INTO order_distributions(order_detail_id, segment_type, segment_description, segment_id) "
                    + "VALUES(?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setInt(1, Integer.valueOf(TransactionMap.get("_orderDetailPK").toString()));
            pStmt.setString(2, String.valueOf(TransactionMap.get("_segmentType").toString()));
            pStmt.setString(3, String.valueOf(TransactionMap.get("_segmentDescription").toString()));
            pStmt.setString(4, String.valueOf(TransactionMap.get("_segmentId").toString()));

            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                pkId = rSet.getInt(1);
                UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertOrderSegment", "", "PO Segment Insert", pkId.toString());
            } else {
                UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertOrderSegment", "", "No Generated Key", "");
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertOrderSegment", "SQLException", sqle.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertOrderSegment", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
            } catch (SQLException e){ }
        }

        return pkId;
    }


    public Integer insertInvoice(){
        Integer pkId = -1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;

        try {
            pStmt = newConn.prepareStatement("INSERT INTO invoices(order_id, po_line_no, remain_qty) "
                    + "VALUES(?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setString(1, String.valueOf(TransactionMap.get("_orderId").toString()));
            pStmt.setInt(2, Integer.valueOf(TransactionMap.get("_poLineNumber").toString()));
            pStmt.setDouble(3, Double.valueOf(TransactionMap.get("_itemQuantity").toString()));

            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                pkId = rSet.getInt(1);
                UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertInvoice", "", "Invoice Insert", pkId.toString());
            } else {
                UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertInvoice", "", "No Generated Key", "");
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertInvoice", "SQLException", sqle.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "InsertPurchaseOrder", "insertInvoice", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
            } catch (SQLException e){ }
        }

        return pkId;
    }

}