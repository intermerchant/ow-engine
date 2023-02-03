package com.orderwire.excp;

public class NoStatusTwoFoundException extends Exception{
    //Parameterless Constructor
    public NoStatusTwoFoundException() {}

    //Constructor that accepts a message
    public NoStatusTwoFoundException(String message){
         super(message);
    }        
    
}
