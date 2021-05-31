package com.example.myapplication.xstore.streaming;

public abstract class SymbolArgumentRecord extends StreamingCommandRecord {

    private String symbol;

    public SymbolArgumentRecord(String symbol) {
        this.symbol = symbol;
    }

	@Override
	protected String getExtraKey() {
		return "symbol";
	}

	@Override
	protected String getExtraValue() {
		return symbol;
	}

	@Override
	protected abstract String getCommand();
}