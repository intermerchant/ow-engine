package com.orderwire.threads;
import com.orderwire.logging.UtilLogger;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MonitorThread implements Runnable{
    private final ThreadPoolExecutor executor;
    private final Integer seconds;
    private boolean run=true;
    
    public MonitorThread(ThreadPoolExecutor executor, Integer delay){
        this.executor = executor;
        this.seconds=delay;
    }
    public void shutdown(){
        this.run=false;
    }
    @Override
    public void run()
    {
        while(run){
                /*
                UtilLogger.setGenericStatus("owMonitorThread.log",  
                    String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                        this.executor.getPoolSize(),
                        this.executor.getCorePoolSize(),
                        this.executor.getActiveCount(),
                        this.executor.getCompletedTaskCount(),
                        this.executor.getTaskCount(),
                        this.executor.isShutdown(),
                        this.executor.isTerminated()));
                        */
                
                UtilLogger.setMonitorThreadStatus(this.executor.getPoolSize(), this.executor.getCorePoolSize(), this.executor.getActiveCount(), this.executor.getCompletedTaskCount(), this.executor.getTaskCount(), this.executor.isShutdown(), this.executor.isTerminated());
         
                try {                    
                    Thread.sleep(seconds*1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MonitorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
}
    }

    
}
