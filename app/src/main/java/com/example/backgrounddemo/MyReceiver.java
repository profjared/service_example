package com.example.backgrounddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
	public static final String ACTION = "weather_fetched";
	
	public MyReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		//Override this in MyIntentService
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
