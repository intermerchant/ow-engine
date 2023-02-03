package com.orderwire.common;

public class ReplaceNullStrings {

    public ReplaceNullStrings(){}

    public String replaceNull(String inputString){
        return inputString == null ? "" : inputString;
    }
}
