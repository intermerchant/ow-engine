package com.orderwire.api;

import com.orderwire.data.DataGenerateCreditMemo;
import com.orderwire.data.DataStatusTwoLog;
import com.orderwire.documents.POEmailDocument;
import com.orderwire.documents.StatusTwoLogEmailDocument;
import com.orderwire.excp.CreditMemoNotDataFoundException;
import com.orderwire.excp.StatusTwoLogCountZeroCountException;
import com.orderwire.logging.UtilLogger;
import com.orderwire.smtp.HtmlEmailSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatusTwoLogThreadAPI {
    private final String owThreadName;

    public StatusTwoLogThreadAPI(String threadName){
        owThreadName = threadName;
    }

}
