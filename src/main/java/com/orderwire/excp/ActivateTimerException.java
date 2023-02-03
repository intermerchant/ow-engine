package com.orderwire.excp;

public class ActivateTimerException extends Exception{
    //Parameterless Constructor
    public ActivateTimerException() {}

    //Constructor that accepts a message
    public ActivateTimerException(String message){
         super(message);
    }        
    
}
