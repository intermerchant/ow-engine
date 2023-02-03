package com.orderwire.html;

public class PurchaseOrderDetailTemplate {

    public PurchaseOrderDetailTemplate(){}

    public String DetailTemplate (){
        String _detailTemplate = "<tr>\n" +
                "    <td class=\"li-detail-center\">[LINE]</td>\n" +
                "    <td class=\"li-detail-center\">[ITEM#]</td>\n" +
                "    <td class=\"li-detail-left\">[DESCRIPTION]</td>\n" +
                "    <td class=\"li-detail-center\">[QTY]</td>\n" +
                "    <td class=\"li-detail-center\">[UOM]</td>\n" +
                "    <td class=\"li-detail-unit-price\">[UNIT-PRICE]</td>\n" +
                "    <td class=\"li-detail-right-up-tot\">[TOTAL]</td>\n" +
                "</tr>";

        return _detailTemplate;
    }
}
