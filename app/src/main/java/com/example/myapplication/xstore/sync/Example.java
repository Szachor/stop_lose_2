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

	public void runExample(ServerEnum server, Credentials credentials, String instrument) {
		try {
			SyncAPIConnector connector = new SyncAPIConnector(server);
			LoginResponse loginResponse = APICommandFactory.executeLoginCommand(connector, credentials);
			System.out.println(loginResponse);

			if (loginResponse.getStatus())
			{
				StreamingListener sl = new StreamingListener(this.c) {
				};

				LinkedList<String> list = new LinkedList<>();
				list.add(instrument);

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
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected Map<String,Server> getAvailableServers() {
		return ServerData.getProductionServers();
	}
}