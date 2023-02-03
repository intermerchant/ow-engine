package com.orderwire.excp;

public class NoEDIFileFoundException extends Exception{
    //Parameterless Constructor
    public NoEDIFileFoundException() {}

    //Constructor that accepts a message
    public NoEDIFileFoundException(String message){
         super(message);
    }        
    
}
