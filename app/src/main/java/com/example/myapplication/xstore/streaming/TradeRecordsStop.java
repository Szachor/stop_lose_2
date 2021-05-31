package com.example.myapplication.xstore.streaming;

public class TradeRecordsStop extends StreamingCommandRecord {

	@Override
	protected String getCommand() {
		return "stopTrades";
	}
}