package com.orderwire.excp;

public class PurchaseOrderTransactionException extends Exception{
    //Parameterless Constructor
    public PurchaseOrderTransactionException() {}

    //Constructor that accepts a message
    public PurchaseOrderTransactionException(String message){
         super(message);
    }        
}
