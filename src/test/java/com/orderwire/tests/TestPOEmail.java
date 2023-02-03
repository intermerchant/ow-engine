package com.orderwire.tests;

import com.orderwire.api.GeneratePOEmailAPI;

public class TestPOEmail {

    public static void main(String[] args) {

        String testString = testBuildEmail();
        System.out.println("pause");
    }


    public static String testBuildEmail(){
        String htmlEmail = "";
        try {

            GeneratePOEmailAPI testEmailMail = new GeneratePOEmailAPI("testThreadName", 2430);
            Boolean procStatus = testEmailMail.BuildPOEmail();

            System.out.println("pause");
            System.out.println(htmlEmail);


        } catch (Exception exce){
            System.out.println(exce.getMessage());
        }

        return htmlEmail;
    }

}
