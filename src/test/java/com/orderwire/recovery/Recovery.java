package com.orderwire.recovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Recovery {

    public static void main(String[] args) {

        for(int ii = 0; ii < 175; ii++) {
            // runTheRecoveryTest();
            runTheRecoveryProd();
            System.out.println(ii);
        }
        System.out.println("Out");
    }


    private static void runTheRecoveryTest(){
        RecoveryData rd = new RecoveryData();

        // Get Order Id Group
        String orderIdGroup = rd.getOrderIdGroup();
        if (orderIdGroup.equals(-1)){
            System.exit(0);
        }

        // Get Hashmap List of Order Id Group
        List<HashMap> masterMapList = new ArrayList<>();
        masterMapList = rd.getMasterMapList(orderIdGroup);

        // Iterate List
        Integer headerId = -1;
        Boolean procHeader = true;
        for (HashMap masterMap : masterMapList){
            if(procHeader) {
                headerId = rd.insertHeader(masterMap);
                procHeader = false;
            }
            masterMap.put("order_header_id", headerId);
            Integer detailId = rd.insertDetail(masterMap);
            Integer inoviceId = rd.insertInvoice(masterMap);
            Boolean updateStatus = rd.updateProcessFlag(orderIdGroup, 1);
        }
    }

    private static void runTheRecoveryProd(){
        RecoveryData rd = new RecoveryData();

        // Get Order Id Group
        String orderIdGroup = rd.getOrderIdGroup();
        if (orderIdGroup.equals(-1)){
            System.exit(0);
        }

        // Get Hashmap List of Order Id Group
        List<HashMap> masterMapList = new ArrayList<>();
        masterMapList = rd.getMasterMapList(orderIdGroup);

        // Iterate List
        Integer headerId = -1;
        Boolean procHeader = true;
        for (HashMap masterMap : masterMapList){
            if(procHeader) {
                headerId = rd.insertHeaderProd(masterMap);
                procHeader = false;
                System.out.println(headerId);
            }
            masterMap.put("order_header_id", headerId);
            Integer detailId = rd.insertDetailProd(masterMap);
            Integer inoviceId = rd.insertInvoiceProd(masterMap);
            Boolean updateStatus = rd.updateProcessFlag(orderIdGroup, 2);
        }
    }

}
