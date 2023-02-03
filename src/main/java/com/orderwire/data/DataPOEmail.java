package com.orderwire.data;

import com.orderwire.logging.UtilLogger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DataPOEmail {
    private final String owThreadName;
    private final Integer orderHeaderId;

    public DataPOEmail(String _threadName, Integer _orderHeaderId){
        owThreadName = _threadName;
        orderHeaderId = _orderHeaderId;
    }

    public HashMap getOrderHeaderMap(){
        HashMap headerMap = new HashMap<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement(
                    "SELECT order_id, order_date, shipto_address_id, shipto_deliverto, shipto_name, shipto_address_street1, shipto_address_street2, shipto_city, shipto_state, shipto_zip, "
                            + "order_contact_name, order_contact_email, order_contact_phone_country_code, order_contact_phone_area_code, order_contact_phone_number, order_contact_phone_extension, ship_charge "
                            + "FROM order_headers "
                            + "WHERE order_header_id = ?");
            pStmt.setInt(1, orderHeaderId);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                Double poSubtotal = getPOSubTotal();
                Double poTax = 0.00;
                Double poShipping = rSet.getDouble("ship_charge");
                Double poOther = 0.00;
                Double poTotal =  Double.valueOf(poSubtotal.doubleValue() + poTax.doubleValue() + poShipping.doubleValue() + poOther.doubleValue());

                String po_shipping = String.format("%.2f", poShipping);
                String po_subtotal = String.format("%.2f", poSubtotal);
                String po_tax = String.format("%.2f", poTax);
                String po_other = String.format("%.2f", poOther);
                String po_total = String.format("%.2f", poTotal);

                headerMap.put("owThreadName", owThreadName);
                headerMap.put("orderHeaderId", orderHeaderId);
                headerMap.put("order_id", String.valueOf(rSet.getString("order_id")));
                headerMap.put("order_date", formatOrderDate(rSet.getString("order_date")));
                headerMap.put("shipto_address_id", rSet.getString("shipto_address_id"));
                headerMap.put("shipto_deliverto", rSet.getString("shipto_deliverto"));
                headerMap.put("shipto_name", rSet.getString("shipto_name"));
                headerMap.put("shipto_address_street1", rSet.getString("shipto_address_street1"));
                headerMap.put("shipto_address_street2", rSet.getString("shipto_address_street2"));
                headerMap.put("shipto_city", rSet.getString("shipto_city"));
                headerMap.put("shipto_state", rSet.getString("shipto_state"));
                headerMap.put("shipto_zip", rSet.getString("shipto_zip"));
                headerMap.put("order_contact_name", rSet.getString("order_contact_name"));
                headerMap.put("order_contact_email", rSet.getString("order_contact_email"));
                headerMap.put("order_contact_phone_country_code", rSet.getString("order_contact_phone_country_code"));
                headerMap.put("order_contact_phone_area_code", rSet.getString("order_contact_phone_area_code"));
                headerMap.put("order_contact_phone_number", rSet.getString("order_contact_phone_number"));
                headerMap.put("order_contact_phone_extension", rSet.getString("order_contact_phone_extension"));
                headerMap.put("po_shipping", po_shipping);
                headerMap.put("po_subtotal", po_subtotal);
                headerMap.put("po_tax", po_tax);
                headerMap.put("po_other", po_other);
                headerMap.put("po_total", po_total);

                String shipto_csp = rSet.getString("shipto_city") + ", " + rSet.getString("shipto_state") + " " + rSet.getString("shipto_zip");
                headerMap.put("shipto_csp", shipto_csp);

                String order_contact_area_phone_ext = ((rSet.getString("order_contact_phone_area_code") == null ) ? "" : rSet.getString("order_contact_phone_area_code")) + " " + ((rSet.getString("order_contact_phone_number") == null ) ? "" : rSet.getString("order_contact_phone_number")) + " " + ((rSet.getString("order_contact_phone_extension").length() == 0 ) ? "" : " x" +rSet.getString("order_contact_phone_extension"));
                headerMap.put("order_contact_area_phone_ext", order_contact_area_phone_ext);
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getOrderHeaderMap", "No Data Found Exception", "No Data Found", "No Data Found");
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getOrderHeaderMap", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getOrderHeaderMap", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return headerMap;
    }

    private Double getPOSubTotal(){
        Double poTotal = 0.00;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement(
                    "SELECT sum(item_qty * unit_price) as TOTAL FROM order_details WHERE line_item_killed = 'N' AND order_header_id = ?");
            pStmt.setInt(1, orderHeaderId);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                poTotal = rSet.getDouble("TOTAL");
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getPOTotal", "No Data Found Exception", "No Data Found", orderHeaderId.toString());
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getPOTotal", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getPOTotal", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return poTotal;
    }

    public List<HashMap> getOrderDetailMap(){
        List<HashMap> orderDetailList = new ArrayList<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {

            // i need join catalog_times via the supplier_id and supplier_pn  will be the supplier_aux_pn to display on email.

            pStmt = newConn.prepareStatement("SELECT od.order_detail_id, od.order_header_id, od.order_id, od.po_line_no, ci.supplier_aux_pn, od.supplier_pn, od.item_desc, od.item_qty, od.item_uom, od.unit_price "
                    + "FROM order_details od "
                    + "INNER JOIN catalog_items ci ON od.supplier_no = ci.supplier_no "
                    + "AND ci.supplier_pn = od.supplier_pn "
                    + "WHERE order_header_id = ?");

            /*
            pStmt = newConn.prepareStatement("SELECT order_detail_id, order_header_id, order_id, po_line_no, supplier_aux_pn, supplier_pn, item_desc, item_qty, item_uom, unit_price "
                    + "FROM order_details "
                    + "WHERE order_header_id = ?");
             */
            pStmt.setInt(1, orderHeaderId);
            rSet = pStmt.executeQuery();

            while ( rSet.next() ){
                HashMap<String, String> orderDetailMap = new HashMap<>();
                String supplierAuxPnString = rSet.getString("supplier_aux_pn");
                String supplierAuxPn = substituteNull(supplierAuxPnString);

                String supplierPnString = rSet.getString("supplier_pn");
                String supplierPn = substituteNull(supplierPnString);
                String po_item_no = supplierAuxPn + " / " + supplierPn;

                Double itemQty = rSet.getDouble("item_qty");
                String item_qty = String.format("%.2f", itemQty);

                Double unitPrice = rSet.getDouble("unit_price");
                String unit_price = String.format("%.2f", unitPrice);

                Double totalUnitPrice = (rSet.getDouble("item_qty") * rSet.getDouble("unit_price"));
                String total_unit_price = String.format("%.2f", totalUnitPrice);

                orderDetailMap.put("owThreadName", owThreadName);
                orderDetailMap.put("order_detail_id", String.valueOf(rSet.getInt("order_detail_id")));
                orderDetailMap.put("order_header_id", String.valueOf(rSet.getInt("order_header_id")));
                orderDetailMap.put("order_id", String.valueOf(rSet.getString("order_id")));
                orderDetailMap.put("po_line_no", String.valueOf(rSet.getInt("po_line_no")));
                orderDetailMap.put("po_item_no", po_item_no);
                orderDetailMap.put("item_desc", rSet.getString("item_desc"));
                orderDetailMap.put("item_qty", item_qty);
                orderDetailMap.put("item_uom", rSet.getString("item_uom"));
                orderDetailMap.put("unit_price", unit_price);
                orderDetailMap.put("total_unit_price", total_unit_price);

                orderDetailList.add(orderDetailMap);
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getOrderDetailMap", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getOrderDetailMap", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return orderDetailList;
    }


    public HashMap getOrderSupplierMap(){
        HashMap supplierMap = new HashMap<>();
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT sp.supplier_id, sp.supplier_no, sp.supplier_name, sp.supplier_contact, sp.supplier_addr1, sp.supplier_addr2, sp.supplier_city, sp.supplier_state, sp.supplier_zip, sp.supplier_phone, sp.supplier_fax, "
                    + "oh.order_id "
                    + "FROM suppliers sp "
                    + "INNER JOIN order_headers oh ON sp.supplier_no = oh.supplier_no "
                    + "WHERE oh.order_header_id = ?");
            pStmt.setInt(1, orderHeaderId);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                supplierMap.put("owThreadName", owThreadName);
                supplierMap.put("order_id", ((rSet.getString("order_id") == null ) ? "" : rSet.getString("order_id")));
                supplierMap.put("supplier_id", ((rSet.getString("supplier_id") == null ) ? "" : rSet.getString("supplier_id")));
                supplierMap.put("supplier_no", ((rSet.getString("supplier_no") == null ) ? "" : rSet.getString("supplier_no")));
                supplierMap.put("supplier_name", ((rSet.getString("supplier_name") == null ) ? "" : rSet.getString("supplier_name")));
                supplierMap.put("supplier_contact", ((rSet.getString("supplier_contact") == null ) ? "" : rSet.getString("supplier_contact")));
                supplierMap.put("supplier_addr1", ((rSet.getString("supplier_addr1") == null ) ? "" : rSet.getString("supplier_addr1")));
                supplierMap.put("supplier_addr2", ((rSet.getString("supplier_addr2") == null ) ? "" : rSet.getString("supplier_addr2")));
                supplierMap.put("supplier_city", ((rSet.getString("supplier_city") == null ) ? "" : rSet.getString("supplier_city")));
                supplierMap.put("supplier_state", ((rSet.getString("supplier_state") == null ) ? "" : rSet.getString("supplier_state")));
                supplierMap.put("supplier_zip", ((rSet.getString("supplier_zip") == null ) ? "" : rSet.getString("supplier_zip")));
                supplierMap.put("supplier_phone", ((rSet.getString("supplier_phone") == null ) ? "" : rSet.getString("supplier_phone")));
                supplierMap.put("supplier_fax", ((rSet.getString("supplier_fax") == null) ? "" : rSet.getString("supplier_fax")));
                String supplier_csp = ((rSet.getString("supplier_city") == null) ? "" : rSet.getString("supplier_city")) + ", " + ((rSet.getString("supplier_state") == null) ? "" : rSet.getString("supplier_state")) + " " +  ((rSet.getString("supplier_zip") == null ) ? "" : rSet.getString("supplier_zip"));
                supplierMap.put("supplier_csp", supplier_csp);
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getOrderSupplierMap", "No Data Found Exception", "No Data Found", "No Data Found");
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getOrderSupplierMap", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getOrderSupplierMap", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return supplierMap;
    }

    public HashMap getEmailDetails(Integer supplierId){
        HashMap emailMap = new HashMap<>();
        String imageEncoded = "";
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT po_header_text, po_image, email_to, email_cc, email_bcc, email_subject "
                    + "FROM supplier_po_email_templates "
                    + "WHERE supplier_id = ?");
            pStmt.setInt(1, supplierId);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                emailMap.put("owThreadName", owThreadName);
                emailMap.put("po_header_text", substituteNull(rSet.getString("po_header_text")));
                Blob poImage = rSet.getBlob("po_image");
                String encodedImage = Base64.getEncoder().encodeToString(poImage.getBytes(1, (int) poImage.length()));
                emailMap.put("po_image", encodedImage);
                emailMap.put("email_to", substituteNull(rSet.getString("email_to")));
                emailMap.put("email_cc", substituteNull(rSet.getString("email_cc")));
                emailMap.put("email_bcc", substituteNull(rSet.getString("email_bcc")));

                emailMap.put("email_subject", substituteNull(rSet.getString("email_subject")));

            } else {
                UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getSupplierImage", "No Data Found Exception", "No Data Found", "No Data Found");
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getSupplierImage", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getSupplierImage", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return emailMap;

    }


    public String getInfomeldLogo(){
        String logoEncoded = "";
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT infomeld_logo "
                    + "FROM ow_orderwire_infomeld "
                    + "WHERE ow_orderwire_infomeld_id = ?");
            pStmt.setInt(1, 1);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                Blob infomeldBlob = rSet.getBlob("infomeld_logo");
                logoEncoded = Base64.getEncoder().encodeToString(infomeldBlob.getBytes(1, (int) infomeldBlob.length()));
            } else {
                UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getInfomeldLogo", "No Data Found Exception", "No Data Found", "No Data Found");
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getInfomeldLogo", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "getInfomeldLogo", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return logoEncoded;

    }


    public Integer insertEmailMessage(Integer orderHeaderId, String emailHtml){
        Integer order_po_email_id = -1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("INSERT INTO order_po_emails (order_header_id, email_html) VALUES (?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setInt(1, orderHeaderId);
            pStmt.setString(2, emailHtml);

            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                order_po_email_id = rSet.getInt(1);
            }


        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "insertEmailMessage", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "insertEmailMessage", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return order_po_email_id;
    }

    private String substituteNull(String inputString){
        String returnString = "";

        try {
            if (inputString.equals("null")) {
                returnString = "";
            } else {
                returnString = inputString == null ? "" : inputString;
            }
        } catch (Exception exce){
            returnString = "";
        }
        return returnString;
    }

    private String formatOrderDate(String utcDate){
        String formattedDate = "";
        try {
            Instant time = Instant.parse(utcDate);
            java.util.Date myDate = Date.from(time);
            String myPattern = "MM-dd-yyyy";
            SimpleDateFormat formatter = new SimpleDateFormat(myPattern);
            formattedDate = formatter.format(myDate);
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "DataPOEmail", "formatOrderDate", "Exception", exce.getMessage(), "");
        }
        return formattedDate;
    }


}