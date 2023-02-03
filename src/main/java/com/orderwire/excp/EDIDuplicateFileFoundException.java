package com.orderwire.excp;

public class EDIDuplicateFileFoundException extends Exception{
    //Parameterless Constructor
    public EDIDuplicateFileFoundException() {}

    //Constructor that accepts a message
    public EDIDuplicateFileFoundException(String message){
        super(message);
    }

}
