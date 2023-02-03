package com.orderwire.tests;

import com.orderwire.smtp.HtmlEmailSender;

import java.util.HashMap;

public class TestHtmlEmail {

    public TestHtmlEmail(){}

    public static void main(String[] args) {
        testHtmlEmailSend();
    }


    public static void testHtmlEmailSend(){

        HashMap<String,String> emailDetailMap = new HashMap<>();
        emailDetailMap.put("owThreadName" , "TestEmailProcess");
        emailDetailMap.put("mailTo" , "dll@intermerchant.com");
        emailDetailMap.put("subject" , "Hello Dale");
        emailDetailMap.put("message" , "Message");

        HtmlEmailSender mailer = new HtmlEmailSender(emailDetailMap);

        try {
            mailer.sendHtmlEmail();
            System.out.println("Email sent.");
        } catch (Exception ex) {
            System.out.println("Failed to sent email.");
            ex.printStackTrace();
        }

    }

}
