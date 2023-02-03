package com.orderwire.html;

public class PurchaseOrderFooterTemplate {

    public String FooterTemplate() {

        String _footerTemplate = "<tr>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td class=\"label-totals\">SUBTOTAL</td>\n" +
                "    <td class=\"totals\">[PO-SUBTOTAL]</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td class=\"label-totals\">TAX</td>\n" +
                "    <td class=\"totals\">[PO-TAX]</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td class=\"label-totals\">SHIPPING</td>\n" +
                "    <td class=\"totals\">[PO-SHIPPING]</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td class=\"label-totals\">OTHER</td>\n" +
                "    <td class=\"totals\">[PO-OTHER]</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td></td>\n" +
                "    <td class=\"label-totals\">TOTAL</td>\n" +
                "    <td class=\"totals\">[PO-TOTAL]</td>\n" +
                "</tr>";

        return _footerTemplate;
    }

}
