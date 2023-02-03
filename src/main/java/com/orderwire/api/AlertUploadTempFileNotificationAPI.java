package com.orderwire.api;

import com.orderwire.excp.NoTempFileFoundException;
import com.orderwire.logging.UtilLogger;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.*;

public class AlertUploadTempFileNotificationAPI {

    private final String owThreadName;

    public AlertUploadTempFileNotificationAPI(String threadName){
        owThreadName = threadName;
    }

    public String BuildUploadTempFileEmail(List<HashMap> tempFileMapList){
        String emailHeader = "";
        String emailDetail = "";
        String emailFooter = "";
        String emailMessage = "";
        try{

            emailHeader = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                 "<head>\n" +
                 "    <meta charset=\"UTF-8\">\n" +
                 "    <title>Orderwire Upload Temp File Template</title>\n" +
                 "</head>\n" +
                 "<body>\n" +
                 "<table>\n";

            for(HashMap tempFileMap : tempFileMapList){
                emailDetail = emailDetail + "<tr><td>" + tempFileMap.get("tmpFileName") + "</td><td>" + tempFileMap.get("tmpLastModified") + "</td></tr>\n";
            }

            emailFooter = "</table>\n" +
                "</body>\n" +
                "</html>\n";

            emailMessage = emailHeader + emailDetail + emailFooter;

        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "AlertUploadTempFileNotificationAPI", "BuildPOEmail", "Exception", "Exception Error", exce.getMessage());
        } finally {

        }

        return emailMessage;
    }

    public List<HashMap> tempFileScan (){
        List<HashMap> tempFileMapList = new ArrayList<>();
        String tmpFileFolderPath = "/opt/tomcat/owupload/";

        try{

            File dir = new File(tmpFileFolderPath);
            File[] files = dir.listFiles(tmpFileFilter);
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

            if (files.length == 0) {
                throw new NoTempFileFoundException();
            }
            SimpleDateFormat sim = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            for(File file : files) {
                HashMap<String, String> tmpFileMap = new HashMap<>();
                tmpFileMap.put("tmpFileName", file.getName());
                tmpFileMap.put("tmpLastModified", sim.format(file.lastModified()));
                tempFileMapList.add(tmpFileMap);
            }

        } catch (NoTempFileFoundException neffe) {
           // UtilLogger.setDbStatus(owThreadName, "AlertUploadTempFileNotificationAPI", "tempFileScan", "No Temp File Exception", "No Temp File Found Exception", "");

        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "AlertUploadTempFileNotificationAPI", "tempFileScan", "Exception", "Exception Error", exce.getMessage());
        } finally {}

        return tempFileMapList;
    }

    FilenameFilter tmpFileFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".tmp");
        }
    };

}
