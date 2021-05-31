package com.example.myapplication.xstore.streaming;

public class KeepAliveStop extends StreamingCommandRecord {

	@Override
	protected String getCommand() {
		return "stopKeepAlive";
	}
}