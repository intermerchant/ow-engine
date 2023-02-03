package com.orderwire.threads;

import com.orderwire.api.AlertUploadTempFileNotificationAPI;
import com.orderwire.api.EmailAPI;
import com.orderwire.data.DataAlertUploadTempFileNotification;
import com.orderwire.data.DataCommon;
import com.orderwire.excp.NoTempFileFoundException;
import com.orderwire.excp.PurchaseOrderNotDataFoundException;
import com.orderwire.logging.UtilLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlertUploadTempFileNotificationThread implements Runnable{
    private final String owThreadName;
    private final Integer owTaskSubscribeId;
    private final Integer owTaskId;
    private final String owSeperator = " | ";
    private Thread t;

    public AlertUploadTempFileNotificationThread(HashMap threadMap){
        owThreadName = threadMap.get("owThreadName").toString();
        owTaskSubscribeId = Integer.valueOf(threadMap.get("owOrderwireTaskSubscribeId").toString());
        owTaskId = Integer.valueOf(threadMap.get("owOrderwireTaskId").toString());
    }

    @Override
    public void run() {
        Boolean NothingToProcessFlag = false;
        Integer owTaskSubscribeJournalId = -1;
        DataCommon dc = new DataCommon(owTaskSubscribeId, owThreadName);
        List<HashMap> tempFileMapList = new ArrayList<>();
        String emailMessage = "";

        try{
            /* *********************   Scan for Temp Upload Files to Send Alert  **********************************  */
            AlertUploadTempFileNotificationAPI autfnApi = new AlertUploadTempFileNotificationAPI(owThreadName);
            tempFileMapList = autfnApi.tempFileScan();

            if(tempFileMapList.isEmpty()){
                throw new NoTempFileFoundException();
            }


            /* *********************   Start Task  **********************************  */
            owTaskSubscribeJournalId = dc.startTaskSubscribedJournal();

            // Get DataAlertUploadTempFileNotification
            DataAlertUploadTempFileNotification dautfn = new DataAlertUploadTempFileNotification(owThreadName);
            String emailTo = dautfn.getUploadTempAlertEmailTo();

            //Build HTML Email Message with file list
            emailMessage = autfnApi.BuildUploadTempFileEmail(tempFileMapList);
            HashMap<String, String> emailMap = new HashMap<>();
            emailMap.put("email_to", emailTo);
            emailMap.put("email_cc", "");
            emailMap.put("email_bcc", "");
            emailMap.put("email_subject", "Orderwire Upload Temp File Alert");
            emailMap.put("email_message", emailMessage);

            EmailAPI emailAPI = new EmailAPI(owThreadName);
            Boolean procStatus = emailAPI.sendHtmlEmail(emailMap);

            UtilLogger.setDbStatus(owThreadName, "AlertUploadTempFileNotificationThread", "run()", "", "Alert UploadTemp File Notification Thread", "Email Sent");

        }catch(NoTempFileFoundException ntff) {
            NothingToProcessFlag = true;
        }catch(Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "AlertUploadTempFileNotificationThread", "run()", "Exception", exce.getMessage(), owTaskSubscribeId.toString());
        }finally {
            if (!NothingToProcessFlag) {
                /* **************** End the Journal Process ********************* */
                Boolean endTaskStatus = dc.endTaskSubscribedJournal(owTaskSubscribeJournalId);
                UtilLogger.setDbStatus(owThreadName, "AlertUploadTempFileNotificationThread", "run()", "", "End Task Subscribed", owTaskSubscribeId.toString());
            }
            Boolean endTaskStatus = dc.endTaskSubscribedStatus();
            if (!NothingToProcessFlag) {
                UtilLogger.setDbStatus(owThreadName, "AlertUploadTempFileNotificationThread", "run()", "", "End Task Status", owTaskSubscribeId.toString());
            }
        }
    }

    public void start () {
        if (t == null){
            t = new Thread (this, owThreadName);
            t.start ();
        }
    }

}
