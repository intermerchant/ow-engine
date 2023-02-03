package com.orderwire.api;

import com.orderwire.logging.UtilLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IngestEdiFileAPI {
    private final String owThreadName;
    private final List<String> fileLines;

    public IngestEdiFileAPI(String _threadName, List<String> _fileLines){
        owThreadName = _threadName;
        fileLines = _fileLines;
    }


    public HashMap getBigMap(){
        HashMap<String, String> ediBigMap = new HashMap<>();
        String ediRecordString = getEdiRecordString();

        Pattern pattern = Pattern.compile("^BIG.*", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(ediRecordString);
        try {
            while (matcher.find()){
                String[] lineSplit = matcher.group().split("[*]", -1);
                ediBigMap.put("segmentId", lineSplit[0]);
                ediBigMap.put("bigDate", lineSplit[1]);
                ediBigMap.put("bigInvoiceNumber", substituteNull(lineSplit[2]));
                ediBigMap.put("bigDate2", lineSplit[3]);
                ediBigMap.put("bigPurchaseOrderNumber", lineSplit[4]);
                ediBigMap.put("bigReleaseNumber", lineSplit[5]);
                ediBigMap.put("bigChangeOrderSequenceNumber", lineSplit[6]);
                ediBigMap.put("bigTransactionTypeCode", lineSplit[7]);
                try {
                    ediBigMap.put("bigTransactionSetPurposeCode", lineSplit[8]);
                } catch (Exception exce){
                    ediBigMap.put("bigTransactionSetPurposeCode", "");
                }
                try {
                    ediBigMap.put("bigActionCode", lineSplit[9]);
                } catch (Exception exce) {
                    ediBigMap.put("bigActionCode", "");
                }
                try {
                    ediBigMap.put("bigInvoiceNumber2", lineSplit[10]);
                } catch (Exception exce) {
                    ediBigMap.put("bigInvoiceNumber2", "");
                }
            }

        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "IngestEdiFileAPI", "getit1List", "", "Exception", exce.getMessage());
        }

        return ediBigMap;
    }

    public List<HashMap> getit1MapList(){
        List<HashMap> it1MapList = new ArrayList<>();
        HashMap<String, String> ediIT1Map = new HashMap<>();
        String ediRecordString = getEdiRecordString();
        Pattern pattern = Pattern.compile("^IT1.*", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(ediRecordString);
        try {
            while (matcher.find()){
                HashMap<String, String> it1Map = new HashMap<>();
                String[] lineSplit = matcher.group().split("[*]", -1);
                it1Map.put("Segment", lineSplit[0]);
                it1Map.put("it1AssignedIdentification", lineSplit[1]);
                it1Map.put("it1QuantityInvoiced", lineSplit[2]);
                it1Map.put("it1UnitOfMeasurementCode", lineSplit[3]);
                it1Map.put("it1UnitPrice", lineSplit[4]);
                it1Map.put("it1BasisOfUnitPriceCode", lineSplit[5]);
                it1Map.put("it1ProductServiceIdQualifier", lineSplit[6]);
                it1Map.put("it1ProductServiceId", lineSplit[7]);
                it1MapList.add(it1Map);
            }

        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "IngestEdiFileAPI", "getit1List", "", "Exception", exce.getMessage());
        }

        return it1MapList;
    }

    private String getEdiRecordString(){
        String ediRecordString = "";
        try {
            ediRecordString = fileLines.stream().map(Object::toString).collect(Collectors.joining("\n"));
        } catch (Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "IngestEdiFileAPI", "getEdiRecordString", "", "Exception", exce.getMessage());
        }
        return ediRecordString;
    }

    private String substituteNull(String inputString){
        String returnString = "";
        returnString = inputString == null ? "" : inputString;
        return returnString;
    }

}
