package com.orderwire.excp;


public class PurchaseOrderDuplicateException extends Exception{
    //Parameterless Constructor
    public PurchaseOrderDuplicateException() {}

    //Constructor that accepts a message
    public PurchaseOrderDuplicateException(String message){
         super(message);
    }        
}
