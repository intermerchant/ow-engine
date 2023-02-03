package com.orderwire.api;

import com.orderwire.data.DataGenerateInvoice;
import com.orderwire.documents.DocumentData;
import com.orderwire.documents.InvoiceDocument;
import com.orderwire.documents.TransmitDocument;
import com.orderwire.excp.InvoiceNotDataFoundException;
import com.orderwire.excp.UpdateInvoiceStatusException;
import com.orderwire.logging.UtilLogger;
import java.io.StringWriter;
import java.net.http.HttpResponse;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

public class GenerateInvoiceAPI {
    private final String owThreadName;
    private final Integer owInvoiceId;

     public GenerateInvoiceAPI(String threadName, Integer invoiceId){
        owThreadName = threadName;
        owInvoiceId = invoiceId;
    }
     
    public Boolean BuildInvoice(){
        Boolean procStatus = true;

        try {
            DataGenerateInvoice dgi = new DataGenerateInvoice(owThreadName);
            DocumentData ddata = new DocumentData(owThreadName);

            UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceAPI", "BuildInvoice", "", "Processing Invoice Number", owInvoiceId.toString());

            // update invoice status to 1 - in process
            // moved this update code into datacommon.getInvoiceGenerate();  using a SELECT FOR UPDATE  to get invoices_id and also set status of record.
            // need to ensure that invoice is only processed by only one task and not have multiple tasks processing the same invoice which creates duplicate invoice records for remain_qty
            /*
            Boolean updateStatus = ddata.updateInvoiceStatus(owInvoiceId, 1);
            if (!updateStatus){
                UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceAPI", "BuildInvoice", "", "Update Invoice Status Exception", updateStatus.toString());
                    throw new UpdateInvoiceStatusException();
            } else {
                    UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceAPI", "BuildInvoice", "", "Processing Invoice Number", owInvoiceId.toString());
            }
            */


            // get Header HashMap
            HashMap headerMap = dgi.getHeaderElements(owInvoiceId);

            // get Request Hashmap
            Integer lineNumber = Integer.valueOf(headerMap.get("po_line_no").toString());
            HashMap requestMap = dgi.getRequestElements(owInvoiceId, lineNumber);


            /* *******************  BUILD INVOICE XML ******************** */
            InvoiceDocument invd = new InvoiceDocument(headerMap, requestMap);
            String resultString = invd.BuildInvoiceDocument();


             /* *******************  INSERT INVOICE XML ******************** */
            Integer invoiceXMLId = dgi.insertInvoiceXML(owInvoiceId, resultString);
            UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceAPI", "BuildInvoice", "", "Insert Invoice XML Documemt", invoiceXMLId.toString());


            /* ******************* TRANSMIT POST DOCUMENT ***************** */
            HashMap<String,String> transmitMap = new HashMap<>();
            transmitMap.put("owThreadName", owThreadName);
            transmitMap.put("resultString", resultString);
            TransmitDocument transd = new TransmitDocument(transmitMap);
            HttpResponse<String> response = transd.TransmitDocument();



            /* ******************* UPDATE INVOICE XMLs RESPONSE DOCUMENT ***************** */
            Boolean updateResponseMessage = ddata.updateResponseMessage(invoiceXMLId, response);

            if (updateResponseMessage){
                String _gdateTimestamp = String.valueOf(headerMap.get("gdateTimestamp"));
                Boolean updateInvoiceStatus = ddata.updateInvoiceStatus(owInvoiceId, 3, _gdateTimestamp);
                Boolean remainStatus = dgi.RemainInvoice(owInvoiceId);
            } else {
                Boolean updateInvoiceStatus = ddata.updateInvoiceStatus(owInvoiceId, 2);
            }

        } catch (Exception exce){
            procStatus = false;
            UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceAPI", "BuildInvoice", "Exception", "Exception Error", exce.getMessage());
        } finally {}
            
        
        return procStatus;  
    } 

}
