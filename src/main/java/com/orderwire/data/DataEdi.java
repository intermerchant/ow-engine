package com.orderwire.data;

import com.orderwire.logging.UtilLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DataEdi {
    private final String owThreadName;

    public DataEdi(String _threadName){
        owThreadName = _threadName;
    }

    public Integer isEdiFileDuplicate(String _ediFileName){
        Integer primaryKey = -1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("SELECT ow_orderwire_edi_file_id FROM ow_orderwire_edi_files WHERE edi_file_name = ?");
            pStmt.setString(1, _ediFileName);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ) {
                primaryKey = rSet.getInt(1);
            }


        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataEdi", "isEdiFileDuplicate", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataEdi", "isEdiFileDuplicate", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }



        return primaryKey;
    }



    public Integer insertEdi(String _ediFileName, List<String> _fileLines){
        Integer primaryKey = -1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            String fileContent = getEdiRecordString(_fileLines);

            pStmt = newConn.prepareStatement("INSERT INTO ow_orderwire_edi_files (edi_file_name, edi_file_content) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setString(1, _ediFileName);
            pStmt.setString(2, fileContent);
            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                primaryKey = rSet.getInt(1);
            }


        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataEdi", "insertEdi", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataEdi", "insertEdi", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }


        return primaryKey;
    }

    public Integer insertEdiSegmentBig(HashMap<String, String> _bigMap){
        Integer primaryKey = -1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("INSERT INTO ow_orderwire_edi_segment_big (ow_orderwire_edi_file_id, big_date, big_invoice_number, big_date_2, big_purchase_order_number, big_release_number, big_change_order_sequence_number, big_transaction_type_code) " +
                    "VALUES (?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setString(1, _bigMap.get("ow_orderwire_edi_file_id"));
            pStmt.setString(2, _bigMap.get("bigDate"));
            pStmt.setString(3, _bigMap.get("bigInvoiceNumber"));
            pStmt.setString(4, _bigMap.get("bigDate2"));
            pStmt.setString(5, _bigMap.get("bigPurchaseOrderNumber"));
            pStmt.setString(6, _bigMap.get("bigReleaseNumber"));
            pStmt.setString(7, _bigMap.get("bigChangeOrderSequenceNumber"));
            pStmt.setString(8, _bigMap.get("bigTransactionTypeCode"));

            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                primaryKey = rSet.getInt(1);
                UtilLogger.setDbStatus(owThreadName, "DataEdi", "insertEdiSegmentBig", "", "Insert EDI Segment BIG Record", primaryKey.toString());
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataEdi", "insertEdiSegmentBig", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataEdi", "insertEdiSegmentBig", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return primaryKey;
    }

    public Integer insertEdiSegmentIt1(HashMap<String, String> _it1Map){
        Integer primaryKey = -1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try {
            pStmt = newConn.prepareStatement("INSERT INTO ow_orderwire_edi_segment_it1 (ow_orderwire_edi_file_id, it1_assigned_identification, it1_quantity_invoiced, it1_unit_of_measurement_code, it1_unit_price, it1_basis_of_unit_price_code, it1_product_service_id_qualifier, it1_product_service_id) " +
                    "VALUES (?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setString(1, _it1Map.get("ow_orderwire_edi_file_id"));
            pStmt.setString(2, _it1Map.get("it1AssignedIdentification"));
            pStmt.setString(3, _it1Map.get("it1QuantityInvoiced"));
            pStmt.setString(4, _it1Map.get("it1UnitOfMeasurementCode"));
            pStmt.setString(5, _it1Map.get("it1UnitPrice"));
            pStmt.setString(6, _it1Map.get("it1BasisOfUnitPriceCode"));
            pStmt.setString(7, _it1Map.get("it1ProductServiceIdQualifier"));
            pStmt.setString(8, _it1Map.get("it1ProductServiceId"));
            pStmt.executeUpdate();
            rSet = pStmt.getGeneratedKeys();
            if ( rSet.next() ) {
                primaryKey = rSet.getInt(1);
                UtilLogger.setDbStatus(owThreadName, "DataEdi", "insertEdiSegmentIt1", "", "Insert EDI Segment IT1 Record", primaryKey.toString());
            }

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "DataEdi", "insertEdiSegmentIt1", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "DataEdi", "insertEdiSegmentIt1", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return primaryKey;
    }

    private String getEdiRecordString(List<String> fileLines){
        String ediRecordString = "";
        try {
            ediRecordString = fileLines.stream().map(Object::toString).collect(Collectors.joining("\n"));
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "DataEdi", "getEdiRecordString", "", "Exception", exce.getMessage());
        }
        return ediRecordString;
    }

}
