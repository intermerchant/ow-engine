package com.orderwire.ow.engine;

import com.orderwire.data.DataCommon;
import com.orderwire.data.DataShutdownEngine;
import com.orderwire.excp.ActivateTimerException;
import com.orderwire.excp.NoAccountTaskFoundException;
import com.orderwire.threads.*;
import com.orderwire.logging.UtilLogger;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OrderwireEngine {
    
    public OrderwireEngine (){
    }
    
    public static void main(String[] args) {

        if (0 == args.length){
            UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "No Parameter", "No Parameter", "main", "");
        }

        if (args[0].equals("Shutdown")){
            shutdownEngine();
        } else if (args[0].equals("Start")) {
            runTheThread();
        } else {
            UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "Invalid Parameter", "Invalid Parameter", "main", "");
        }
    }

    
    private static void runTheThread(){
        
        String threadName = "";
        
        DataCommon dc = new DataCommon();
        UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "Java Home Setting", System.getenv("JAVA_HOME"));
        UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "Start Engine", "Start Engine");
        Boolean DEBUG = dc.getDebugMode();
        UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "Debug Mode", DEBUG.toString());
        Boolean resetStatus = dc.resetTaskStatus();
        UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "Reset Status", resetStatus.toString());
        String propRunCycle = dc.getPropRunCycle();
        UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "propRunCycle", propRunCycle);
        Integer propThreadCount = dc.getPropThreadCount();
        UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "propThreadCount", propThreadCount.toString());
        Integer propMaxThreadCount = dc.getPropMaxThreadCount();
        UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "maxThreadCount", propMaxThreadCount.toString());
        Integer ArrayBlockingQueueCount = (propMaxThreadCount / 2);
        UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "ArrayBlockingQueueCount", ArrayBlockingQueueCount.toString());
        
        RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(propThreadCount, propMaxThreadCount, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(ArrayBlockingQueueCount), threadFactory, rejectionHandler);
        MonitorThread monitor = new MonitorThread(executor, 120);
        Thread monitorThread = new Thread(monitor);
        monitorThread.start();
        
        UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "START", "START");
        Integer owTaskSubscribeId = -1;

        // Start Run Cycle Loop
        while ( propRunCycle.equals("ON") ){   
            try {
                Integer owTaskId = -1;
                Integer activeTasksCount = -1;
                Integer _activeTaskCountDelay = dc.getActiveTasksCountDelay();

                // active a pause delay if Active Tasks Count (task_status = 1) EQUALS Max Thread Count
                do {
                    activeTasksCount = dc.getActiveTasksCount();
                    if (activeTasksCount.equals(propMaxThreadCount)) {
                        Thread.sleep(_activeTaskCountDelay * 1000);
                        UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "activeMaxThreadCountDelay", "");
                    }
                } while (activeTasksCount.equals(propMaxThreadCount));

                HashMap<String,String> maps = new HashMap<>();

                if (DEBUG) {
                    //maps = dc.getAccountTask(52);
                    // maps = dc.getAccountTask();
                    maps = dc.getAccountTaskUpdate();
                } else {
                    // maps = dc.getAccountTask();
                    maps = dc.getAccountTaskUpdate();
                }

                owTaskSubscribeId = Integer.valueOf(maps.get("owOrderwireTaskSubscribeId"));
                owTaskId = Integer.valueOf(maps.get("owOrderwireTaskId"));

                if (owTaskSubscribeId.equals(-1)) {
                    throw new NoAccountTaskFoundException("No Active Account Task Found");
                }

                Boolean activateTimer = dc.getTaskTimerUpdate(owTaskSubscribeId);
                if(!activateTimer){
                    throw new ActivateTimerException("Activate Time Exception");
                }

                /* ***********  Task Threads *********************** */
                long epochTimestamp = System.currentTimeMillis();
                threadName = owTaskId + "-" + epochTimestamp;
                // UtilLogger.setDbStatus(threadName, "OrderwireEngine", "runTheThread", "", "owTaskSubscribeId", owTaskSubscribeId.toString());
                // UtilLogger.setDbStatus(threadName, "OrderwireEngine", "runTheThread", "", "owTaskId", owTaskId.toString());
                if (owTaskId.equals(52)) {
                    maps.put("owThreadName", threadName);
                    Runnable IngestPurchaseOrder = new IngestPurchaseOrderThread(maps);
                    executor.execute(IngestPurchaseOrder);
                } else if (owTaskId.equals(53)) {
                    maps.put("owThreadName", threadName);
                    Runnable GenerateInvoice = new GenerateInvoiceThread(maps);
                    executor.execute(GenerateInvoice);
                } else if (owTaskId.equals(54)) {
                    maps.put("owThreadName", threadName);
                    Runnable GenerateCreditMemo = new GenerateCreditMemoThread(maps);
                    executor.execute(GenerateCreditMemo);
                } else if (owTaskId.equals(55)) {
                    maps.put("owThreadName", threadName);
                    Runnable IngestEDIFile = new IngestEdiFileThread(maps);
                    executor.execute(IngestEDIFile);
                } else if (owTaskId.equals(56)) {
                    maps.put("owThreadName", threadName);
                    Runnable StatusTwoLogNotification = new StatusTwoLogThread(maps);
                    executor.execute(StatusTwoLogNotification);
                } else if (owTaskId.equals(57)) {
                    maps.put("owThreadName", threadName);
                    Runnable AlertUploadTempFileNotificationThread = new AlertUploadTempFileNotificationThread(maps);
                    executor.execute(AlertUploadTempFileNotificationThread);
                } else {
                    UtilLogger.setDbStatus("", "OrderwireEngine", "runTheThread", "owTaskId", "owTaskId Not Found", owTaskId.toString());
                }

                /* ************ Run Cycle Loop Delay  ******************** */
                /* normal operation set to 0, used to slow down cycle loops to inspect along the way during troubleshooting  */
                Integer runCycleLoopDelayParameter = dc.getRunLoopDelayParameter();
                if (runCycleLoopDelayParameter > 0){
                    Thread.sleep(runCycleLoopDelayParameter * 100);
                    //UtilLogger.setDbStatus(threadName, "OrderwireEngine", "runTheThread", "", "runCycleLoopDelayParameter", runCycleLoopDelayParameter.toString());
                }

            } catch (ActivateTimerException ate){
                Boolean procStatus = dc.resetActivateTimerTaskStatus(owTaskSubscribeId);
                if (DEBUG) {
                    UtilLogger.setDbStatus(threadName, "OrderwireEngine", "runTheThread", "ActivateTimerException", ate.getMessage(), owTaskSubscribeId.toString());
                }
            } catch (NoAccountTaskFoundException notfe){
                UtilLogger.setDbStatus(threadName, "OrderwireEngine", "runTheThread", "NoAccountTaskFoundException", notfe.getMessage(), "No Account Task Found");
                randomSleep();
            } catch (InterruptedException inte){
                    UtilLogger.setDbStatus(threadName, "OrderwireEngine", "runTheThread", "InterruptedException", inte.getMessage(), "");
            } catch (Exception exce) {
                UtilLogger.setDbStatus(threadName, "OrderwireEngine", "runTheThread", "IOException", exce.getMessage(), "");
            } finally {
                propRunCycle = dc.getPropRunCycle();
                if (!propRunCycle.equals("ON")) {
                    UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "Run Cycle Off Exit", propRunCycle);
                    break;
                }

                if (DEBUG) {
                    UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "Debug Mode Exit", "");
                    break;
                }
            }
        }//end while loop
        
        UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "Shutting Down Thread Executor", "");
        try { 
            executor.shutdown();
            if (!executor.awaitTermination(45, TimeUnit.MINUTES)){
                UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "Shutting Down Now", "awaitTermination");
                executor.shutdownNow();
            }       
            while (!executor.isTerminated()) {
            }
            UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "Executor Is Terminated", "isTerminated");
            monitor.shutdown();
            UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "Monitor Shutdown", "Monitor Shutdown");
        } catch (InterruptedException iex) {
            UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "InterruptedException", "Interrupted Exception", iex.getMessage());
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }finally{
            UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "runTheThread", "", "Exit", "Exit Engine");
        }

    }    

    private static void shutdownEngine(){
        UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "shutdownEngine", "", "shutdownEngine", "");
        DataShutdownEngine dse = new DataShutdownEngine();
        Boolean procStatus = dse.shutdownEngine();
    }
    
    private static long randomSleepRange(int min, int max){
        long range = (max - min) + 1;     
        long xxx = (long) ((Math.random() * range) + min);
        return xxx;
    }

    private static void randomSleep(){
        try{
            long randomSleepLength = randomSleepRange(991, 5001);
            Double sleepSeconds = (Double.valueOf(randomSleepLength) / 1000);
            UtilLogger.setDbStatus("OrderwireEngine", "OrderwireEngine", "randomSleep", "", "Random Sleep", sleepSeconds + " Seconds");
            Thread.sleep(randomSleepLength);
        } catch (Exception exce){

        } finally {

        }
    }

    

      
}
