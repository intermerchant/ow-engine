package com.orderwire.threads;

import com.orderwire.logging.UtilLogger;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        UtilLogger.setGenericStatus("owRejectedExecutionHandlerImpl.log", " RejectedExecutionHandlerImpl | rejectedExecution | " + r.toString() + " is rejected" ); 
        //Boolean executorRemove = executor.remove(r);
        //UtilLogger.setThreadStatus("RejectedExecutionHandlerImpl | rejectedExecution | executorRemove Status | " + executorRemove ); 
        try{
            executor.getQueue().clear();
            UtilLogger.setGenericStatus("owRejectedExecutionHandlerImpl.log", " | RejectedExecutionHandlerImpl | rejectedExecution | getQueue Clear | "); 
            executor.purge();
        }catch(Exception exc){
            UtilLogger.setGenericStatus("owRejectedExecutionHandlerImpl.log", " | RejectedExecutionHandlerImpl | rejectedExecution | Exception | " + exc.getMessage() ); 
        }
    }
}
