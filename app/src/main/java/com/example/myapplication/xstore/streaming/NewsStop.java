package com.example.myapplication.xstore.streaming;

public class NewsStop extends StreamingCommandRecord {

	@Override
	protected String getCommand() {
		return "stopNews";
	}
}