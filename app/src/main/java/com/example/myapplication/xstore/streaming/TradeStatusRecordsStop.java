package com.example.myapplication.xstore.streaming;

public class TradeStatusRecordsStop extends StreamingCommandRecord {

	@Override
	protected String getCommand() {
		return "stopTradeStatus";
	}
}