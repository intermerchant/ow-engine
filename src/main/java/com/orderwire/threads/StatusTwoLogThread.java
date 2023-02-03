package com.orderwire.threads;

import com.orderwire.api.EmailAPI;
import com.orderwire.api.StatusTwoLogThreadAPI;
import com.orderwire.data.DataAlertUploadTempFileNotification;
import com.orderwire.data.DataCommon;
import com.orderwire.data.DataStatusTwoLog;
import com.orderwire.documents.StatusTwoLogEmailDocument;
import com.orderwire.excp.NoStatusTwoFoundException;
import com.orderwire.logging.UtilLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatusTwoLogThread implements Runnable{
    private final String owThreadName;
    private final Integer owTaskSubscribeId;
    private final Integer owTaskId;
    private final String owSeperator = " | ";
    private Thread t;

    public StatusTwoLogThread(HashMap threadMap){
        owThreadName = threadMap.get("owThreadName").toString();
        owTaskSubscribeId = Integer.valueOf(threadMap.get("owOrderwireTaskSubscribeId").toString());
        owTaskId = Integer.valueOf(threadMap.get("owOrderwireTaskId").toString());
    }

    @Override
    public void run() {
        Boolean NothingToProcessFlag = false;
        Integer owTaskSubscribeJournalId = -1;
        DataCommon dc = new DataCommon(owTaskSubscribeId, owThreadName);
        List<HashMap> statusTwoMapList = new ArrayList<>();
        String emailMessage = "";

        try{
            /* *******************  GET STATUS TWO COUNT ******************** */
            DataStatusTwoLog dstl = new DataStatusTwoLog(owThreadName);


            // get map List of status 2 records ....
            statusTwoMapList = dstl.getStatusTwoLogMapList();
            if(statusTwoMapList.isEmpty()){
                throw new NoStatusTwoFoundException();
            }

            /* *********************   Start Task  **********************************  */
            owTaskSubscribeJournalId = dc.startTaskSubscribedJournal();
            UtilLogger.setDbStatus(owThreadName, "StatusTwoLogThread", "run()", "", "Start Task Subscribed", owTaskSubscribeId.toString());


            /* *******************   get Email Header Template from File ******************** */
            // StatusTwoLogEmailDocument emailDocument = new StatusTwoLogEmailDocument(StatusTwoMap, detailMapList);
            //String emailHeaderMessage = emailDocument.loadEmailHeaderTemplate();


            // Get DataAlertUploadTempFileNotification
            DataAlertUploadTempFileNotification dautfn = new DataAlertUploadTempFileNotification(owThreadName);
            String emailTo = dautfn.getUploadTempAlertEmailTo();


            //Build HTML Email Message with file list
            //emailMessage = autfnApi.BuildUploadTempFileEmail(statusTwoMapList);
            HashMap<String, String> emailMap = new HashMap<>();
            emailMap.put("email_to", emailTo);
            emailMap.put("email_cc", "");
            emailMap.put("email_bcc", "");
            emailMap.put("email_subject", "Orderwire Upload Temp File Alert");
            emailMap.put("email_message", emailMessage);

            EmailAPI emailAPI = new EmailAPI(owThreadName);
            Boolean procStatus = emailAPI.sendHtmlEmail(emailMap);


            /* ******************* Monitor Status 2 Orderwire Log Process *************************** */
            StatusTwoLogThreadAPI stltApi = new StatusTwoLogThreadAPI(owThreadName);
            Boolean stltStatus = false;
            //stltStatus = stltApi.BuildStatusTwoLogReport();

            if(stltStatus){
                UtilLogger.setDbStatus(owThreadName, "StatusTwoLogThread", "run()", "", "Status Two Log Thread", "Success");
            } else {
                UtilLogger.setDbStatus(owThreadName, "StatusTwoLogThread", "run()", "", "Status Two Log Thread", "Credit Memo Not Created");
            }
        } catch (NoStatusTwoFoundException exce){
            NothingToProcessFlag = true;
        }catch(Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "StatusTwoLogThread", "run()", "Exception", exce.getMessage(), owTaskSubscribeId.toString());
        }finally {
            /* **************** End the Journal Process ********************* */
            Boolean endTaskStatus = dc.endTaskSubscribedJournal(owTaskSubscribeJournalId);
            UtilLogger.setDbStatus(owThreadName, "StatusTwoLogThread", "run()", "", "End Task Subscribed", owTaskSubscribeId.toString());
        }


    }

    public void start () {
        if (t == null){
            t = new Thread (this, owThreadName);
            t.start ();
        }
    }

}
