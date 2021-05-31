package com.example.myapplication.xstore.streaming;

public class ProfitsStop extends StreamingCommandRecord {

	@Override
	protected String getCommand() {
		return "stopProfits";
	}
}