package com.example.myapplication.xstore.streaming;

public class BalanceStop extends StreamingCommandRecord {

	@Override
	protected String getCommand() {
		return "stopBalance";
	}
}