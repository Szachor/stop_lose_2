package com.example.myapplication.xstore.sync;

import android.content.Context;

import com.example.myapplication.xstore.message.command.APICommandFactory;
import com.example.myapplication.xstore.message.records.TickRecord;
import com.example.myapplication.xstore.message.response.LoginResponse;
import com.example.myapplication.xstore.message.response.TickPricesResponse;
import com.example.myapplication.xstore.streaming.StreamingListener;
import com.example.myapplication.xstore.sync.ServerData.ServerEnum;

import java.util.LinkedList;
import java.util.Map;

public class Example {

	Context c;
	public Example(Context c){
		this.c = c;
	}

	public void runExample(ServerEnum server, Credentials credentials, String instrument) throws Exception {
		try {
			SyncAPIConnector connector = new SyncAPIConnector(server);
			LoginResponse loginResponse = APICommandFactory.executeLoginCommand(connector, credentials);
			System.out.println(loginResponse);
			if (loginResponse != null && loginResponse.getStatus())
			{
				StreamingListener sl = new StreamingListener(this.c) {
				};

				LinkedList<String> list = new LinkedList<String>();
				String symbol = instrument;
				list.add(symbol);

				TickPricesResponse resp = APICommandFactory.executeTickPricesCommand(connector, 0L, list, 0L);
				for (TickRecord tr : resp.getTicks()) {
					System.out.println("TickPrices result: "+tr.getSymbol() + " - ask: " + tr.getAsk());
				}

				connector.connectStream(sl);
				System.out.println("Stream connected.");

				for(String l: list) {
					connector.subscribePrice(l);
				}
				connector.subscribeKeepAlive();

				Thread.sleep(10000);

				//connector.unsubscribePrice(symbol);
				//connector.unsubscribeTrades();
				//connector.disconnectStream();
				//System.out.println("Stream disconnected.");
				
				//Thread.sleep(5000);
				
				//connector.connectStream(sl);
				//System.out.println("Stream connected again.");
				//connector.disconnectStream();
				//System.out.println("Stream disconnected again.");
				//System.exit(0);
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	protected Map<String,Server> getAvailableServers() {
		return ServerData.getProductionServers();
	}
}