package com.orderwire.data;

import com.orderwire.logging.UtilLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplierData {
    private final String supplerSharedSecret;
    private final String owThreadName;
    
    public SupplierData(String _supplierSharedSecret, String _threadName){
        supplerSharedSecret = _supplierSharedSecret;
        owThreadName = _threadName;
    }
    
    
    
    public Integer getSupplierNo(){
        Integer supplierNo = -1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;        
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();
        
        
        try{
            pStmt = newConn.prepareStatement("SELECT supplier_no FROM suppliers WHERE supplier_sharedsecret = ?");
            pStmt.setString(1, supplerSharedSecret);            
            rSet = pStmt.executeQuery();
            if ( rSet.next() ){
                supplierNo = rSet.getInt("supplier_no");
                UtilLogger.setDbStatus(owThreadName, "SupplierData", "getSupplierNo", "", "Supplier Number", supplierNo.toString());
            } else {
                UtilLogger.setDbStatus(owThreadName, "SupplierData", "getSupplierNo", "-1", "Supplier Number", "NOT FOUND");                
            }
        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "SupplierData", "getSupplierNo", "SQLException", sqle.getMessage(), "");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "SupplierData", "getSupplierNo", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}                
            } catch (SQLException e){ }
        }
        return supplierNo;
    }       
}
