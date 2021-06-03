package com.example.myapplication.xstore.streaming;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.GlobalData;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.xstore.message.records.SBalanceRecord;
import com.example.myapplication.xstore.message.records.SCandleRecord;
import com.example.myapplication.xstore.message.records.SKeepAliveRecord;
import com.example.myapplication.xstore.message.records.SNewsRecord;
import com.example.myapplication.xstore.message.records.SProfitRecord;
import com.example.myapplication.xstore.message.records.STickRecord;
import com.example.myapplication.xstore.message.records.STradeRecord;
import com.example.myapplication.xstore.message.records.STradeStatusRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.abs;

public class StreamingListener implements StreamingListenerInterface {

	Context c;
	String CHANNEL_ID = "my_channel_01";

	class Tick{
		public Double ask, bid;

		public Tick(Double ask, Double bid){
			this.ask  = ask; this.bid = bid;
		}

		public Double getChangeAsk(Double ask){
			return (this.ask - ask)/this.ask;
		}
		public Double getChangeBid(Double bid){
			return (this.bid - bid)/this.bid;
		}
	}
	Map<String,Tick> symbols = new HashMap<String, Tick>();

	public StreamingListener(Context c) {
		this.c = c;
		int NOTIFICATION_ID = 234;
		NotificationManager notificationManager = (NotificationManager) this.c.getSystemService(Context.NOTIFICATION_SERVICE);

		this.symbols.put("EURJPY",new Tick(0.0,0.0));
		this.symbols.put("EURUSD",new Tick(0.0,0.0));
		this.symbols.put("EURGBP",new Tick(0.0,0.0));

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

			CharSequence name = "my_channel";
			String Description = "This is my channel";
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
			mChannel.setDescription(Description);
			mChannel.enableLights(true);
			mChannel.setLightColor(Color.RED);
			mChannel.enableVibration(true);
			mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
			mChannel.setShowBadge(false);
			notificationManager.createNotificationChannel(mChannel);
		}
	}

	@Override
	public void receiveTradeRecord(STradeRecord tradeRecord) {
		System.out.println(tradeRecord.toString());
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void receiveTickRecord(STickRecord tickRecord) {
		Intent intent = new Intent(this.c, MainActivity.class);
		//intent.getstr
		GlobalData globalDataInstance = GlobalData.getInstance();
		String logInstrumentCode = globalDataInstance.logInstrumentCode;

		if(logInstrumentCode.equals(tickRecord.getSymbol())){

		}

		PendingIntent contentIntent = PendingIntent.getActivity(this.c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		int r = new Random().nextInt(1000000);


		Tick oldTick = this.symbols.get(tickRecord.getSymbol());
		Tick newTick = new Tick(tickRecord.getAsk(), tickRecord.getBid());
		if(oldTick == null){
			return;
		}
		Double askChange = oldTick.getChangeAsk(tickRecord.getAsk());
		boolean notify = false;
		String notificationText ="" + tickRecord.getSymbol() + ". ";

		if(abs(askChange) > 1.0) {
			notificationText += "Ask: %change " + askChange + "%. New:" + newTick.ask + ", Old: " + oldTick.ask + ".";
			notify = true;
		}

		Double bidChange = oldTick.getChangeBid(tickRecord.getBid());
		if(abs(bidChange) > 1.0) {
			notificationText += "Bid: %change " + askChange + "%. New:" + newTick.bid + ", Old: " + oldTick.bid + ".";
			notify = true;
		}
		if(!notify)
			return;

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this.c, CHANNEL_ID)
						.setSmallIcon(R.drawable.notification_icon)
						.setContentTitle(notificationText)
						.setContentText(tickRecord.toString()); //Required on Gingerbread and below

		if(notify) {
			this.symbols.replace(tickRecord.getSymbol(), newTick);
		}

		NotificationManager notificationManager = (NotificationManager) this.c.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(r, mBuilder.build());

	}

	@Override
	public void receiveBalanceRecord(SBalanceRecord balanceRecord) {
		System.out.println(balanceRecord.toString());
	}

	@Override
	public void receiveNewsRecord(SNewsRecord newsRecord) {
		System.out.println(newsRecord.toString());
	}

	@Override
	public void receiveKeepAliveRecord(SKeepAliveRecord keepAliveRecord) {
		Intent intent = new Intent(this.c, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this.c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		int r = new Random().nextInt(1000000);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this.c, CHANNEL_ID)
						.setSmallIcon(R.drawable.notification_icon)
						.setContentTitle("My notification")
						.setContentText(keepAliveRecord.toString()); //Required on Gingerbread and below

		NotificationManager notificationManager = (NotificationManager) this.c.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(r, mBuilder.build());

		System.out.println(keepAliveRecord.toString());

	}

	@Override
	public void receiveCandleRecord(SCandleRecord candleRecord) {
		System.out.println(candleRecord.toString());
	}
	
	@Override
	public void receiveTradeStatusRecord(STradeStatusRecord tradeStatusRecord) {
		System.out.println(tradeStatusRecord.toString());
	}

	@Override
	public void receiveProfitRecord(SProfitRecord profitRecord) {
		System.out.println(profitRecord.toString());
	}
}