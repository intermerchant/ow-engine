package com.orderwire.api;

import com.orderwire.data.DataGenerateCreditMemo;
import com.orderwire.documents.CreditMemoDocument;
import com.orderwire.documents.DocumentData;
import com.orderwire.documents.TransmitDocument;
import com.orderwire.excp.CreditMemoNotDataFoundException;
import com.orderwire.excp.UpdateCreditMemoStatusException;
import com.orderwire.logging.UtilLogger;

import java.net.http.HttpResponse;
import java.util.HashMap;

public class GenerateCreditMemoAPI {
    private final String owThreadName;
    private final Integer owCreditMemoId;

    public GenerateCreditMemoAPI(String threadName, Integer creditMemoId){
        owThreadName = threadName;
        owCreditMemoId = creditMemoId;
    }

    public Boolean BuildCreditMemo() {
        Boolean procStatus = true;

        try {
            DataGenerateCreditMemo dgcm = new DataGenerateCreditMemo(owThreadName);
            UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoAPI", "BuildCreditMemo", "", "Processing Credit Memo Number", owCreditMemoId.toString());

            // update invoice status to 1 - in process
            // moved this update code into datacommon.getCreditMemoGenerate();  using a SELECT FOR UPDATE  to get invoices_id and also set status of record.
            // need to ensure that invoice is only processed by only one task and not have multiple tasks processing the same invoice which creates duplicate invoice records for remain_qty

            /*
            Boolean updateStatus = dgcm.updateCreditMemoStatus(owCreditMemoId, 1);
            if (!updateStatus){
                UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoAPI", "BuildCreditMemo", "", "Update Credit Memo Status Exception", updateStatus.toString());
                throw new UpdateCreditMemoStatusException();
            } else {
                 UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoAPI", "BuildCreditMemo", "", "Processing Credit Memo Number", owCreditMemoId.toString());
            }
            */

            /* *******************  BUILD HEADER HASHMAP ******************** */
            HashMap headerMap = dgcm.getHeaderElements(owCreditMemoId);
            UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoAPI", "BuildCreditMemo", "", "Build Header HashMap", owCreditMemoId.toString());

            /* *******************  BUILD REQUEST HASHMAP ******************** */
            Integer lineNumber = Integer.valueOf(headerMap.get("po_line_no").toString());
            HashMap requestMap = dgcm.getRequestElements(owCreditMemoId, lineNumber);
            UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoAPI", "BuildCreditMemo", "", "Build Request HashMap", owCreditMemoId.toString());

            /* *******************  BUILD CREDIT MEMO XML DOCUMENT ******************** */
            CreditMemoDocument cmd = new CreditMemoDocument(headerMap, requestMap);
            String resultString = cmd.BuildCreditMemoDocument();
            UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoAPI", "BuildCreditMemo", "", "Build Credit Memo XML Documemt", owCreditMemoId.toString());

            /* *******************  INSERT CREDIT MEMO XML *************************** */
            Integer creditMemoXMLId = dgcm.insertCreditMemoXML(owCreditMemoId, resultString);
            UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoAPI", "BuildCreditMemo", "", "Insert Credit Memo XML Documemt", owCreditMemoId.toString());


            /* ******************* TRANSMIT POST DOCUMENT ***************** */
            HashMap<String,String> transmitMap = new HashMap<>();
            transmitMap.put("owThreadName", owThreadName);
            transmitMap.put("resultString", resultString);
            TransmitDocument transd = new TransmitDocument(transmitMap);
            HttpResponse<String> response = transd.TransmitDocument();


            /* ******************* UPDATE INVOICE XMLs RESPONSE DOCUMENT ***************** */
            DocumentData ddata = new DocumentData(owThreadName);
            Boolean updateResponseMessage = ddata.updateResponseMessage(creditMemoXMLId, response);

            if (updateResponseMessage){
                String _gdateTimestamp = String.valueOf(headerMap.get("gdateTimestamp"));
                Boolean updateCreditMemoStatus = ddata.updateCreditMemoStatus(owCreditMemoId, 3, _gdateTimestamp);
            } else {
                Boolean updateCreditMemoStatus = ddata.updateCreditMemoStatus(owCreditMemoId, 2);
            }

        } catch (Exception exce){
            procStatus = false;
            UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoAPI", "BuildCreditMemo", "Exception", "Exception Error", exce.getMessage());
        } finally {}

        return procStatus;
    }

}
