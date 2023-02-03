package com.orderwire.html;

public class StatusTwoLogHeaderTemplate {
    public StatusTwoLogHeaderTemplate(){}

    public String HeaderTemplate(){

        String _headerTemplate = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Orderwire Purchase Order Template</title>\n" +
                "    <style>\n" +
                "        .invoice-box {\n" +
                "            max-width: 1100px;\n" +
                "            margin: auto;\n" +
                "            padding: 30px;\n" +
                "            border: 1px solid #eee;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, .15);\n" +
                "            font-size: 16px;\n" +
                "            line-height: 24px;\n" +
                "            font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;\n" +
                "            color: #555;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box td.infomeld-logo{\n" +
                "            text-align: right;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box table.outside-frames {\n" +
                "            width: 100%;\n" +
                "            line-height: inherit;\n" +
                "            text-align: left;\n" +
                "            margin-top: 20px;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box table {\n" +
                "            width: 100%;\n" +
                "            line-height: inherit;\n" +
                "            text-align: left;\n" +
                "            border-collapse: collapse;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box tr.one {\n" +
                "            vertical-align: top;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box table.abc td:nth-child(2) {\n" +
                "            border: 1px solid grey;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box td.po-header {\n" +
                "            font-size: x-large;\n" +
                "            color: #7b8dc5;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box table.vendor-ship {\n" +
                "            width: 390px;\n" +
                "            line-height: inherit;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box td.header {\n" +
                "            background-color: #3B4E87;\n" +
                "            text-align: left;\n" +
                "            font-weight: bold;\n" +
                "            color: white;\n" +
                "            padding-right: 5px;\n" +
                "            padding-bottom: 5px;\n" +
                "            padding-left: 10px;\n" +
                "            padding-top: 5px;\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "        .invoice-box table.three-top tr.header {\n" +
                "            width: 100%;\n" +
                "            background-color: #3B4E87;\n" +
                "            text-align: center;\n" +
                "            font-weight: bold;\n" +
                "            color: white;\n" +
                "            line-height: inherit;\n" +
                "            padding-top: 20px;\n" +
                "            padding-bottom: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box table.three-top tr.detail {\n" +
                "            width: 100%;\n" +
                "            text-align: center;\n" +
                "            line-height: inherit;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box table.three-top tr.detail td{\n" +
                "            text-align: center;\n" +
                "            line-height: inherit;\n" +
                "            border: 1px solid black;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box table.line-item-frame {\n" +
                "            width: 100%;\n" +
                "            line-height: inherit;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box table.line-item-header {\n" +
                "            width: 100%;\n" +
                "            line-height: inherit;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "        .invoice-box td.li-header {\n" +
                "            background-color: #3B4E87;\n" +
                "            text-align: center;\n" +
                "            font-weight: bold;\n" +
                "            color: white;\n" +
                "            padding-right: 5px;\n" +
                "            padding-bottom: 5px;\n" +
                "            padding-left: 10px;\n" +
                "            padding-top: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box td.li-header-up-desc {\n" +
                "            background-color: #3B4E87;\n" +
                "            text-align: center;\n" +
                "            font-weight: bold;\n" +
                "            color: white;\n" +
                "            width: 450px;\n" +
                "            padding-right: 5px;\n" +
                "            padding-bottom: 5px;\n" +
                "            padding-left: 10px;\n" +
                "            padding-top: 5px;\n" +
                "        }\n" +
                "        \n" +
                "        .invoice-box td.li-header-up-tot {\n" +
                "            background-color: #3B4E87;\n" +
                "            text-align: center;\n" +
                "            font-weight: bold;\n" +
                "            width: 110px;\n" +
                "            color: white;\n" +
                "            padding-right: 5px;\n" +
                "            padding-bottom: 5px;\n" +
                "            padding-left: 10px;\n" +
                "            padding-top: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box table.po-calculates {\n" +
                "            width: 230px;\n" +
                "            line-height: inherit;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box td.li-detail-left {\n" +
                "            border: 1px solid black;\n" +
                "            line-height: inherit;\n" +
                "            text-align: left;\n" +
                "            padding-left: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box td.li-detail-unit-price {\n" +
                "            border: 1px solid black;\n" +
                "            line-height: inherit;\n" +
                "            text-align: right;\n" +
                "            width: 110px;\n" +
                "            padding-right: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box td.li-detail-right {\n" +
                "            border: 1px solid black;\n" +
                "            line-height: inherit;\n" +
                "            text-align: right;\n" +
                "            width: 100px;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box td.li-detail-right-up-tot {\n" +
                "            border: 1px solid black;\n" +
                "            line-height: inherit;\n" +
                "            text-align: right;\n" +
                "            width: 110px;\n" +
                "            padding-right: 5px;\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "        .invoice-box td.li-detail-center {\n" +
                "            border: 1px solid black;\n" +
                "            line-height: inherit;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box td.label-totals {\n" +
                "            line-height: inherit;\n" +
                "            text-align: left;\n" +
                "            padding-left: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .invoice-box td.totals {\n" +
                "            background-color: #f2f2f2;\n" +
                "            border-top: 1px solid black;\n" +
                "            padding-right: 5px;\n" +
                "            line-height: inherit;\n" +
                "            text-align: right;\n" +
                "        }\n" +
                "\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"invoice-box\">\n" +
                "        <table class=\"outside-frames\">\n" +
                "            <tr><td class=\"po-header\">[USPS-PO-HEADER]</td></tr>\n" +
                "        </table>\n" +
                "        <table class=\"outside-frames\">\n" +
                "            <tr class=\"one\">\n" +
                "                <td>\n" +
                "                    <table>\n" +
                "                        <tr><td><img src=\"data:image/jpg;base64, [SUPPLIER-LOGO]\" style=\"width:100%; max-width:300px; height: 110px;\"></td></tr>\n" +
                "                    </table>\n" +
                "                </td>\n" +
                "                <td>\n" +
                "                    <table>\n" +
                "                        <tr><td class=\"infomeld-logo\"><img src=\"data:image/jpg;base64, [INFOMELD-LOGO]\" style=\"width:100%; max-width:300px; height: 110px;\"></td></tr>\n" +
                "                    </table>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "        <table class=\"outside-frames\">\n" +
                "            <tr class=\"one\">\n" +
                "                <td>\n" +
                "                    <table class=\"vendor-ship\">\n" +
                "                        <tr><td class=\"header\">Supplier</td></tr>\n" +
                "                        <tr><td>[SUPPLIER-NAME]</td></tr>\n" +
                "                        <tr><td>[SUPPLIER-ADDRESS-1]</td></tr>\n" +
                "                        <tr><td>[SUPPLIER-ADDRESS-2]</td></tr>\n" +
                "                        <tr><td>[SUPPLIER-CITY-STATE-POSTAL]</td></tr>\n" +
                "                        <tr><td>[SUPPLIER-TELEPHONE]</td></tr>\n" +
                "                        <tr><td>[SUPPLIER-FAX]</td></tr>\n" +
                "                    </table>\n" +
                "                </td>\n" +
                "                <td>\n" +
                "                    <table>\n" +
                "                        <tr><td>\n" +
                "                            <table class=\"abc\">\n" +
                "                                <tr><td style=\"text-align: right; padding-right: 10px;\">DATE</td><td>[PO-DATE]</td></tr>\n" +
                "                                <tr><td style=\"text-align: right; padding-right: 10px;\">PO #</td><td>[PO-PO-NO]</td></tr>\n" +
                "                            </table>\n" +
                "                        </td></tr>\n" +
                "                    </table>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "        <table class=\"outside-frames\">\n" +
                "            <tr class=\"one\">\n" +
                "                <td>\n" +
                "                    <table class=\"vendor-ship\">\n" +
                "                        <tr><td class=\"header\">Ship To</td></tr>\n" +
                "                        <tr><td>[SHIP-TO-ADDRESS-ID]</td></tr>\n" +
                "                        <tr><td>[SHIP-TO]</td></tr>\n" +
                "                        <tr><td>[SHIP-TO-NAME]</td></tr>\n" +
                "                        <tr><td>[SHIP-TO-ADDRESS-1]</td></tr>\n" +
                "                        <tr><td>[SHIP-TO-ADDRESS-2]</td></tr>\n" +
                "                        <tr><td>[SHIP-TO-CITY-STATE-POSTAL]</td></tr>\n" +
                "                    </table>\n" +
                "                </td>\n" +
                "                <td>\n" +
                "                    <table class=\"vendor-ship\">\n" +
                "                        <tr><td class=\"header\">Order Contact</td></tr>\n" +
                "                        <tr><td>[CONTACT-NAME]</td></tr>\n" +
                "                        <tr><td>[CONTACT-EMAIL]</td></tr>\n" +
                "                        <tr><td>[CONTACT-AREA-TELEPHONE]</td></tr>\n" +
                "                    </table>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "        <table class=\"outside-frames\">\n" +
                "            <tr>\n" +
                "                <td>\n" +
                "                    <table class=\"vendor-ship\">\n" +
                "                        <tr><td class=\"header\">Order / Requisition</td></tr>\n" +
                "                        <tr><td>[ORDER-ID]</td></tr>\n" +
                "                    </table>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "        <table class=\"line-item-frame\">\n" +
                "            <tr>\n" +
                "                <td>\n" +
                "                    <table class=\"line-item-header\">\n" +
                "                        <tr>\n" +
                "                            <td class=\"li-header\">LINE</td>\n" +
                "                            <td class=\"li-header\">ITEM #</td>\n" +
                "                            <td class=\"li-header-up-desc\">DESCRIPTION</td>\n" +
                "                            <td class=\"li-header\">QTY</td>\n" +
                "                            <td class=\"li-header\">UOM</td>\n" +
                "                            <td class=\"li-header-up-tot\">UNIT PRICE</td>\n" +
                "                            <td class=\"li-header-up-tot\">TOTAL</td>\n" +
                "                        </tr>\n" +
                "                        [ORDER-DETAIL-TEMPLATE]\n" +
                "                        [ORDER-FOOTER-TEMPLATE]\n" +
                "                    </table>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        return _headerTemplate;
    }
}