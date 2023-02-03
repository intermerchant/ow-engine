package com.orderwire.documents;

import com.orderwire.data.DataGenerateInvoice;
import com.orderwire.logging.UtilLogger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.HashMap;

public class InvoiceDocument {
    private final HashMap<String,String> invHeaderMap;
    private final HashMap<String,String> invDetailMap;

    public InvoiceDocument(HashMap<String,String> headerMap, HashMap<String,String> requestMap){
        invHeaderMap = headerMap;
        invDetailMap = requestMap;
    }

    public String BuildInvoiceDocument(){
        String resultString = "";
        String owThreadName = invHeaderMap.get("owThreadName");

        try {
            DocumentBuilder builder = null;
            DataGenerateInvoice dgi = new DataGenerateInvoice(owThreadName);

            //String _timeStamp = dgi.invoiceTimestamp();
            String _orderId = String.valueOf(invHeaderMap.get("order_id"));
            String _payloadId = String.valueOf(invHeaderMap.get("payload_id"));
            String _invoiceNo = String.valueOf(invHeaderMap.get("invoice_no"));
            String _orderDate = String.valueOf(invHeaderMap.get("order_date"));
            String _supplierFrom = String.valueOf(invHeaderMap.get("supplier_from"));
            String _supplierFromDomain = String.valueOf(invHeaderMap.get("supplier_from_domain"));
            String _supplierTo = String.valueOf(invHeaderMap.get("supplier_to"));
            String _supplierToDomain = String.valueOf(invHeaderMap.get("supplier_to_domain"));
            String _supplierSender = String.valueOf(invHeaderMap.get("supplier_sender"));
            String _senderSharedSecret = String.valueOf(invHeaderMap.get("sharedsecret_sender"));
            String _supplier_inv_addr_id = String.valueOf(invHeaderMap.get("supplier_inv_addr_id"));
            String _supplier_inv_name = String.valueOf(invHeaderMap.get("supplier_inv_name"));
            String _invoiceLineNumber = String.valueOf(invHeaderMap.get("invoice_line_no"));
            String _invoiceAmount = String.valueOf(invHeaderMap.get("invoice_amount"));
            String _poLineNumber = String.valueOf(invHeaderMap.get("po_line_no"));
            String _invoiceQuantity = String.valueOf(invHeaderMap.get("invoice_qty"));
            String _generateInvoice = String.valueOf(invHeaderMap.get("generate_invoice"));
            String _generateCredit = String.valueOf(invHeaderMap.get("generate_credit"));
            String _userAgent = String.valueOf(invHeaderMap.get("useragent"));
            String _invoiceTimestamp = String.valueOf(invHeaderMap.get("invoiceTimestamp"));
            String gdateTimestamp = String.valueOf(invHeaderMap.get("gdateTimestamp"));

            String _purpose = "";
            if (_generateInvoice.equals("Y")){
                _purpose = "standard";
            } else if (_generateCredit.equals("Y")){
                _purpose = "creditMemo";
            }

            Integer _invoiceId = Integer.valueOf(invDetailMap.get("invoices_id"));
            String _unitOfMeasure = String.valueOf(invDetailMap.get("item_uom"));
            String _unitPrice = String.valueOf(invDetailMap.get("unit_price"));
            String _supplierNo = String.valueOf(invDetailMap.get("supplier_no"));
            String _supplierPN = String.valueOf(invDetailMap.get("supplier_pn"));
            String _itemDesc = String.valueOf(invDetailMap.get("item_desc"));

            /// I think we want to replace the SubtotalAmount calculation of UnitPrice*InvoiceQty with InvoiceAmount out of the Invoices table inv.invoice_amount. All else remains the same
                //Double _subtotalAmount = (Double.valueOf(_invoiceQuantity) * Double.valueOf(_unitPrice));
            Double _subtotalAmount = (Double.valueOf(_invoiceAmount));

            Double _taxAmount = 0.00;
            Double _specialHandlingAmount = 0.00;
            String _shippingAmount = String.valueOf(invDetailMap.get("line_ship_charge"));
            Double _grossAmount = ( _subtotalAmount + _taxAmount + _specialHandlingAmount + Double.valueOf(_shippingAmount) );
            Double _netAmount = ( _subtotalAmount + _taxAmount + _specialHandlingAmount + Double.valueOf(_shippingAmount) );
            Double _dueAmount = ( _subtotalAmount + _taxAmount + _specialHandlingAmount + Double.valueOf(_shippingAmount) );

            // get Catalog Item HashMap
            HashMap catalogMap = dgi.getCatalogElements(_supplierNo, _supplierPN);
            String _manufPn = String.valueOf(catalogMap.get("manuf_pn"));
            String _manufName = String.valueOf(catalogMap.get("manuf_name"));


            // gather invoice items into hashmap
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();
            document.setXmlStandalone(true);

            // <editor-fold defaultstate="collapsed" desc="cXML Element">
            Element cxmlElement = document.createElement("cXML");
            document.appendChild(cxmlElement);
            cxmlElement.setAttribute("timestamp", _invoiceTimestamp);
            cxmlElement.setAttribute("payloadID", _payloadId);
            cxmlElement.setAttribute("version", "1.0");
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Header Element">
            Element headerElement = document.createElement("Header");
            cxmlElement.appendChild(headerElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="From Element">
            Element FromElement = document.createElement("From");
            headerElement.appendChild(FromElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="From Credential Element">
            Element FromCredentialElement = document.createElement("Credential");
            FromCredentialElement.setAttribute("domain", _supplierFromDomain);
            FromElement.appendChild(FromCredentialElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="From Credential Identity Element">
            Element FromIdentityElement = document.createElement("Identity");
            FromIdentityElement.appendChild(document.createTextNode(_supplierFrom));
            FromCredentialElement.appendChild(FromIdentityElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="To Element">
            Element ToElement = document.createElement("To");
            headerElement.appendChild(ToElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="To Credential Element">
            Element ToCredentialElement = document.createElement("Credential");
            ToCredentialElement.setAttribute("domain", _supplierToDomain);
            ToElement.appendChild(ToCredentialElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="To Credential Identity Element">
            Element ToIdentityElement = document.createElement("Identity");
            ToIdentityElement.appendChild(document.createTextNode(_supplierTo));
            ToCredentialElement.appendChild(ToIdentityElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Sender Element">
            Element SenderElement = document.createElement("Sender");
            headerElement.appendChild(SenderElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Sender Credential Element">
            Element SenderCredentialElement = document.createElement("Credential");
            SenderCredentialElement.setAttribute("domain", _supplierFromDomain);
            SenderElement.appendChild(SenderCredentialElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Sender Credential Identity Element">
            Element SenderIdentityElement = document.createElement("Identity");
            SenderIdentityElement.appendChild(document.createTextNode(_supplierSender));
            SenderCredentialElement.appendChild(SenderIdentityElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Sender Shared Secred Element">
            Element SenderSharedSecretElement = document.createElement("SharedSecret");
            SenderSharedSecretElement.appendChild(document.createTextNode(_senderSharedSecret));
            SenderCredentialElement.appendChild(SenderSharedSecretElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Sender User Agent Element">
            Element SenderUserAgentElement = document.createElement("UserAgent");
            SenderUserAgentElement.appendChild(document.createTextNode(_userAgent));
            SenderElement.appendChild(SenderUserAgentElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Request Element">
            Element requestElement = document.createElement("Request");
            requestElement.setAttribute("deploymentMode", "production");
            cxmlElement.appendChild(requestElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Request Element">
            Element InvoiceDetailRequestElement = document.createElement("InvoiceDetailRequest");
            requestElement.appendChild(InvoiceDetailRequestElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Request Header Element">
            Element InvoiceDetailRequestHeaderElement = document.createElement("InvoiceDetailRequestHeader");
            InvoiceDetailRequestHeaderElement.setAttribute("invoiceDate", _invoiceTimestamp);
            InvoiceDetailRequestHeaderElement.setAttribute("invoiceID", _invoiceNo);
            InvoiceDetailRequestHeaderElement.setAttribute("operation", "new");
            InvoiceDetailRequestHeaderElement.setAttribute("purpose", _purpose);
            InvoiceDetailRequestElement.appendChild(InvoiceDetailRequestHeaderElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Header Indicator Element">
            Element InvoiceDetailHeaderIndicatorElement = document.createElement("InvoiceDetailHeaderIndicator");
            InvoiceDetailRequestHeaderElement.appendChild(InvoiceDetailHeaderIndicatorElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Line Indicator Element">
            Element InvoiceDetailLineIndicatorElement = document.createElement("InvoiceDetailLineIndicator");
            InvoiceDetailLineIndicatorElement.setAttribute("isAccountingInLine", "yes");
            //InvoiceDetailLineIndicatorElement.setAttribute("isShippingInLine", "yes");
            //InvoiceDetailLineIndicatorElement.setAttribute("isTaxInLine", "yes");
            InvoiceDetailRequestHeaderElement.appendChild(InvoiceDetailLineIndicatorElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Partner Element">
            Element InvoicePartnerElement = document.createElement("InvoicePartner");
            InvoiceDetailRequestHeaderElement.appendChild(InvoicePartnerElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Partner Contact Element">
            Element InvoicePartnerContactElement = document.createElement("Contact");
            InvoicePartnerContactElement.setAttribute("addressID", _supplier_inv_addr_id);
            InvoicePartnerContactElement.setAttribute("role", "remitTo");
            InvoicePartnerElement.appendChild(InvoicePartnerContactElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Partner Contact Name Element">
            Element InvoicePartnerContactNameElement = document.createElement("Name");
            InvoicePartnerContactNameElement.setAttribute("xml:lang", "en");
            InvoicePartnerContactNameElement.appendChild(document.createTextNode(_supplier_inv_name));
            InvoicePartnerContactElement.appendChild(InvoicePartnerContactNameElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Payment Term Element">
            Element InvoiceDetailPaymentTermElement  = document.createElement("InvoiceDetailPaymentTerm");
            InvoiceDetailPaymentTermElement.setAttribute("percentageRate", "0");
            InvoiceDetailPaymentTermElement.setAttribute("payInNumberOfDays", "30");
            InvoiceDetailRequestHeaderElement.appendChild(InvoiceDetailPaymentTermElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Order Element">
            Element InvoiceDetailOrderElement = document.createElement("InvoiceDetailOrder");
            InvoiceDetailRequestElement.appendChild(InvoiceDetailOrderElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Order Info Element">
            Element InvoiceDetailOrderInfoElement = document.createElement("InvoiceDetailOrderInfo");
            InvoiceDetailOrderElement.appendChild(InvoiceDetailOrderInfoElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Order Reference Element">
            Element OrderReferenceElement = document.createElement("OrderReference");
            OrderReferenceElement.setAttribute("orderDate", _orderDate);
            OrderReferenceElement.setAttribute("orderID", _orderId);
            InvoiceDetailOrderInfoElement.appendChild(OrderReferenceElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Document Reference Element">
            Element DocumentReferenceElement = document.createElement("DocumentReference");
            DocumentReferenceElement.setAttribute("payloadID", _payloadId);
            OrderReferenceElement.appendChild(DocumentReferenceElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Item Element">
            Element InvoiceDetailItemElement = document.createElement("InvoiceDetailItem");
            InvoiceDetailItemElement.setAttribute("invoiceLineNumber", _invoiceLineNumber);  /* invoices.invoice_line_no */
            InvoiceDetailItemElement.setAttribute("quantity", _invoiceQuantity);
            InvoiceDetailOrderElement.appendChild(InvoiceDetailItemElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Unit Of Measure Element">
            Element UnitOfMeasureElement = document.createElement("UnitOfMeasure");
            UnitOfMeasureElement.appendChild(document.createTextNode(_unitOfMeasure));
            InvoiceDetailItemElement.appendChild(UnitOfMeasureElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Unit of Price Element">
            Element UnitPriceElement = document.createElement("UnitPrice");
            InvoiceDetailItemElement.appendChild(UnitPriceElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Money Element">
            Element MoneyElement = document.createElement("Money");
            MoneyElement.setAttribute("currency", "USD");
            MoneyElement.appendChild(document.createTextNode(_unitPrice));
            UnitPriceElement.appendChild(MoneyElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Item Reference Element">
            Element InvoiceDetailItemReferenceElement = document.createElement("InvoiceDetailItemReference");
            InvoiceDetailItemReferenceElement.setAttribute("lineNumber", _poLineNumber);   /* invoices.po_line_no */
            InvoiceDetailItemElement.appendChild(InvoiceDetailItemReferenceElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Item ID Element">
            Element ItemIDElement = document.createElement("ItemID");
            InvoiceDetailItemReferenceElement.appendChild(ItemIDElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Supplier Part ID Element">
            Element SupplierPartIDElement = document.createElement("SupplierPartID");
            SupplierPartIDElement.appendChild(document.createTextNode(_supplierPN));
            ItemIDElement.appendChild(SupplierPartIDElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Description Element">
            Element DescriptionElement = document.createElement("Description");
            DescriptionElement.setAttribute("xml:lang", "en");
            DescriptionElement.appendChild(document.createTextNode(_itemDesc));
            InvoiceDetailItemReferenceElement.appendChild(DescriptionElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Manufacturer Part ID Element">
            Element ManufacturerPartIDElement = document.createElement("ManufacturerPartID");
            ManufacturerPartIDElement.appendChild(document.createTextNode(_manufPn));
            InvoiceDetailItemReferenceElement.appendChild(ManufacturerPartIDElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Manufacturer Name Element">
            Element ManufacturerNameElement = document.createElement("ManufacturerName");
            ManufacturerNameElement.appendChild(document.createTextNode(_manufName));
            InvoiceDetailItemReferenceElement.appendChild(ManufacturerNameElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Serial Number Element">
            Element SerialNumberElement = document.createElement("SerialNumber");
            InvoiceDetailItemReferenceElement.appendChild(SerialNumberElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Subtotal Amount Element">
            Element SubtotalAmountElement = document.createElement("SubtotalAmount");
            InvoiceDetailItemElement.appendChild(SubtotalAmountElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Subtotal Amount Money Element">
            Element SubtotalAmountMoneyElement = document.createElement("Money");
            SubtotalAmountMoneyElement.setAttribute("currency", "USD");
            SubtotalAmountMoneyElement.appendChild(document.createTextNode(_subtotalAmount.toString()));
            SubtotalAmountElement.appendChild(SubtotalAmountMoneyElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Element">
            Element InvoiceDetailSummaryElement = document.createElement("InvoiceDetailSummary");
            InvoiceDetailRequestElement.appendChild(InvoiceDetailSummaryElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Subtotal Amount Element">
            Element InvoiceDetailSummarySubtotalAmountElement = document.createElement("SubtotalAmount");
            InvoiceDetailSummaryElement.appendChild(InvoiceDetailSummarySubtotalAmountElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Subtotal Amount Money Element">
            Element InvoiceDetailSummarySubtotalAmountMoneyElement = document.createElement("Money");
            InvoiceDetailSummarySubtotalAmountMoneyElement.setAttribute("currency", "USD");
            InvoiceDetailSummarySubtotalAmountMoneyElement.appendChild(document.createTextNode(_subtotalAmount.toString()));
            InvoiceDetailSummarySubtotalAmountElement.appendChild(InvoiceDetailSummarySubtotalAmountMoneyElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Tax Element">
            Element InvoiceDetailSummaryTaxElement = document.createElement("Tax");
            InvoiceDetailSummaryElement.appendChild(InvoiceDetailSummaryTaxElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Subtotal Amount Money Element">
            Element InvoiceDetailSummaryTaxMoneyElement = document.createElement("Money");
            InvoiceDetailSummaryTaxMoneyElement.setAttribute("currency", "USD");
            InvoiceDetailSummaryTaxMoneyElement.appendChild(document.createTextNode(_taxAmount.toString()));
            InvoiceDetailSummaryTaxElement.appendChild(InvoiceDetailSummaryTaxMoneyElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Tax Description Element">
            Element InvoiceDetailSummaryTaxDescriptionElement = document.createElement("Description");
            InvoiceDetailSummaryTaxDescriptionElement.setAttribute("xml:lang", "en");
            InvoiceDetailSummaryTaxDescriptionElement.appendChild(document.createTextNode("Total Tax"));
            InvoiceDetailSummaryTaxElement.appendChild(InvoiceDetailSummaryTaxDescriptionElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Special Handling Amount Element">
            Element IDSSpecialHandlingAmountElement = document.createElement("SpecialHandlingAmount");
            InvoiceDetailSummaryElement.appendChild(IDSSpecialHandlingAmountElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Special Handling Amount Money Element">
            Element IDSSpecialHandlingAmountMoneyElement = document.createElement("Money");
            IDSSpecialHandlingAmountMoneyElement.setAttribute("currency", "USD");
            IDSSpecialHandlingAmountMoneyElement.appendChild(document.createTextNode(_specialHandlingAmount.toString()));
            IDSSpecialHandlingAmountElement.appendChild(IDSSpecialHandlingAmountMoneyElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Shipping Amount Element">
            Element InvoiceDetailSummaryShippingAmountElement = document.createElement("ShippingAmount");
            InvoiceDetailSummaryElement.appendChild(InvoiceDetailSummaryShippingAmountElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Shipping Amount Money Element">
            Element InvoiceDetailSummaryShippingAmountMoneyElement = document.createElement("Money");
            InvoiceDetailSummaryShippingAmountMoneyElement.setAttribute("currency", "USD");
            InvoiceDetailSummaryShippingAmountMoneyElement.appendChild(document.createTextNode(_shippingAmount));
            InvoiceDetailSummaryShippingAmountElement.appendChild(InvoiceDetailSummaryShippingAmountMoneyElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Gross Amount Element">
            Element InvoiceDetailSummaryGrossAmountElement = document.createElement("GrossAmount");
            InvoiceDetailSummaryElement.appendChild(InvoiceDetailSummaryGrossAmountElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Gross Amount Money Element">
            Element InvoiceDetailSummaryGrossAmountMoneyElement = document.createElement("Money");
            InvoiceDetailSummaryGrossAmountMoneyElement.setAttribute("currency", "USD");
            InvoiceDetailSummaryGrossAmountMoneyElement.appendChild(document.createTextNode(_grossAmount.toString()));
            InvoiceDetailSummaryGrossAmountElement.appendChild(InvoiceDetailSummaryGrossAmountMoneyElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Net Amount Element">
            Element InvoiceDetailSummaryNetAmountElement = document.createElement("NetAmount");
            InvoiceDetailSummaryElement.appendChild(InvoiceDetailSummaryNetAmountElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Net Amount Money Element">
            Element InvoiceDetailSummaryNetAmountMoneyElement = document.createElement("Money");
            InvoiceDetailSummaryNetAmountMoneyElement.setAttribute("currency", "USD");
            InvoiceDetailSummaryNetAmountMoneyElement.appendChild(document.createTextNode(_netAmount.toString()));
            InvoiceDetailSummaryNetAmountElement.appendChild(InvoiceDetailSummaryNetAmountMoneyElement);
            // </editor-fold>


            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Due Amount Element">
            Element InvoiceDetailSummaryDueAmountElement = document.createElement("DueAmount");
            InvoiceDetailSummaryElement.appendChild(InvoiceDetailSummaryDueAmountElement);
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Invoice Detail Summary Due Amount Money Element">
            Element InvoiceDetailSummaryDueAmountMoneyElement = document.createElement("Money");
            InvoiceDetailSummaryDueAmountMoneyElement.setAttribute("currency", "USD");
            InvoiceDetailSummaryDueAmountMoneyElement.appendChild(document.createTextNode(_dueAmount.toString()));
            InvoiceDetailSummaryDueAmountElement.appendChild(InvoiceDetailSummaryDueAmountMoneyElement);
            // </editor-fold>


            /* *******************  TRANSFORM CONFIG ******************** */

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            DOMImplementation domImpl = document.getImplementation();
            DocumentType doctype = domImpl.createDocumentType("doctype", "", "http://xml.cXML.org/schemas/cXML/1.2.020/InvoiceDetail.dtd");
            // transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
            DOMSource source = new DOMSource(document);
            StringWriter writer = new StringWriter();
            //StreamResult result = new StreamResult(System.out);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            resultString = writer.toString();

        } catch (ParserConfigurationException pce) {
            UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceAPI", "BuildInvoice", "", "ParserConfigurationException", pce.getMessage());
        } catch (TransformerConfigurationException tce) {
            UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceAPI", "BuildInvoice", "", "TransformerConfigurationException", tce.getMessage());
        } catch (TransformerException te) {
            UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceAPI", "BuildInvoice", "", "TransformerException", te.getMessage());
        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceAPI", "BuildInvoice", "", "Exception", exce.getMessage());
        } finally{

        }

        return resultString;
    }

}