package com.example.myapplication;

public class GlobalData {
    private static GlobalData mInstance= null;

    public String logInstrumentCode;

    protected GlobalData(){}

    public static synchronized GlobalData getInstance() {
        if(null == mInstance){
            mInstance = new GlobalData();
        }
        return mInstance;
    }
}