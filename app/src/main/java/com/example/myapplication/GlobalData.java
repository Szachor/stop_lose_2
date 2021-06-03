package com.example.myapplication;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class GlobalData {
    private static GlobalData mInstance= null;

    public String logInstrumentCode;
    public LinkedBlockingQueue<String> logs = new LinkedBlockingQueue<String>();

    public List<String> runningInstruments = new LinkedList<>();

    protected GlobalData(){}

    public static synchronized GlobalData getInstance() {
        if(null == mInstance){
            mInstance = new GlobalData();
        }
        return mInstance;
    }
}