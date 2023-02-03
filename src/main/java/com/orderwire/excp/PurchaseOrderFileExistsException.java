package com.orderwire.excp;

public class PurchaseOrderFileExistsException extends Exception{
    //Parameterless Constructor
    public PurchaseOrderFileExistsException() {}

    //Constructor that accepts a message
    public PurchaseOrderFileExistsException(String message){
         super(message);
    }        
    
}
