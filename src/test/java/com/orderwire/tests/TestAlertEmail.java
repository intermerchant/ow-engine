package com.orderwire.tests;

import com.orderwire.api.AlertUploadTempFileNotificationAPI;
import com.orderwire.api.EmailAPI;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestAlertEmail {

    public TestAlertEmail(){}

    public static void main(String[] args) {
        Boolean procStatus = testAlertEmail();
        System.out.println("pause");
    }


    public static Boolean testAlertEmail(){
        Boolean procStatus = true;
        List<HashMap> tempFileMapList = new ArrayList<>();
        String owThreadName = "TEST-alertEmail";
        String emailMessage = "";

        try{
            AlertUploadTempFileNotificationAPI autfnApi = new AlertUploadTempFileNotificationAPI(owThreadName);
            tempFileMapList = autfnApi.tempFileScan();

            if(tempFileMapList.isEmpty()){
                System.out.println("No Files Found");
            } else {
                //Build HTML Email Message with file list
                emailMessage = autfnApi.BuildUploadTempFileEmail(tempFileMapList);
                HashMap<String, String> emailMap = new HashMap<>();
                emailMap.put("email_to", "dll@intermerchant.com");
                emailMap.put("email_cc", "");
                emailMap.put("email_bcc", "");
                emailMap.put("email_subject", "Orderwire Upload Temp File Alert");
                emailMap.put("email_message", emailMessage);

                EmailAPI emailAPI = new EmailAPI(owThreadName);
                Boolean pocSendStatus = emailAPI.sendHtmlEmail(emailMap);

                System.out.println("pause");

            }

        }catch(Exception exce) {
            System.out.println(exce.getMessage());
        }finally {
        }


        return procStatus;
    }

}
