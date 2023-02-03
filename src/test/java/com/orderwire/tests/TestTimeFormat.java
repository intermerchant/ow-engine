package com.orderwire.tests;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TestTimeFormat {


    public static void main(String[] args) {

        testDate();
    }

    private static void testDate(){

        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date mNow = new Date();
        Date fNow = new Date(mNow.getTime() + 30 * 1000);
        String output = newFormat.format(fNow);

        System.out.println(fNow);
        System.out.println(output);

    }


    private static void mymy(){

        Instant time = Instant.parse("2020-01-08T15:00:12-05:00");
        Date myDate = Date.from(time);
        String myPattern = "MM-dd-yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(myPattern);
        String formattedDate = formatter.format(myDate);

        System.out.println(formattedDate);

    }

}
