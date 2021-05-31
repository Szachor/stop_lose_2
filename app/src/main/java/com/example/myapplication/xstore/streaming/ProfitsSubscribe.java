package com.example.myapplication.xstore.streaming;

public class ProfitsSubscribe extends RecordSubscribe {

    public ProfitsSubscribe(String streamSessionId) {
        super(streamSessionId);
    }

	@Override
	public String getCommand() {
		return "getProfits";
	}
}