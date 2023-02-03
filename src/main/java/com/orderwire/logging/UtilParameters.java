package com.orderwire.logging;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilParameters {

    public static boolean DEBUG = false;
   // public static String LOGGER_FOLDER = "C:\\NetBeansProjects\\TwitterPilot\\";
    public static String LOGGER_OWPROP_FILE = "owProperties.properties";    
    public static String LOGGER_FILE = "owLog.log";
    public static String LOGGER_THREAD_FILE = "owThread.log";

    public Path getOSFolderPath(){
        Path folderPath = null;
        String os = System.getProperty("os.name");
            if (os.toLowerCase().contains("windows")){
                folderPath = Paths.get("owlogs");
            } else if(os.toLowerCase().contains("linux")){
                folderPath = Paths.get("/opt/owengine/owlogs/");
            }
        return folderPath;
        
    }
    
    public String getOSFolderString(){
        String folderPath = "";
        String os = System.getProperty("os.name");
            if (os.toLowerCase().contains("windows")){
                folderPath = "C:\\temp\\";
            } else if(os.toLowerCase().contains("linux")){
                folderPath = "/opt/owengine/owlogs/";
            }
        return folderPath;
        
    }
    
    public static String getTimeStamp () {
        SimpleDateFormat sf = new SimpleDateFormat("MMddyyyy_HHmmssSSS");
	sf.format(new Date());
	String str = sf.format(new Date());
        return str;
    }
    
    public static String getNowDate () {
        SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yyyy");
	sf.format(new Date());
	String str = sf.format(new Date());
        return str;
    }

    public static String getNowTime () {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
	sf.format(new Date());
	String str = sf.format(new Date());
        return str;
    }

    public static String getArchiveDate () {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy_MM_dd");
	sf.format(new Date());
	String str = sf.format(new Date());
        return str;
    }   
}