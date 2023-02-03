package com.orderwire.tests;

import com.orderwire.api.GeneratePOEmailAPI;
import com.orderwire.data.DataConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class RebuildPOEmail {

    public RebuildPOEmail(){}

    public static void main(String[] args) {
        System.out.println("start");
        boolean procStatus = testBuildPOEmail();
        System.out.println("end");
    }

    public static boolean testBuildPOEmail()
    {
        Boolean procStatus = true;
        /*
        String owThreadName = "52-1662460706007";
        Integer _orderHeaderPK = 10959;
        String owThreadName = "52-1662460968055";
        Integer _orderHeaderPK = 10960;
        String owThreadName = "52-1662462746002";
        Integer _orderHeaderPK = 10961;
        String owThreadName = "52-1662467910007";
        Integer _orderHeaderPK = 10962;
        String owThreadName = "52-1662467962004";
        Integer _orderHeaderPK = 10963;
        String owThreadName = "52-1662468701003";
        Integer _orderHeaderPK = 10964;
        String owThreadName = "52-1662469010013";
        Integer _orderHeaderPK = 10965;
        String owThreadName = "52-1662473599004";
        Integer _orderHeaderPK = 10966;
        String owThreadName = "52-1662473888005";
        Integer _orderHeaderPK = 10967;
        String owThreadName = "52-1662475302010";
        Integer _orderHeaderPK = 10968;
        String owThreadName = "52-1662475313007";
        Integer _orderHeaderPK = 10969;
        String owThreadName = "52-1662480169002";
        Integer _orderHeaderPK = 10970;
        String owThreadName = "52-1662481783128";
        Integer _orderHeaderPK = 10971;
        String owThreadName = "52-1662483676014";
        Integer _orderHeaderPK = 10972;
        String owThreadName = "52-1662484253009";
        Integer _orderHeaderPK = 10973;
        String owThreadName = "52-1662486694020";
        Integer _orderHeaderPK = 10974;

        */

        try {
            String owThreadName = "52-";
            Integer _orderHeaderPK = 10975;

            GeneratePOEmailAPI poEmail = new GeneratePOEmailAPI(owThreadName, _orderHeaderPK);
            Boolean emailStatus = poEmail.BuildPOEmail();

        }catch(Exception exce) {
            System.out.println(exce.getMessage());
        }finally {

        }


    return procStatus;


    }


}
