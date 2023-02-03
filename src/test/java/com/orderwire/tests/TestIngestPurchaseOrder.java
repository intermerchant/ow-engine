package com.orderwire.tests;

import com.orderwire.api.GeneratePOEmailAPI;
import com.orderwire.api.IngestPurchaseOrderAPI;

public class TestIngestPurchaseOrder {

    public static void main(String[] args) {

        String testString = testIngestPurchaseOrder();
        System.out.println("pause");
    }

    public static String testIngestPurchaseOrder() {
        String htmlEmail = "";
        Integer owTaskSubscribeId = -1;
        Integer owLogId = -1;
        IngestPurchaseOrderAPI ipoApi = new IngestPurchaseOrderAPI("testThreadName", owTaskSubscribeId, owLogId);
        Integer procStatus = ipoApi.IngestPurchaseOrdersLogs();
        System.out.println("pause");


        return htmlEmail;
    }


}
