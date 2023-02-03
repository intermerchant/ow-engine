package com.orderwire.excp;

public class PurchaseOrderNotDataFoundException extends Exception{
    //Parameterless Constructor
    public PurchaseOrderNotDataFoundException() {}

    //Constructor that accepts a message
    public PurchaseOrderNotDataFoundException(String message){
         super(message);
    }        

    
}
