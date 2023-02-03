package com.orderwire.excp;

public class CreditMemoNotDataFoundException extends Exception{
    //Parameterless Constructor
    public CreditMemoNotDataFoundException() {}

    //Constructor that accepts a message
    public CreditMemoNotDataFoundException(String message){
         super(message);
    }        

    
}
