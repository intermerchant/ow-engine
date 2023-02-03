package com.orderwire.tests;

import com.orderwire.data.TestDataCommon;

public class TestTaskTimer {

    public static void main(String[] args) {

        boolean procStatus = taskTimer();

    }

    public static Boolean taskTimer(){
        Boolean procStatus = false;

        TestDataCommon tdc = new TestDataCommon();
        procStatus = tdc.getTaskTimerUpdate();


        return procStatus;
    }


}
