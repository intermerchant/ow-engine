package com.orderwire.tests;

import com.orderwire.api.IngestPurchaseOrderUSPAPI;

public class TestIngestPurchaseOrderUSP {

    public static void main(String[] args) {

        System.out.println("start");
        for (int z=0; z < 1; z++) {
            Boolean procStatus = testIngestPurchaseOrderUSP();
            System.out.println(z);
        }
        System.out.println("complete");



    }

    public static Boolean testIngestPurchaseOrderUSP() {
        Boolean procStatus = false;
        long epochTimestamp = System.currentTimeMillis() / 1000L;
        String threadName = "USP-" + epochTimestamp;
        System.out.println(threadName);

        IngestPurchaseOrderUSPAPI ipoApi = new IngestPurchaseOrderUSPAPI(threadName);
        procStatus = ipoApi.IngestPurchaseOrdersLogs();
        return procStatus;
    }


}
