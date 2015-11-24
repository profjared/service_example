package com.example.backgrounddemo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {
	private TextView mOutputTextView;
	private EditText mZipcodeEditText;
	private MyReceiver mMyReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//field to display output of broadcast receiver
		mOutputTextView = (TextView) findViewById(R.id.output);

		//user input zip code field
		mZipcodeEditText = (EditText) findViewById(R.id.zip_code);

		mMyReceiver = new MyReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				mOutputTextView.setText("received broadcast at " + new Date().toString() + "\naction "
						+ intent.getAction() + "\noutput:\n "
						+ intent.getStringExtra(MyIntentService.BROADCAST_MESSAGE));
			}
		};
	}


	@Override
	protected void onResume() {
		super.onResume();

		IntentFilter filter = new IntentFilter(MyReceiver.ACTION);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(mMyReceiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(mMyReceiver);
	}


	public void startService(View v){
		Intent intent = generateWeatherIntent();
		startService(intent);
	}

	private Intent generateWeatherIntent(){
		Intent intent = new Intent(this, MyIntentService.class);
		intent.setAction(MyIntentService.ACTION_GET_WEATHER);
		intent.putExtra(MyIntentService.ZIP_PARAM, mZipcodeEditText.getText().toString());

		return intent;
	}

	public void scheduleService(View v){
		Intent intent = generateWeatherIntent();

		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		//cancel existing
		alarm.cancel(pendingIntent);

		//one time alarm trigger 10 seconds in future
		alarm.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance()
				.getTimeInMillis() + 10000, pendingIntent);
	}

	public void scheduleRepeatingService(View v){
		Intent intent = generateWeatherIntent();

		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		//cancel existing, if it exists
		alarm.cancel(pendingIntent);

		//start new repeating alarm - service will run every 60 seconds
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance()
				.getTimeInMillis(), 60000, pendingIntent); //must be 60 seconds or more - android restriction!

	}

	public void cancelService(View v){
		Intent intent = generateWeatherIntent();

		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		//cancel existing, if it exists
		alarm.cancel(pendingIntent);
	}
}
