package com.orderwire.recovery;

import com.orderwire.data.DataConnection;
import com.orderwire.logging.UtilLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecoveryData {
    String owThreadName = "Recovery";

    public Connection getDataConnection(){
        Connection dataConnection = null;
        DataConnection dc = new DataConnection();
        try {
            dataConnection = dc.getOrderwireConnection();
        }catch (Exception excep){

        }
        return dataConnection;
    }

    public String getOrderIdGroup(){
        String orderId = "";
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        Connection newConn = getDataConnection();

        try{
            pStmt = newConn.prepareStatement("select order_id from tmpMaster where process_flag = 1 group by order_id limit 1");
            rSet = pStmt.executeQuery();
            if ( rSet.next() ){
                orderId = rSet.getString("order_id");
            }

        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "getOrderIdGroup", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "getOrderIdGroup", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }

        return orderId;
    }

    public List<HashMap> getMasterMapList(String orderId) {
        List<HashMap> masterMapList = new ArrayList<>();
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        Connection newConn = getDataConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT id, supplier_no, order_killed, order_timestamp, order_id, order_type, order_date, order_total, shipto_address_id, " +
                    "shipto_name, shipto_deliverto, shipto_address_street1, shipto_address_street2, shipto_city, shipto_state, shipto_zip, line_item_killed, po_line_no, " +
                    "supplier_pn, supplier_aux_pn, item_qty, unit_price, item_uom, item_desc, remain_qty, invoice_comment " +
                    "FROM tmpMaster WHERE order_id = ?");

            pStmt.setString(1, orderId);
            rSet = pStmt.executeQuery();

            while ( rSet.next() ){
                HashMap<String, String> masterMap = new HashMap<>();

                masterMap.put("id", String.valueOf(rSet.getInt("id")));
                masterMap.put("supplier_no", String.valueOf(rSet.getInt("supplier_no")));
                masterMap.put("order_killed", String.valueOf(rSet.getString("order_killed")));
                masterMap.put("order_timestamp", String.valueOf(rSet.getString("order_timestamp")));
                masterMap.put("order_id", String.valueOf(rSet.getString("order_id")));
                masterMap.put("order_type", String.valueOf(rSet.getString("order_type")));
                masterMap.put("order_date", String.valueOf(rSet.getString("order_date")));
                masterMap.put("order_total", String.valueOf(rSet.getString("order_total")));
                masterMap.put("shipto_address_id", String.valueOf(rSet.getString("shipto_address_id")));
                masterMap.put("shipto_name", String.valueOf(rSet.getString("shipto_name")));
                masterMap.put("shipto_deliverto", String.valueOf(rSet.getString("shipto_deliverto")));
                masterMap.put("shipto_address_street1", String.valueOf(rSet.getString("shipto_address_street1")));
                masterMap.put("shipto_address_street2", String.valueOf(rSet.getString("shipto_address_street2")));
                masterMap.put("shipto_city", String.valueOf(rSet.getString("shipto_city")));
                masterMap.put("shipto_state", String.valueOf(rSet.getString("shipto_state")));
                masterMap.put("shipto_zip", String.valueOf(rSet.getString("shipto_zip")));
                masterMap.put("line_item_killed", String.valueOf(rSet.getString("line_item_killed")));
                masterMap.put("po_line_no", String.valueOf(rSet.getString("po_line_no")));
                masterMap.put("supplier_pn", String.valueOf(rSet.getString("supplier_pn")));
                masterMap.put("supplier_aux_pn", String.valueOf(rSet.getString("supplier_aux_pn")));
                masterMap.put("item_qty", String.valueOf(rSet.getString("item_qty")));
                masterMap.put("unit_price", String.valueOf(rSet.getString("unit_price")));
                masterMap.put("item_uom", String.valueOf(rSet.getString("item_uom")));
                masterMap.put("item_desc", String.valueOf(rSet.getString("item_desc")));
                masterMap.put("remain_qty", String.valueOf(rSet.getString("remain_qty")));
                masterMap.put("invoice_comment", String.valueOf(rSet.getString("invoice_comment")));


                masterMapList.add(masterMap);
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "getMasterMapList", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "getMasterMapList", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }


        return masterMapList;
    }

    public Integer insertHeader(HashMap masterMap){
        Integer order_header_id = -1;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        Connection newConn = getDataConnection();

        try {
            pStmt = newConn.prepareStatement("INSERT INTO tmpHeaders (supplier_no, order_killed, order_timestamp, order_id, order_type, order_date, order_total, " +
                    "shipto_address_id, shipto_name, shipto_deliverto, shipto_address_street1, shipto_address_street2, shipto_city, shipto_state, shipto_zip) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setInt(1, Integer.valueOf(masterMap.get("supplier_no").toString()));
            pStmt.setString(2, masterMap.get("order_killed").toString());
            pStmt.setString(3, masterMap.get("order_timestamp").toString());
            pStmt.setString(4, masterMap.get("order_id").toString());
            pStmt.setString(5, masterMap.get("order_type").toString());
            pStmt.setString(6, masterMap.get("order_date").toString());
            pStmt.setString(7, masterMap.get("order_total").toString());
            pStmt.setString(8, masterMap.get("shipto_address_id").toString());
            pStmt.setString(9, masterMap.get("shipto_name").toString());
            pStmt.setString(10, masterMap.get("shipto_deliverto").toString());
            pStmt.setString(11, masterMap.get("shipto_address_street1").toString());
            pStmt.setString(12, masterMap.get("shipto_address_street2").toString());
            pStmt.setString(13, masterMap.get("shipto_city").toString());
            pStmt.setString(14, masterMap.get("shipto_state").toString());
            pStmt.setString(15, masterMap.get("shipto_zip").toString());

            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                order_header_id = rSet.getInt(1);
            }


        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "insertHeader", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "insertHeader", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return order_header_id;
    }

    public Integer insertDetail(HashMap masterMap){
        Integer order_detail_id = -1;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        Connection newConn = getDataConnection();

        try {
            pStmt = newConn.prepareStatement("INSERT INTO tmpDetails (order_header_id, line_item_killed, order_id, po_line_no, supplier_no, supplier_pn, supplier_aux_pn, " +
                    "item_qty, unit_price, item_uom, item_desc) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setInt(1, Integer.valueOf(masterMap.get("order_header_id").toString()));
            pStmt.setString(2, masterMap.get("line_item_killed").toString());
            pStmt.setString(3, masterMap.get("order_id").toString());
            pStmt.setString(4, masterMap.get("po_line_no").toString());
            pStmt.setString(5, masterMap.get("supplier_no").toString());
            pStmt.setString(6, masterMap.get("supplier_pn").toString());
            pStmt.setString(7, masterMap.get("supplier_aux_pn").toString());
            pStmt.setString(8, masterMap.get("item_qty").toString());
            pStmt.setString(9, masterMap.get("unit_price").toString());
            pStmt.setString(10, masterMap.get("item_uom").toString());
            pStmt.setString(11, masterMap.get("item_desc").toString());

            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                order_detail_id = rSet.getInt(1);
            }


        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "insertDetail", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "insertDetail", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return order_detail_id;
    }

    public Integer insertInvoice(HashMap masterMap){
        Integer order_detail_id = -1;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        Connection newConn = getDataConnection();

        try {
            pStmt = newConn.prepareStatement("INSERT INTO tmpInvoices (order_id, ow_invoice_status_code_id, po_line_no, remain_qty, invoice_comment) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setString(1, masterMap.get("order_id").toString());
            pStmt.setInt(2, 4);
            pStmt.setString(3, masterMap.get("po_line_no").toString());
            pStmt.setString(4, masterMap.get("remain_qty").toString());
            pStmt.setString(5, masterMap.get("invoice_comment").toString());

            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                order_detail_id = rSet.getInt(1);
            }


        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "insertDetail", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "insertDetail", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return order_detail_id;
    }


    public Integer insertHeaderProd(HashMap masterMap){
        Integer order_header_id = -1;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        Connection newConn = getDataConnection();

        try {
            pStmt = newConn.prepareStatement("INSERT INTO order_headers (supplier_no, order_killed, order_timestamp, order_id, order_type, order_date, order_total, " +
                    "shipto_address_id, shipto_name, shipto_deliverto, shipto_address_street1, shipto_address_street2, shipto_city, shipto_state, shipto_zip) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setInt(1, Integer.valueOf(masterMap.get("supplier_no").toString()));
            pStmt.setString(2, masterMap.get("order_killed").toString());
            pStmt.setString(3, masterMap.get("order_timestamp").toString());
            pStmt.setString(4, masterMap.get("order_id").toString());
            pStmt.setString(5, masterMap.get("order_type").toString());
            pStmt.setString(6, masterMap.get("order_date").toString());
            pStmt.setString(7, masterMap.get("order_total").toString());
            pStmt.setString(8, masterMap.get("shipto_address_id").toString());
            pStmt.setString(9, masterMap.get("shipto_name").toString());
            pStmt.setString(10, masterMap.get("shipto_deliverto").toString());
            pStmt.setString(11, masterMap.get("shipto_address_street1").toString());
            pStmt.setString(12, masterMap.get("shipto_address_street2").toString());
            pStmt.setString(13, masterMap.get("shipto_city").toString());
            pStmt.setString(14, masterMap.get("shipto_state").toString());
            pStmt.setString(15, masterMap.get("shipto_zip").toString());

            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                order_header_id = rSet.getInt(1);
            }


        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "insertHeaderProd", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "insertHeaderProd", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return order_header_id;
    }

    public Integer insertDetailProd(HashMap masterMap){
        Integer order_detail_id = -1;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        Connection newConn = getDataConnection();

        try {
            pStmt = newConn.prepareStatement("INSERT INTO order_details (order_header_id, line_item_killed, order_id, po_line_no, supplier_no, supplier_pn, supplier_aux_pn, " +
                    "item_qty, unit_price, item_uom, item_desc) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setInt(1, Integer.valueOf(masterMap.get("order_header_id").toString()));
            pStmt.setString(2, masterMap.get("line_item_killed").toString());
            pStmt.setString(3, masterMap.get("order_id").toString());
            pStmt.setString(4, masterMap.get("po_line_no").toString());
            pStmt.setString(5, masterMap.get("supplier_no").toString());
            pStmt.setString(6, masterMap.get("supplier_pn").toString());
            pStmt.setString(7, masterMap.get("supplier_aux_pn").toString());
            pStmt.setString(8, masterMap.get("item_qty").toString());
            pStmt.setString(9, masterMap.get("unit_price").toString());
            pStmt.setString(10, masterMap.get("item_uom").toString());
            pStmt.setString(11, masterMap.get("item_desc").toString());

            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                order_detail_id = rSet.getInt(1);
            }


        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "insertDetailProd", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "insertDetailProd", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return order_detail_id;
    }

    public Integer insertInvoiceProd(HashMap masterMap){
        Integer order_detail_id = -1;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        Connection newConn = getDataConnection();

        try {
            pStmt = newConn.prepareStatement("INSERT INTO invoices (order_id, ow_invoice_status_code_id, po_line_no, remain_qty, invoice_comment) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setString(1, masterMap.get("order_id").toString());
            pStmt.setInt(2, 4);
            pStmt.setString(3, masterMap.get("po_line_no").toString());
            pStmt.setString(4, masterMap.get("remain_qty").toString());
            pStmt.setString(5, masterMap.get("invoice_comment").toString());

            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                order_detail_id = rSet.getInt(1);
            }


        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "insertInvoiceProd", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "insertInvoiceProd", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return order_detail_id;
    }

    public Boolean updateProcessFlag(String orderIdGroup, Integer processFlag){
        Boolean updateStatus = false;
        ResultSet rSet=null;
        PreparedStatement pStmt=null;
        Connection newConn = getDataConnection();

        try{
            pStmt = newConn.prepareStatement("update tmpMaster set process_flag = ? where order_id = ?");
            pStmt.setInt(1, processFlag);
            pStmt.setString(2, orderIdGroup);
            pStmt.executeUpdate();

        } catch (SQLException sqlex) {
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "updateProcessFlag", "SQLException", sqlex.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "RecoveryData", "updateProcessFlag", "Exception", exce.getMessage(), "");
        }finally{
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException finex){}
        }


        return updateStatus;
    }

}
