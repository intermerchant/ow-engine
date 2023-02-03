package com.orderwire.api;

import com.orderwire.data.DataPOEmail;
import com.orderwire.documents.POEmailDocument;
import com.orderwire.logging.UtilLogger;
import com.orderwire.smtp.HtmlEmailSender;

import java.util.*;

public class GeneratePOEmailAPI {
    private final String owThreadName;
    private final Integer orderHeaderId;

    public GeneratePOEmailAPI(String _threadName, Integer _orderHeaderId){
        owThreadName = _threadName;
        orderHeaderId = _orderHeaderId;
    }

    public Boolean BuildPOEmail(){
        Boolean procStatus = true;

        try {
            DataPOEmail dataEmail = new DataPOEmail(owThreadName, orderHeaderId);

            // get Order Header Map
            HashMap<String, String> headerMap = new HashMap<>();
            headerMap = dataEmail.getOrderHeaderMap();

            // get Order Detail Map List
            List<HashMap> detailMapList = new ArrayList<>();
            detailMapList = dataEmail.getOrderDetailMap();

            // get Suppliers Map
            HashMap<String, String> supplierMap = new HashMap<>();
            supplierMap = dataEmail.getOrderSupplierMap();


            HashMap<String, String> emailDetailsMap = new HashMap<>();
            // get Supplier Image
            Integer supplierId = Integer.valueOf(supplierMap.get("supplier_id"));
            emailDetailsMap = dataEmail.getEmailDetails(supplierId);
            String emailSubjectCombined = emailDetailsMap.get("email_subject") + " " + headerMap.get("order_id");
            emailDetailsMap.put("email_subject", emailSubjectCombined);

            // get Infomeld Image
            String infomeldLogo = dataEmail.getInfomeldLogo();


            // combine HashMaps
            HashMap<String, String> combinedMap = new HashMap<>();
            combinedMap.putAll(headerMap);
            combinedMap.putAll(supplierMap);
            combinedMap.putAll(emailDetailsMap);
            combinedMap.put("infomeldLogo", infomeldLogo);


            // get Email Header Template from File
            POEmailDocument emailDocument = new POEmailDocument(combinedMap, detailMapList);
            String emailHeaderMessage = emailDocument.loadEmailHeaderTemplate();

            // get Email Detail Template from File
            String emailDetailMessage = emailDocument.loadEmailDetailTemplate();
            emailHeaderMessage = emailHeaderMessage.replace("[ORDER-DETAIL-TEMPLATE]", emailDetailMessage);

            // get Email Footer Template from File
            String emailFooterMessage = emailDocument.loadEmailFooterTemplate();
            emailHeaderMessage = emailHeaderMessage.replace("[ORDER-FOOTER-TEMPLATE]", emailFooterMessage);

            // Assemble Email Parts
            String emailMessageComplete = emailHeaderMessage;
            emailDetailsMap.put("email_message", emailMessageComplete);

            // send email message
            HtmlEmailSender mailer = new HtmlEmailSender(emailDetailsMap);
            Boolean emailStatus = mailer.sendHtmlEmail();


            //save email message to database
            Integer poEmailId = dataEmail.insertEmailMessage(orderHeaderId, emailMessageComplete);

        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "GeneratePOEmailAPI", "BuildPOEmail", "Exception", exce.getMessage(), "");
        }
        return procStatus;
    }



}
