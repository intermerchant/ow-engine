package com.orderwire.excp;

public class NoAccountTaskFoundException extends Exception{
    //Parameterless Constructor
    public NoAccountTaskFoundException() {}

    //Constructor that accepts a message
    public NoAccountTaskFoundException(String message){
         super(message);
    }        
    
}
