package com.orderwire.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UtilLogger {

    public static String getTimeStamp () {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	sf.format(new Date());
	String str = sf.format(new Date());
        return str;
    }
    
    public static void setGenericStatus(String logFile, String status) {        
        String dateStamp = getTimeStamp();
        try {
            Path folderPath = null;
            String os = System.getProperty("os.name");
            if (os.toLowerCase().contains("windows")){
                folderPath = Paths.get("owlogs");
            } else if(os.toLowerCase().contains("linux")){
                folderPath = Paths.get("/opt/owengine/owlogs/");
            }
            String logFolderPath = folderPath.toString();
            Path logPath = Paths.get(logFolderPath, logFile);
            BufferedWriter writer = Files.newBufferedWriter(logPath,StandardCharsets.UTF_8,StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            writer.write(dateStamp + status + "\n" );
            writer.close();
        } catch (AccessDeniedException ade){
            Logger.getLogger(UtilLogger.class.getName()).log(Level.SEVERE, null, ade);
        } catch (IOException ioe) {
            Logger.getLogger(UtilLogger.class.getName()).log(Level.SEVERE, null, ioe);
        } catch (Exception exce){
            Logger.getLogger(UtilLogger.class.getName()).log(Level.SEVERE, null, exce);
        }
    }     

    public static void setTaskMapStatus(HashMap taskMapStatus){



    }

    public static HashMap setMessageTempMap(String _threadName, String _packageName, String _packageMethod, String _errorClass, String _processMethod, String _processMethodValue, String _processSortBy){

        HashMap<String, String> messageTempMap = new HashMap<>();

        try {
            messageTempMap.put("_threadName", _threadName);
            messageTempMap.put("_packageName", _packageName);
            messageTempMap.put("_packageMethod", _packageMethod);
            messageTempMap.put("_errorClass", _errorClass);
            messageTempMap.put("_processMethod", _processMethod);
            messageTempMap.put("_processMethodValue", _processMethodValue);
            messageTempMap.put("_processSortBy", _processSortBy);

        } catch (Exception exce){
            UtilLogger.setGenericStatus("owDbLogger.log", " | UtilLogger | setDbStatus | Exception " + exce.getMessage());
        }

        return messageTempMap;

    }


    public static void setDbStatus(String _threadName, String _packageName, String _packageMethod, String _errorClass, String _processMethod, String _processMethodValue){
        
        HashMap<String, String> messageMap = new HashMap<>();
        
        try {
            messageMap.put("_threadName", _threadName);
            messageMap.put("_packageName", _packageName);
            messageMap.put("_packageMethod", _packageMethod);
            messageMap.put("_errorClass", _errorClass);
            messageMap.put("_processMethod", _processMethod);
            messageMap.put("_processMethodValue", _processMethodValue);
          
            DbLogger DBLOG = new DbLogger(messageMap);
            DBLOG.writeProcesLog();
            
        } catch (Exception exce){
            UtilLogger.setGenericStatus("owDbLogger.log", " | UtilLogger | setDbStatus | Exception " + exce.getMessage());
        }
        
    }
    
    public static void setMonitorThreadStatus(Integer _poolSize, Integer _corePoolSize, Integer _activeCount, Long _completedTaskCount, Long _taskCount, Boolean _isShutdown, Boolean _isTerminated ){
        
        HashMap<String, String> messageMap = new HashMap<>();
        String dateStamp = getTimeStamp();
        
        try {
            messageMap.put("_dateStamp", dateStamp);
            messageMap.put("_poolSize", _poolSize.toString());
            messageMap.put("_corePoolSize", _corePoolSize.toString());
            messageMap.put("_activeCount", _activeCount.toString());
            messageMap.put("_completedTaskCount", _completedTaskCount.toString());
            messageMap.put("_taskCount", _taskCount.toString());
            messageMap.put("_isShutdown", _isShutdown.toString());
            messageMap.put("_isTerminated", _isTerminated.toString());
          
            MonitorLogger monitorLog = new MonitorLogger(messageMap);
            monitorLog.writeMonitorLog();
            
        } catch (Exception exce){
            UtilLogger.setGenericStatus("owDbLogger.log", " | UtilLogger | setDbStatus | Exception " + exce.getMessage());
        }
             
        
        
        
    }
}
    
