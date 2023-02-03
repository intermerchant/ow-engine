package com.orderwire.tests;


import com.mysql.cj.xdevapi.Client;
import com.mysql.cj.xdevapi.ClientFactory;
import com.orderwire.api.GenerateCreditMemoAPI;
import com.orderwire.api.GenerateInvoiceAPI;
import com.orderwire.api.IngestPurchaseOrderAPI;

public class GenerateTestCases {

    public static void main(String[] args) {
        Boolean procStatus = true;
        Integer processType = 3;
        if (processType.equals(1)){
            procStatus = InvoiceTest();
        } else if (processType.equals(2)) {
            procStatus = CreditMemoTest();
        } else if (processType.equals(3)) {
            procStatus = PurchaseOrderTest();
        }


        ClientFactory cf = new ClientFactory();

        Client cli = cf.getClient("jdbc:mysql://localhost:3306/ow?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=CST6CDT", "{pooling:{enabled:true, maxSize:8, maxIdleTime:30000, queueTimeout:10000} }");



        System.out.println(procStatus);
    }


    private static Boolean InvoiceTest(){

        Boolean procStatus = true;

        String owThreadName = "INVOICE-TEST";
        Integer owInvoiceId = -1;
        /* ******************* Generate Invoice Process *************************** */
        GenerateInvoiceAPI giApi = new GenerateInvoiceAPI(owThreadName, owInvoiceId);
        Boolean giStatus = giApi.BuildInvoice();
        System.out.println(giStatus);


        return procStatus;

    }

     private static Boolean CreditMemoTest(){

        Boolean procStatus = true;

        String owThreadName = "CREDIT-MEMO-TEST";
        Integer owInvoiceId = -1;
        /* ******************* Generate Credit Memo Process *************************** */
        GenerateCreditMemoAPI gcmApi = new GenerateCreditMemoAPI(owThreadName, owInvoiceId);
        Boolean giStatus = gcmApi.BuildCreditMemo();
        System.out.println(giStatus);


        return procStatus;

    }

     private static Boolean PurchaseOrderTest(){

        Boolean procStatus = true;

        String owThreadName = "PURCHASE-ORDER-TEST";
        Integer owTaskSubscribeId = -1;
        Integer owLogId = -1;
         /* ******************* Generate Credit Memo Process *************************** */
         IngestPurchaseOrderAPI ipoApi = new IngestPurchaseOrderAPI(owThreadName, owTaskSubscribeId, owLogId);
         Integer ipoStatus = ipoApi.IngestPurchaseOrdersLogs();
         System.out.println(ipoApi);


        return procStatus;

    }

}
