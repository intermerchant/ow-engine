package com.orderwire.threads;

import com.orderwire.api.GenerateCreditMemoAPI;
import com.orderwire.data.DataCommon;
import com.orderwire.excp.ActivateTimerException;
import com.orderwire.excp.CreditMemoNotDataFoundException;
import com.orderwire.logging.UtilLogger;

import java.util.HashMap;

public class GenerateCreditMemoThread implements Runnable{
    private final String owThreadName;
    private final Integer owTaskSubscribeId;
    private final Integer owTaskId;
    private final String owSeperator = " | ";
    private Thread t;

    public GenerateCreditMemoThread(HashMap threadMap){
        owThreadName = threadMap.get("owThreadName").toString();
        owTaskSubscribeId = Integer.valueOf(threadMap.get("owOrderwireTaskSubscribeId").toString());
        owTaskId = Integer.valueOf(threadMap.get("owOrderwireTaskId").toString());
    }

    @Override
    public void run() {
        Boolean procStatus = true;
        Integer owTaskSubscribeJournalId = -1;
        DataCommon dc = new DataCommon(owTaskSubscribeId, owThreadName);

        try {
            Integer creditMemoId = dc.getCreditMemoGenerate();
            if (creditMemoId.equals(-1)) {
                throw new CreditMemoNotDataFoundException();
            }

            /* *********************   Start Task  **********************************  */
            UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoThread", "run()", "", "Start Task Subscribed", owTaskSubscribeId.toString());
            owTaskSubscribeJournalId = dc.startTaskSubscribedJournal();



            /* ******************* Generate Invoice Process *************************** */
            GenerateCreditMemoAPI gcmApi = new GenerateCreditMemoAPI(owThreadName, creditMemoId);
            Boolean gcmStatus = gcmApi.BuildCreditMemo();

            if(gcmStatus){
                UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoThread", "run()", "", "Generate Credit Memo Thread", "Success");
            } else {
                UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoThread", "run()", "", "Generate Credit Memo Thread", "Credit Memo Not Created");
            }

        }catch (CreditMemoNotDataFoundException exce){
            procStatus = false;
        }catch(Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoThread", "run()", "Exception", exce.getMessage(), owTaskSubscribeId.toString());
        }finally {
            /* **************** End the Journal Process ********************* */
            Boolean endTaskStatus = dc.endTaskSubscribedStatus();

            if (procStatus) {
                Boolean endTaskJournalStatus = dc.endTaskSubscribedJournal(owTaskSubscribeJournalId);
                UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoThread", "run()", "", "End Task Status", owTaskSubscribeId.toString());
                UtilLogger.setDbStatus(owThreadName, "GenerateCreditMemoThread", "run()", "", "End Task Journal Subscribed", owTaskSubscribeId.toString());
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
