package com.orderwire.documents;

import com.orderwire.html.PurchaseOrderFooterTemplate;
import com.orderwire.html.PurchaseOrderHeaderTemplate;
import com.orderwire.logging.UtilLogger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class POEmailDocument {
    private final HashMap<String,String> poCombinedMap;
    private final List<HashMap> poDetailMapList;
    private String owThreadName = "";

    public POEmailDocument(HashMap<String,String> combinedMap, List<HashMap> detailMapList){
        poCombinedMap = combinedMap;
        poDetailMapList = detailMapList;
        owThreadName = poCombinedMap.get("owThreadName");
    }


    public String loadEmailHeaderTemplate(){
        String htmlHeaderEmail = "";
        try {
            PurchaseOrderHeaderTemplate headerTemplate = new PurchaseOrderHeaderTemplate();
            htmlHeaderEmail = headerTemplate.HeaderTemplate();

            HashMap<String, String> replaceMap = loadEmailHeaderReplacements();
            Set<Map.Entry<String, String>> entries = replaceMap.entrySet();
            for (HashMap.Entry<String, String> entry : entries) {
                try {
                    htmlHeaderEmail = htmlHeaderEmail.replace(entry.getKey().trim(), entry.getValue().trim());
                } catch (Exception exce){
                    UtilLogger.setDbStatus(owThreadName, "POEmailDocument", "loadEmailHeaderTemplate", "Exception", "Inner Exception", exce.getMessage());
                }
            }

        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "POEmailDocument", "loadEmailHeaderTemplate", "", "Exception", exce.getMessage());
        }

        return htmlHeaderEmail;
    }

    private HashMap loadEmailHeaderReplacements(){
        HashMap<String,String> replaceMap = new HashMap<>();

        try {
            replaceMap.put("[USPS-PO-HEADER]", poCombinedMap.get("po_header_text"));
            replaceMap.put("[SUPPLIER-LOGO]", poCombinedMap.get("po_image"));
            replaceMap.put("[INFOMELD-LOGO]",poCombinedMap.get("infomeldLogo"));
            replaceMap.put("[SUPPLIER-NAME]", poCombinedMap.get("supplier_name"));
            replaceMap.put("[SUPPLIER-ADDRESS-1]", poCombinedMap.get("supplier_addr1"));
            replaceMap.put("[SUPPLIER-ADDRESS-2]", poCombinedMap.get("supplier_addr2"));
            replaceMap.put("[SUPPLIER-CITY-STATE-POSTAL]", poCombinedMap.get("supplier_csp"));
            replaceMap.put("[SUPPLIER-TELEPHONE]", poCombinedMap.get("supplier_phone"));
            replaceMap.put("[SUPPLIER-FAX]", poCombinedMap.get("supplier_fax"));
            replaceMap.put("[PO-DATE]", poCombinedMap.get("order_date"));
            replaceMap.put("[PO-PO-NO]", poCombinedMap.get("order_id"));
            replaceMap.put("[SHIP-TO-ADDRESS-ID]", poCombinedMap.get("shipto_address_id"));
            replaceMap.put("[SHIP-TO]", poCombinedMap.get("shipto_deliverto"));
            replaceMap.put("[SHIP-TO-NAME]", poCombinedMap.get("shipto_name"));
            replaceMap.put("[SHIP-TO-ADDRESS-1]", poCombinedMap.get("shipto_address_street1"));
            replaceMap.put("[SHIP-TO-ADDRESS-2]", poCombinedMap.get("shipto_address_street2"));
            replaceMap.put("[SHIP-TO-CITY-STATE-POSTAL]", poCombinedMap.get("shipto_csp"));
            replaceMap.put("[CONTACT-NAME]", poCombinedMap.get("order_contact_name"));
            replaceMap.put("[CONTACT-EMAIL]", poCombinedMap.get("order_contact_email"));
            replaceMap.put("[CONTACT-AREA-TELEPHONE]", poCombinedMap.get("order_contact_area_phone_ext"));
            replaceMap.put("[ORDER-ID]", poCombinedMap.get("order_id"));
            replaceMap.put("[PO-SUBTOTAL]", poCombinedMap.get("po_subtotal"));
            replaceMap.put("[PO-TAX]", poCombinedMap.get("po_tax"));
            replaceMap.put("[PO-SHIPPING]", poCombinedMap.get("po_shipping"));
            replaceMap.put("[PO-OTHER]", poCombinedMap.get("po_other"));
            replaceMap.put("[PO-TOTAL]", poCombinedMap.get("po_total"));

        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "POEmailDocument", "loadEmailHeaderReplacements", "", "Exception", exce.getMessage());
        }

        return replaceMap;

    }


    public String loadEmailDetailTemplate(){
        String htmlDetailEmail = "";
        String orderDetail = "";

        try {
            // Path filePath = Paths.get("./src/main/html", "PurchaseOrderDetailTemplate.html");
            // htmlDetailEmail = Files.readString(filePath);

            PurchaseOrderDetailTemplate detailTemplate = new PurchaseOrderDetailTemplate();
            htmlDetailEmail = detailTemplate.DetailTemplate();

            orderDetail = htmlDetailEmail;
            Integer iterCounter = 0;
            HashMap<String,String> replaceMap;
            for (HashMap detailMap : poDetailMapList){
                replaceMap = loadEmailDetailReplacements(detailMap);
                Set<Map.Entry<String, String>> entries = replaceMap.entrySet();
                for (HashMap.Entry<String, String> entry : entries) {
                    orderDetail = orderDetail.replace(entry.getKey().trim(), entry.getValue().trim());
                }
                iterCounter ++;
                if(iterCounter < poDetailMapList.size()) {
                    orderDetail = orderDetail + htmlDetailEmail;
                }
            }

        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "POEmailDocument", "loadEmailDetailTemplate", "", "Exception", exce.getMessage());
        }

        return orderDetail;
    }

    public HashMap loadEmailDetailReplacements(HashMap<String, String> detailMap){
        HashMap<String,String> replaceMap = new HashMap<>();

        try {
            replaceMap.put("[LINE]", detailMap.get("po_line_no"));
            replaceMap.put("[ITEM#]", detailMap.get("po_item_no"));
            replaceMap.put("[DESCRIPTION]", detailMap.get("item_desc"));
            replaceMap.put("[QTY]", detailMap.get("item_qty"));
            replaceMap.put("[UOM]", detailMap.get("item_uom"));
            replaceMap.put("[UNIT-PRICE]", detailMap.get("unit_price"));
            replaceMap.put("[TOTAL]", detailMap.get("total_unit_price"));

        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "POEmailDocument", "loadEmailDetailReplacements", "", "Exception", exce.getMessage());
        }

        return replaceMap;
    }

    public String loadEmailFooterTemplate(){
        String htmlFooterEmail = "";

        try {
            // Path filePath = Paths.get("./src/main/html", "PurchaseOrderFooterTemplate.html");
            // htmlFooterEmail = Files.readString(filePath);

            PurchaseOrderFooterTemplate footerTemplate = new PurchaseOrderFooterTemplate();
            htmlFooterEmail = footerTemplate.FooterTemplate();

            HashMap<String, String> replaceMap = loadEmailFooterReplacements();
            Set<Map.Entry<String, String>> entries = replaceMap.entrySet();
            for (HashMap.Entry<String, String> entry : entries) {
                try {
                    htmlFooterEmail = htmlFooterEmail.replace(entry.getKey().trim(), entry.getValue().trim());
                } catch (Exception exce){
                    UtilLogger.setDbStatus(owThreadName, "POEmailDocument", "loadEmailFooterTemplate", "Exception", "Inner Exception", exce.getMessage());
                }
            }

        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "POEmailDocument", "loadEmailFooterTemplate", "", "Exception", exce.getMessage());
        }

        return htmlFooterEmail;
    }


    public HashMap loadEmailFooterReplacements(){
        HashMap<String,String> replaceMap = new HashMap<>();

        try {
            replaceMap.put("[PO-SUBTOTAL]", poCombinedMap.get("po_subtotal"));
            replaceMap.put("[PO-TAX]", poCombinedMap.get("po_tax"));
            replaceMap.put("[PO-SHIPPING]", poCombinedMap.get("po_shipping"));
            replaceMap.put("[PO-OTHER]", poCombinedMap.get("po_other"));
            replaceMap.put("[PO-TOTAL]", poCombinedMap.get("po_total"));

        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "POEmailDocument", "loadEmailFooterReplacements", "", "Exception", exce.getMessage());
        }

        return replaceMap;

    }
}