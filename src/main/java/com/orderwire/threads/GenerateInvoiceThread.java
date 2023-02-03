package com.orderwire.threads;
import com.orderwire.api.GenerateInvoiceAPI;
import com.orderwire.data.DataCommon;
import com.orderwire.excp.InvoiceNotDataFoundException;
import com.orderwire.logging.UtilLogger;
import java.util.HashMap;

public class GenerateInvoiceThread implements Runnable{
    private final String owThreadName;
    private final Integer owTaskSubscribeId;
    //private final Integer owTaskId;
    private final String owSeperator = " | ";
    private Thread t;
        
    public GenerateInvoiceThread(HashMap threadMap ){
        owThreadName = threadMap.get("owThreadName").toString();
        owTaskSubscribeId = Integer.valueOf(threadMap.get("owOrderwireTaskSubscribeId").toString());
        // owTaskId = Integer.valueOf(threadMap.get("owOrderwireTaskId").toString());
    }
    
    @Override
    public void run() {
        Boolean procStatus = true;
        Integer owTaskSubscribeJournalId = -1;
        DataCommon dc = new DataCommon(owTaskSubscribeId, owThreadName);

        try {

            // get invoice with generate invoice flag = 'Y' and status code = 0
            Integer invoiceId = dc.getInvoiceGenerate();
            if (invoiceId.equals(-1)) {
                throw new InvoiceNotDataFoundException();
            }

            /* *********************   Start Task  **********************************  */
            //1
            UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceThread", "run()", "", "Start Task Subscribed", owTaskSubscribeId.toString());

            owTaskSubscribeJournalId = dc.startTaskSubscribedJournal();

            
            /* ******************* Generate Invoice Process *************************** */
            GenerateInvoiceAPI giApi = new GenerateInvoiceAPI(owThreadName, invoiceId);
            Boolean giStatus = giApi.BuildInvoice();

            if(giStatus){
                //2
                UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceThread", "run()", "", "Generate Invoice Thread", "Invoice Generated");

                /*  ************************ Dynamic Task Allocation Process  ************* */
                /// get Generate Invoice Flag Count
                Integer gifCount = dc.getGenerateInvoiceFlagCount();

                // get Active Generate Invoice Task Count
                Integer agitCount = dc.getActiveGenerateInvoiceTasksCount();

                // Allocate Dynamic Tasks based on ( gifCount - agitCount )
                boolean allocateStatus = dc.setActiveGenerateInvoiceTasks(gifCount,agitCount);
            }

        } catch (InvoiceNotDataFoundException exce){
            procStatus = false;
        }catch(Exception exce) {
            UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceThread", "run()", "Exception", exce.getMessage(), owTaskSubscribeId.toString());            
        }finally {
            /* **************** End the Journal Process ********************* */
            if (procStatus) {
                Boolean endTaskJournalStatus = dc.endTaskSubscribedJournal(owTaskSubscribeJournalId);
                UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceThread", "run()", "", "End Task Subscribed", owTaskSubscribeId.toString());
            }
            Boolean endTaskStatus = dc.endTaskSubscribedStatus();
            if (procStatus) {
                UtilLogger.setDbStatus(owThreadName, "GenerateInvoiceThread", "run()", "", "End Task Status", owTaskSubscribeId.toString());
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
