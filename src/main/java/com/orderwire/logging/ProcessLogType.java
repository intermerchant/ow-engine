package com.orderwire.logging;

public class ProcessLogType {
    private String threadName;
    private String packageName;
    private String packageMethod;
    private String errorClass;
    private String processMessage;
    private String processMessageValue;
    
    public String getThreadName(){
        return this.threadName;
    }
    
    public void setThreadName(String _threadName){
        this.threadName = _threadName;
    }
    
    public String getPackageName(){
        return this.packageName;
    }
    
    public void setPackageName(String _packageName){
        this.packageName = _packageName;
    }
    
    public String getPackageMethod(){
        return this.packageMethod;
    }
    
    public void setPackageMethod(String _packageMethod){
        this.packageMethod = _packageMethod;
    }
    
    public String getErrorClass(){
        return this.errorClass;
    }
    
    public void setErrorClass(String _errorClass){
        this.errorClass = _errorClass;
    }

    public String getProcessMessage(){
        return this.processMessage;
    }
    
    public void setProcessMessage(String _processMessage){
        this.processMessage = _processMessage;
    }

    public String getProcessMessageValue(){
        return this.processMessageValue;
    }
    
    public void setProcessMessageValue(String _processMessageValue){
        this.processMessageValue = _processMessageValue;
    }

    
}
