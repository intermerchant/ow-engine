package com.orderwire.documents;

import com.orderwire.html.StatusTwoLogHeaderTemplate;
import com.orderwire.logging.UtilLogger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatusTwoLogEmailDocument {
    private final HashMap<String,String> poCombinedMap;
    private final List<HashMap> poDetailMapList;
    private String owThreadName = "";

    public StatusTwoLogEmailDocument(HashMap<String,String> combinedMap, List<HashMap> detailMapList){
        poCombinedMap = combinedMap;
        poDetailMapList = detailMapList;
        owThreadName = poCombinedMap.get("owThreadName");
    }


    public String loadEmailHeaderTemplate(){
        String htmlHeaderEmail = "";
        try {
            StatusTwoLogHeaderTemplate headerTemplate = new StatusTwoLogHeaderTemplate();
            htmlHeaderEmail = headerTemplate.HeaderTemplate();

            HashMap<String, String> replaceMap = loadEmailHeaderReplacements();
            Set<Map.Entry<String, String>> entries = replaceMap.entrySet();
            for (HashMap.Entry<String, String> entry : entries) {
                try {
                    htmlHeaderEmail = htmlHeaderEmail.replace(entry.getKey().trim(), entry.getValue().trim());
                } catch (Exception exce){
                    UtilLogger.setDbStatus(owThreadName, "StatusTwoLogEmailDocument", "loadEmailHeaderTemplate", "Exception", "Inner Exception", exce.getMessage());
                }
            }

        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "StatusTwoLogEmailDocument", "loadEmailHeaderTemplate", "", "Exception", exce.getMessage());
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
            UtilLogger.setDbStatus(owThreadName, "StatusTwoLogEmailDocument", "loadEmailHeaderReplacements", "", "Exception", exce.getMessage());
        }

        return replaceMap;

    }

}
