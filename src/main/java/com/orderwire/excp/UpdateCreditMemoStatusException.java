package com.orderwire.excp;

public class UpdateCreditMemoStatusException extends Exception{
  //Parameterless Constructor
    public UpdateCreditMemoStatusException() {}

    //Constructor that accepts a message
    public UpdateCreditMemoStatusException(String message){
         super(message);
    }        
    
}
