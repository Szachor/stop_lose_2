package com.example.myapplication.xstore.streaming;

import com.example.myapplication.xstore.message.records.SBalanceRecord;
import com.example.myapplication.xstore.message.records.SCandleRecord;
import com.example.myapplication.xstore.message.records.SKeepAliveRecord;
import com.example.myapplication.xstore.message.records.SNewsRecord;
import com.example.myapplication.xstore.message.records.SProfitRecord;
import com.example.myapplication.xstore.message.records.STickRecord;
import com.example.myapplication.xstore.message.records.STradeRecord;
import com.example.myapplication.xstore.message.records.STradeStatusRecord;

public interface StreamingListenerInterface {
    public void receiveTradeRecord(STradeRecord tradeRecord);
    public void receiveTickRecord(STickRecord tickRecord);
    public void receiveBalanceRecord(SBalanceRecord balanceRecord);
    public void receiveNewsRecord(SNewsRecord newsRecord);
    public void receiveTradeStatusRecord(STradeStatusRecord tradeStatusRecord);
    public void receiveProfitRecord(SProfitRecord profitRecord);
    public void receiveKeepAliveRecord(SKeepAliveRecord keepAliveRecord);
    public void receiveCandleRecord(SCandleRecord candleRecord);
}