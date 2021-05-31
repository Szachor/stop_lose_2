package com.example.myapplication.xstore.streaming;

public class TickPricesStop extends SymbolArgumentRecord {

    public TickPricesStop(String symbol) {
        super(symbol);
    }

	@Override
	protected String getCommand() {
		return "stopTickPrices";
	}
}