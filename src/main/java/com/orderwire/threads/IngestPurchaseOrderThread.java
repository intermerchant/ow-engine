package com.orderwire.threads;
import com.orderwire.api.IngestPurchaseOrderAPI;
import com.orderwire.data.DataCommon;
import com.orderwire.excp.PurchaseOrderNotDataFoundException;
import com.orderwire.logging.UtilLogger;
import java.util.HashMap;

public class IngestPurchaseOrderThread implements Runnable{
    private final String owThreadName;
    private final Integer owTaskSubscribeId;
    private final Integer owTaskId;
    private final String owSeperator = " | ";
    private Thread t;
        
    public IngestPurchaseOrderThread(HashMap threadMap){
        owThreadName = threadMap.get("owThreadName").toString();
        owTaskSubscribeId = Integer.valueOf(threadMap.get("owOrderwireTaskSubscribeId").toString());
        owTaskId = Integer.valueOf(threadMap.get("owOrderwireTaskId").toString());
    }
    
    @Override
    public void run() {
        Boolean NothingToProcessFlag = false;
        Integer owTaskSubscribeJournalId = -1;
        DataCommon dc = new DataCommon(owTaskSubscribeId, owThreadName);

        try {
            //Get and Lock Log Record for Processing ... if no record in Status 1 available throw exception, skip processing, and return.
            Integer owLogId = dc.getPurchaseOrderIngestGenerate();
            //UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderThread", "run()", "", "Orderwire Log Id", owLogId.toString());
            if (owLogId.equals(-1)) {
                throw new PurchaseOrderNotDataFoundException();
            }


            /* *********************   Start Task  **********************************  */
            owTaskSubscribeJournalId = dc.startTaskSubscribedJournal();
            /* ******************* Ingest Purchase Order Process *************************** */
            IngestPurchaseOrderAPI ipoApi = new IngestPurchaseOrderAPI(owThreadName, owTaskSubscribeId, owLogId);
            Integer ipoStatus = ipoApi.IngestPurchaseOrdersLogs();
            if (ipoStatus.equals(1)) {
                UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderThread", "run()", "", "Ingest Purchase Orders Logs", "Success");
            } else if (ipoStatus.equals(2)) {
                UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderThread", "run()", "", "Ingest Purchase Orders Logs", "No PO Ingested");
            } else if (ipoStatus.equals(3)) {
                NothingToProcessFlag = true;
            }

        }catch(PurchaseOrderNotDataFoundException pondf) {
            NothingToProcessFlag = true;
        }catch(Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderThread", "run()", "Exception", exce.getMessage(), owTaskSubscribeId.toString());            
        }finally {
            /* **************** End the Journal Process ********************* */
            if (!NothingToProcessFlag) {
                Boolean endTaskJournalStatus = dc.endTaskSubscribedJournal(owTaskSubscribeJournalId);
                UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderThread", "run()", "", "End Task Subscribed", owTaskSubscribeId.toString());
            }
            Boolean endTaskStatus = dc.endTaskSubscribedStatus();
            if (!NothingToProcessFlag) {
                UtilLogger.setDbStatus(owThreadName, "IngestPurchaseOrderThread", "run()", "", "End Task Status", owTaskSubscribeId.toString());
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
