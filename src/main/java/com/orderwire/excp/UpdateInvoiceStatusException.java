package com.orderwire.excp;

public class UpdateInvoiceStatusException extends Exception{
  //Parameterless Constructor
    public UpdateInvoiceStatusException() {}

    //Constructor that accepts a message
    public UpdateInvoiceStatusException(String message){
         super(message);
    }        
    
}
