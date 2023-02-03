package com.orderwire.excp;

public class InvoiceNotDataFoundException extends Exception{
    //Parameterless Constructor
    public InvoiceNotDataFoundException() {}

    //Constructor that accepts a message
    public InvoiceNotDataFoundException(String message){
         super(message);
    }        

    
}
