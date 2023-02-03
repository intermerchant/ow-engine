package com.orderwire.excp;

public class NoTempFileFoundException extends Exception{
    //Parameterless Constructor
    public NoTempFileFoundException() {}

    //Constructor that accepts a message
    public NoTempFileFoundException(String message){
         super(message);
    }        
    
}
