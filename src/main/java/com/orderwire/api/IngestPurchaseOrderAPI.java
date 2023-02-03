package com.orderwire.api;


import com.orderwire.data.DataCommon;
import com.orderwire.data.DataConnection;
import com.orderwire.data.InsertPurchaseOrder;
import com.orderwire.data.SupplierData;
import com.orderwire.excp.PurchaseOrderDuplicateException;
import com.orderwire.excp.PurchaseOrderFileExistsException;
import com.orderwire.excp.PurchaseOrderTransactionException;
import com.orderwire.logging.UtilLogger;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
// import jakarta.xml.bind.JAXBContext;
//import jakarta.xml.bind.JAXBException;
//import jakarta.xml.bind.Unmarshaller;
import com.orderwire.cxml.dll.*;

public class IngestPurchaseOrderAPI {
    private final String owThreadName;
    private final Integer owTaskSubscribeId;
    private final Integer owLogId;

    public IngestPurchaseOrderAPI(String threadName, Integer TaskSubscribeId, Integer LogId){
        owThreadName = threadName;
        owTaskSubscribeId = TaskSubscribeId;
        owLogId = LogId;
    }

    public Integer IngestPurchaseOrdersLogs(){
        Integer procStatus = 1;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try{

            /*      ONLY USED TO TEST A SINGLE PO
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "IngestPurchaseOrdersLogs", "", "CHANGE QUERY", "CHANGE QUERY - LINE 44");
            pStmt = newConn.prepareStatement("SELECT log_id, payload_id, order_id, supplier_id, payload_filename FROM ow_orderwire_logs WHERE log_id = ?");
            pStmt.setInt(1, 5726);
            */

            pStmt = newConn.prepareStatement("SELECT log_id, payload_id, order_id, supplier_id, payload_filename FROM ow_orderwire_logs WHERE log_id = ?");
            pStmt.setInt(1, owLogId);
            rSet = pStmt.executeQuery();

            if ( rSet.next() ){
                UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "IngestPurchaseOrdersLogs", "", "Start Task Subscribed", owTaskSubscribeId.toString());
                Integer _logId = rSet.getInt("log_id");
                String _payloadId = rSet.getString("payload_id");
                String _orderId = rSet.getString("order_id");
                String _supplierId = rSet.getString("supplier_id");
                String _payloadFilename = rSet.getString("payload_filename");
                Integer _logStatusCodeId = -1;
                UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "IngestPurchaseOrdersLogs", "", "Orderwire Log Id", _logId.toString());

                /* ******** Does File Exist on File System **************** */
                Boolean _fileExists = fileExistsCheck(_payloadFilename);
                if (!_fileExists){
                    _logStatusCodeId = 6;
                    Boolean updateFileExistStatus = updateLogStatus(_logId, _logStatusCodeId);
                    UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "IngestPurchaseOrdersLogs", "", "PO File Does Not Exist on File System", _logId.toString());
                    throw new PurchaseOrderFileExistsException();
                }


                /* ******** Is File Duplicate **************** */
                Boolean _isDuplicate = duplicatePOCheck(_payloadId, _orderId, _supplierId);
                if(_isDuplicate){
                    _logStatusCodeId = 3;
                    Boolean updateDuplicateStatue = updateLogStatus(_logId, _logStatusCodeId);     //Duplicate PO
                    UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "IngestPurchaseOrdersLogs", "", "Duplicate PO File", _logId.toString());
                    throw new PurchaseOrderDuplicateException();
                }

                /* ************* Parse Files *******************  */
                _logStatusCodeId = 2;
                Boolean updateStatus = updateLogStatus(_logId, _logStatusCodeId);              // Ingesting PO File
                UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "IngestPurchaseOrdersLogs", "", "Start Parse PO XML File", _payloadFilename);
                Boolean parseStatus = ParseXMLPurchaseOrder(_payloadFilename);
                if (parseStatus){
                    _logStatusCodeId = 4;
                    Boolean updateInsertStatus = updateLogStatus(_logId, _logStatusCodeId);
                }else{
                    _logStatusCodeId = 5;
                    Boolean updateInsertStatus = updateLogStatus(_logId, _logStatusCodeId);
                }

            } else{
                procStatus = 3;
            }
        } catch (PurchaseOrderFileExistsException pofee){
            procStatus = 2;
        } catch (PurchaseOrderDuplicateException pode){
            procStatus = 2;
        } catch (SQLException sqle) {
            procStatus = 2;
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "IngestPurchaseOrdersLogs", "SQLException", sqle.getMessage(), sqle.getSQLState());
        } catch (Exception exce){
            procStatus = 2;
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "IngestPurchaseOrdersLogs", "Exception", exce.getMessage(), "");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return procStatus;
    }

    private String getPOUploadFolder(){
        String folderPathFile = "";

        try{
            String os = System.getProperty("os.name");
            if (os.toLowerCase().contains("windows")){
                String folderPathWindows = "C:\\temp\\";
                folderPathFile = folderPathWindows;
            } else if(os.toLowerCase().contains("linux")){
                DataCommon dc = new DataCommon(owTaskSubscribeId, owThreadName);
                String folderPathLinux = dc.getPOUploadFilePathLinux();
                folderPathFile = folderPathLinux;
            }
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "getPOUploadFolder", "Exception", exce.getMessage(),"");
        }

        return folderPathFile;
    }

    private Boolean fileExistsCheck(String xmlFileName){
        Boolean procStatus = false;

        try{
            String folderPathFile = "";
            String os = System.getProperty("os.name");
            if (os.toLowerCase().contains("windows")){
                String folderPathWindows = "C:\\temp\\";
                folderPathFile = folderPathWindows + xmlFileName;
            } else if(os.toLowerCase().contains("linux")){
                DataCommon dc = new DataCommon();
                String folderPathLinux = dc.getPOUploadFilePathLinux();
                folderPathFile = folderPathLinux + xmlFileName;
            }

            Path path = Paths.get(folderPathFile);
            if(Files.exists(path)){
                procStatus = true;
            }else{
                UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "IngestPurchaseOrdersLogs", "PurchaseOrderFileExistsException", "PO File Does Not Exist on File System", folderPathFile);
            }
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "fileExistsCheck", "Exception", exce.getMessage(),"");
        }

        return procStatus;
    }

    private Boolean duplicatePOCheck(String _payload_id, String _order_id, String _supplier_id){
        Boolean procStatus = false;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try{
            pStmt = newConn.prepareStatement("SELECT log_id FROM ow_orderwire_logs WHERE ow_log_status_code_id = ? AND payload_id = ? AND order_id = ? AND supplier_id = ? AND payload_type = ?");
            pStmt.setInt(1, 4);
            pStmt.setString(2, _payload_id);
            pStmt.setString(3, _order_id);
            pStmt.setString(4, _supplier_id);
            pStmt.setString(5, "PO");

            rSet = pStmt.executeQuery();
            if ( rSet.next() ){
                procStatus = true;
            }
        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "duplicatePOCheck", "SQLException", sqle.getMessage(),"");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "duplicatePOCheck", "Exception", exce.getMessage(),"");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }


        return procStatus;
    }

    private Boolean updateLogStatus(Integer _logId, Integer _logStatusCodeId){
        Boolean procStatus = true;
        PreparedStatement pStmt=null;
        ResultSet rSet=null;
        DataConnection dataConn = new DataConnection();
        Connection newConn = dataConn.getOrderwireConnection();

        try{
            pStmt = newConn.prepareStatement("UPDATE ow_orderwire_logs SET ow_log_status_code_id = ? WHERE log_id = ?");
            pStmt.setInt(1, _logStatusCodeId);
            pStmt.setInt(2, _logId);
            pStmt.executeUpdate();

        } catch (SQLException sqle) {
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "updateLogStatus", "SQLException", sqle.getMessage(),"");
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "updateLogStatus", "Exception", exce.getMessage(),"");
        }finally {
            try {
                if (rSet != null){rSet.close();}
                if (pStmt != null){pStmt.close();}
                if (newConn != null){newConn.close();}
            } catch (SQLException e){ }
        }

        return procStatus;
    }

    private String formatPostalCode(String _postalCode) {
        String formattedPostalCode = "";
        Integer postalCodeLength = _postalCode.length();

        if (postalCodeLength.equals(4)) {
            formattedPostalCode = "0" + _postalCode;
        } else if (postalCodeLength.equals(9)) {
            formattedPostalCode = "0" + _postalCode;
        } else {
            formattedPostalCode = _postalCode;
        }

        return formattedPostalCode;
    }

    private Boolean ParseXMLPurchaseOrder(String xmlFileName){
        Boolean procStatus = true;
        Connection newConn = null;
        Integer _orderHeaderPK = -1;

        UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "Start Transaction", "");
        try {
            File xmlFile = null;
            String xmlPOFilePath = "";
            try {
                String poUploadFolder = getPOUploadFolder();
                xmlPOFilePath = poUploadFolder + xmlFileName;
                xmlFile = new File(xmlPOFilePath);
            } catch (Exception exce) {
                UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "Exception", "xml PO File Path Exception", exce.getMessage());
            } finally {
                UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "xml PO File Path", xmlPOFilePath);
            }


            JAXBContext jaxbContext = JAXBContext.newInstance(CXML.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            CXML cxmlType = (CXML) jaxbUnmarshaller.unmarshal(xmlFile);
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "Unmarshal CXML", "");



            List<HashMap> segmentList = new ArrayList<>();

            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "Build Order Request List", "");
            List<OrderRequest> orTypeList = cxmlType.getRequest().getOrderRequest();
            for (Iterator<OrderRequest> it = orTypeList.iterator(); it.hasNext();) {
                OrderRequest orderRequestType = it.next();
                HashMap<String,String> headerMap = new HashMap<>();
                //String _supplierIdentity = cxmlType.getHeader().getTo().getCredential().getIdentity();
                String _supplierSharedSecret = cxmlType.getHeader().getSender().getCredential().getSharedSecret();
                OrderRequestHeader orderRequestHeader = orderRequestType.getOrderRequestHeader();

                String _orderId = String.valueOf(orderRequestHeader.getOrderID());
                String _orderDate = String.valueOf(orderRequestHeader.getOrderDate());
                String _orderType =  String.valueOf(orderRequestHeader.getOrderType());
                String _orderTotal = String.valueOf(orderRequestHeader.getTotal().getMoney().getValue());

                String _shipToAddressIsoCountryCode = String.valueOf(orderRequestHeader.getShipTo().getAddress().getIsoCountryCode());
                String _shipToAddressId = String.valueOf(orderRequestHeader.getShipTo().getAddress().getAddressID());

                String _shipToName = String.valueOf(orderRequestHeader.getShipTo().getAddress().getName().getContent());

                // Deliver Iterator
                List<String> _shipToDeliverToList = orderRequestHeader.getShipTo().getAddress().getPostalAddress().getDeliverTo();
                String _shipToDeliverToA = "";
                for (int i=0; i<_shipToDeliverToList.size(); i++){
                    if(i == 0) {
                        _shipToDeliverToA = _shipToDeliverToList.get(i);
                    }
                }

                // Street Iterator
                List<String> _shipToStreetList = orderRequestHeader.getShipTo().getAddress().getPostalAddress().getStreet();
                String _shipToStreetA = "";
                String _shipToStreetB = "";
                for (int i=0; i<_shipToStreetList.size(); i++){
                    if(i == 0) {
                        _shipToStreetA = _shipToStreetList.get(i);
                    }
                    if(i == 1) {
                        _shipToStreetB = _shipToStreetList.get(i);
                    }
                }

                //String _shipToStreet = String.valueOf(orderRequestHeader.getShipTo().getAddress().getPostalAddress().getStreet());
                String _shipToCity = String.valueOf(orderRequestHeader.getShipTo().getAddress().getPostalAddress().getCity());
                String _shipToState = String.valueOf(orderRequestHeader.getShipTo().getAddress().getPostalAddress().getState());
                String _shipToPostalCode = String.valueOf(orderRequestHeader.getShipTo().getAddress().getPostalAddress().getPostalCode());
                String _shipToCountry = String.valueOf(orderRequestHeader.getShipTo().getAddress().getPostalAddress().getCountry().getContent());
                String _shipToEmail = String.valueOf(orderRequestHeader.getShipTo().getAddress().getEmail().getContent());

                String _shipToPhoneCountryCode = String.valueOf(orderRequestHeader.getShipTo().getAddress().getPhone().getTelephoneNumber().getCountryCode().getValue());
                String _shipToPhoneAreaCode = String.valueOf(orderRequestHeader.getShipTo().getAddress().getPhone().getTelephoneNumber().getAreaOrCityCode());
                String _shipToPhoneNumber = String.valueOf(orderRequestHeader.getShipTo().getAddress().getPhone().getTelephoneNumber().getNumber());
                String _shipToPhoneExtension = String.valueOf(orderRequestHeader.getShipTo().getAddress().getPhone().getTelephoneNumber().getExtension());


                String _billToAddressIsoCountryCode = String.valueOf(orderRequestHeader.getBillTo().getAddress().getIsoCountryCode());
                String _billToAddressId = String.valueOf(orderRequestHeader.getBillTo().getAddress().getAddressID());
                String _billToName = String.valueOf(orderRequestHeader.getBillTo().getAddress().getName().getContent());

                // Deliver Iterator
                List<String> _billToDeliverToList = orderRequestHeader.getBillTo().getAddress().getPostalAddress().getDeliverTo();
                String _billToDeliverToA = "";
                for (int i=0; i<_billToDeliverToList.size(); i++){
                    if(i == 0) {
                        _billToDeliverToA = _billToDeliverToList.get(i);
                    }
                }



                // Street Iterator
                List<String> _billToStreetList = orderRequestHeader.getBillTo().getAddress().getPostalAddress().getStreet();
                String _billToStreetA = "";
                String _billToStreetB = "";
                for (int i=0; i<_billToStreetList.size(); i++){
                    if(i == 0) {
                        _billToStreetA = _billToStreetList.get(i);
                    }
                    if(i == 1) {
                        _billToStreetA = _billToStreetList.get(i);
                    }
                }



                //String _billToStreet = String.valueOf(orderRequestHeader.getBillTo().getAddress().getPostalAddress().getStreet());
                String _billToCity = String.valueOf(orderRequestHeader.getBillTo().getAddress().getPostalAddress().getCity());
                String _billToState = String.valueOf(orderRequestHeader.getBillTo().getAddress().getPostalAddress().getState());
                String _billToPostalCode = String.valueOf(orderRequestHeader.getBillTo().getAddress().getPostalAddress().getPostalCode());
                String _billToCountry = String.valueOf(orderRequestHeader.getBillTo().getAddress().getPostalAddress().getCountry().getContent());
                String _billToEmail = String.valueOf(orderRequestHeader.getBillTo().getAddress().getEmail().getContent());

                String _billToPhoneCountryCode = String.valueOf(orderRequestHeader.getBillTo().getAddress().getPhone().getTelephoneNumber().getCountryCode().getValue());
                String _billToPhoneAreaCode = String.valueOf(orderRequestHeader.getBillTo().getAddress().getPhone().getTelephoneNumber().getAreaOrCityCode());
                String _billToPhoneNumber = String.valueOf(orderRequestHeader.getBillTo().getAddress().getPhone().getTelephoneNumber().getNumber());
                String _billToPhoneExtension = String.valueOf(orderRequestHeader.getBillTo().getAddress().getPhone().getTelephoneNumber().getExtension());


                String _shipCharge = String.valueOf(orderRequestHeader.getShipping().getMoney().getValue());

                String _shipDescription = "";
                List<String> shipDescriptionList = orderRequestHeader.getShipping().getDescription().getContent();
                for(String content : shipDescriptionList){
                    _shipDescription = _shipDescription + content;
                }
                _shipDescription = _shipDescription.trim();

                String _contactName = String.valueOf(orderRequestHeader.getContact().get(0).getName().getContent());
                String _contactEmail = String.valueOf(orderRequestHeader.getContact().get(0).getEmail().get(0).getContent());
                String _contactPhoneCountryCode = String.valueOf(orderRequestHeader.getContact().get(0).getPhone().get(0).getTelephoneNumber().getCountryCode().getValue());
                String _contactPhoneAreaCode = String.valueOf(orderRequestHeader.getContact().get(0).getPhone().get(0).getTelephoneNumber().getAreaOrCityCode());
                String _contactPhoneNumber = String.valueOf(orderRequestHeader.getContact().get(0).getPhone().get(0).getTelephoneNumber().getNumber());
                String _contactPhoneExtension = String.valueOf(orderRequestHeader.getContact().get(0).getPhone().get(0).getTelephoneNumber().getExtension());

                UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "Build Header Map", "");
                headerMap.put("_payloadFilename", xmlFileName);
                //headerMap.put("_supplierIdentity", _supplierIdentity);
                headerMap.put("_payloadTimestamp", cxmlType.getTimestamp());
                headerMap.put("_orderId", _orderId);
                headerMap.put("_orderDate", _orderDate);
                headerMap.put("_orderType", _orderType);
                headerMap.put("_orderTotal", _orderTotal);

                headerMap.put("_shipToAddressIsoCountryCode", _shipToAddressIsoCountryCode);
                headerMap.put("_shipToAddressId", _shipToAddressId);
                headerMap.put("_shipToName", _shipToName);
                headerMap.put("_shipToDeliverTo", _shipToDeliverToA);
                headerMap.put("_shipToStreetA", _shipToStreetA);
                headerMap.put("_shipToStreetB", _shipToStreetB);

                headerMap.put("_shipToCity", _shipToCity);
                headerMap.put("_shipToState", _shipToState);
                headerMap.put("_shipToPostalCode", _shipToPostalCode);
                headerMap.put("_shipToCountry", _shipToCountry);
                headerMap.put("_shipToEmail", _shipToEmail);

                headerMap.put("_shipToPhoneCountryCode", _shipToPhoneCountryCode);
                headerMap.put("_shipToPhoneAreaCode", _shipToPhoneAreaCode);
                headerMap.put("_shipToPhoneNumber", _shipToPhoneNumber);
                headerMap.put("_shipToPhoneExtension", _shipToPhoneExtension);


                headerMap.put("_billToAddressIsoCountryCode", _billToAddressIsoCountryCode);
                headerMap.put("_billToAddressId", _billToAddressId);
                headerMap.put("_billToName", _billToName);
                headerMap.put("_billToDeliverTo", _billToDeliverToA);
                headerMap.put("_billToStreetA", _billToStreetA);
                headerMap.put("_billToStreetB", _billToStreetB);

                headerMap.put("_billToCity", _billToCity);
                headerMap.put("_billToState", _billToState);
                headerMap.put("_billToPostalCode", _billToPostalCode);
                headerMap.put("_billToCountry", _billToCountry);
                headerMap.put("_billToEmail", _billToEmail);

                headerMap.put("_billToPhoneCountryCode", _billToPhoneCountryCode);
                headerMap.put("_billToPhoneAreaCode", _billToPhoneAreaCode);
                headerMap.put("_billToPhoneNumber", _billToPhoneNumber);
                headerMap.put("_billToPhoneExtension", _billToPhoneExtension);

                headerMap.put("_shipCharge", _shipCharge);
                headerMap.put("_shipDescription", _shipDescription);


                headerMap.put("_contactName", _contactName);
                headerMap.put("_contactEmail", _contactEmail);

                headerMap.put("_contactPhoneCountryCode", _contactPhoneCountryCode);
                headerMap.put("_contactPhoneAreaCode", _contactPhoneAreaCode);
                headerMap.put("_contactPhoneNumber", _contactPhoneNumber);
                headerMap.put("_contactPhoneExtension", _contactPhoneExtension);


                SupplierData supplierData = new SupplierData(_supplierSharedSecret, owThreadName);
                Integer _supplierNo = supplierData.getSupplierNo();
                headerMap.put("_supplierNo", _supplierNo.toString());
                headerMap.put("_threadName", owThreadName);



                /* *************** Transaction Data Connection ********************** */
                DataConnection dataConn = new DataConnection();
                newConn = dataConn.getOrderwireConnection();
                newConn.setAutoCommit(false);

                /* ************************************************************
                                    INSERT PO HEADER
                * ************************************************************/
                //UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "Insert PO Header ", "");
                InsertPurchaseOrder ipoh = new InsertPurchaseOrder(headerMap, newConn);
                _orderHeaderPK = ipoh.insertOrderHeader();
                if (_orderHeaderPK.equals(-1)){
                    throw new PurchaseOrderTransactionException("Purchase Order Header Fail");
                }


                /* ********************** ITEM OUT TYPE ***********************/
                UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "Build Item Out List", "");
                List<ItemOut> itemOrderTypeList = orderRequestType.getItemOut();
                for (Iterator<ItemOut> io = itemOrderTypeList.iterator(); io.hasNext();) {
                    ItemOut itemOutType = io.next();
                    HashMap<String,String> detailMap = new HashMap<>();

                    String _itemQuantity = String.valueOf(itemOutType.getQuantity());
                    String _poLineNumber = String.valueOf(itemOutType.getLineNumber());
                    String _itemIdSupplierPartId = String.valueOf(itemOutType.getItemID().getSupplierPartID());

                    // now represents Contract Number
                    String _itemIdSupplierPartAuxiliaryId = String.valueOf(itemOutType.getItemID().getSupplierPartAuxiliaryID());
                    String _unitPrice = String.valueOf(itemOutType.getItemDetail().getUnitPrice().getMoney().getValue());

                    String _itemDescription = "";
                    List<Description> itemDescriptionList = itemOutType.getItemDetail().getDescription();
                    for(Description content : itemDescriptionList){
                        _itemDescription = _itemDescription + content.getContent().get(0);
                    }
                    _itemDescription = _itemDescription.trim();
                    String _unitOfMeasure = itemOutType.getItemDetail().getUnitOfMeasure();
                    String _classification = "";
                    List<Classification> classificationList = itemOutType.getItemDetail().getClassification();
                    for(Classification content : classificationList){
                        _classification = _classification + content.getValue();
                    }
                    _classification = _classification.trim();

                    String _manufacturerPartId = itemOutType.getItemDetail().getManufacturerPartID();
                    String _manufacturerName = "";
                    try {
                        _manufacturerName = itemOutType.getItemDetail().getManufacturerName().getValue();
                    } catch (Exception exce){
                        // do nothing  _manufacturerName is set to zero length string.
                    }

                    String _extrinsicName = "";
                    String _extrinsicValue = "";
                    List<Extrinsic> extrinsicList = itemOutType.getItemDetail().getExtrinsic();
                    for(Extrinsic content : extrinsicList){
                        _extrinsicName = content.getName();
                        _extrinsicValue = content.getValue();
                    }

                    UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "Build Detail Map", "");
                    detailMap.put("_orderHeaderPK", _orderHeaderPK.toString());
                    detailMap.put("_orderId", _orderId);
                    detailMap.put("_supplierNo", _supplierNo.toString());
                    detailMap.put("_itemQuantity", _itemQuantity);
                    detailMap.put("_poLineNumber", _poLineNumber);
                    detailMap.put("_itemIdSupplierPartId", _itemIdSupplierPartId);
                    detailMap.put("_itemIdSupplierPartAuxiliaryId", _itemIdSupplierPartAuxiliaryId);
                    detailMap.put("_unitPrice", _unitPrice);
                    detailMap.put("_itemDescription", _itemDescription);
                    detailMap.put("_unitOfMeasure", _unitOfMeasure);
                    detailMap.put("_classification", _classification);
                    detailMap.put("_manufacturerPartId", _manufacturerPartId);
                    detailMap.put("_manufacturerName", _manufacturerName);
                    detailMap.put("_extrinsicName", _extrinsicName);
                    detailMap.put("_extrinsicValue", _extrinsicValue);
                    detailMap.put("_threadName", owThreadName);

                    HashMap<String,String> segmentMap = new HashMap<>();
                    List<Distribution> distributionTypeList = itemOutType.getDistribution();
                    for (Iterator<Distribution> id = distributionTypeList.iterator(); id.hasNext();){
                        Distribution distributionType = id.next();

                        String _charge = distributionType.getCharge().getMoney().getValue();
                        detailMap.put("_charge", _charge);
                        String _accountingName = distributionType.getAccounting().getName();
                        detailMap.put("_accountingName", _accountingName);


                        /* ***************************************************
                                        INSERT PO DETAIL
                        * ******************************************************/
                        //UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "Insert PO Detail Map", "");
                        InsertPurchaseOrder ipod = new InsertPurchaseOrder(detailMap, newConn);
                        Integer _orderDetailPK = ipod.insertOrderDetail();
                        if (_orderDetailPK.equals(-1)){
                            throw new PurchaseOrderTransactionException("Purchase Order Detail Fail");
                        }

                        UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "Build Segment List", "");
                        List<Segment> segmentTypeList = distributionType.getAccounting().getSegment();
                        for (Iterator<Segment> is = segmentTypeList.iterator(); is.hasNext();){
                            Segment segmentType = is.next();
                            String _segmentType = segmentType.getType();
                            String _segmentId = segmentType.getId();
                            String _segmentDescription = segmentType.getDescription();

                            segmentMap.put("_orderDetailPK", _orderDetailPK.toString());
                            segmentMap.put("_segmentType", _segmentType);
                            segmentMap.put("_segmentId", _segmentId);
                            segmentMap.put("_segmentDescription", _segmentDescription);
                            segmentMap.put("_accountingName", _accountingName);
                            segmentMap.put("_threadName", owThreadName);


                            /* *************** INSERT PO SEGMENT  ********************** */
                            // UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "Insert PO Segment", "");
                            InsertPurchaseOrder ipos = new InsertPurchaseOrder(segmentMap, newConn);
                            Integer _orderSegmentPK = ipos.insertOrderSegment();
                            if (_orderSegmentPK.equals(-1)){
                                throw new PurchaseOrderTransactionException("Purchase Order Segment Fail");
                            }
                        }

                        /* ***************************************************
                                            INSERT INVOICE
                        * ****************************************************/
                        UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "Insert Invoice Placeholder", "");
                        Integer _orderInvoicePK = ipod.insertInvoice();
                        if (_orderInvoicePK.equals(-1)){
                            throw new PurchaseOrderTransactionException("Invoice Fail");
                        }

                    } // distribution loop


                    /* *************** COMMIT TRANSACTION  ********************** */
                    newConn.commit();

                } // end of ItemOut Loop
            } // end of OrderRequest Loop

            /* ************************* SEND EMAIL PROCESS *********************** */
            GeneratePOEmailAPI poEmail = new GeneratePOEmailAPI(owThreadName, _orderHeaderPK);
            Boolean emailStatus = poEmail.BuildPOEmail();
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "", "Email PO", emailStatus.toString());


        } catch (PurchaseOrderTransactionException pote){
            procStatus = false;
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "PurchaseOrderTransactionException", pote.getMessage(), "ROLLBACK");
        } catch (JAXBException jaxbe) {
            procStatus = false;
            StringWriter errors = new StringWriter();
            jaxbe.printStackTrace(new PrintWriter(errors));
            String strErrors = errors.toString().substring(0,1023);
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "JAXBException", jaxbe.getMessage(), strErrors);
        } catch (Exception exce){
            procStatus = false;
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderAPI", "ParseXMLPurchaseOrder", "Exception", exce.getMessage(), "");
        } finally{
            if (!procStatus){
                try{
                    newConn.rollback();
                } catch (SQLException e){ }
            }
        }

        return procStatus;
    }

}