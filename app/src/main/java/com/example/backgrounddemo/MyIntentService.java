package com.example.backgrounddemo;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class MyIntentService extends IntentService {
	public static final String ACTION_GET_WEATHER = "get_weather";
	public static final String ZIP_PARAM = "zip_code";
	public static final String BROADCAST_MESSAGE = "weather_json_output";

    public MyIntentService() {
        super("MyIntentService");
    }

	@Override
	protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_GET_WEATHER.equals(action)) {
                String zipCode = intent.getStringExtra(ZIP_PARAM);
                handleGetWeatherAction(zipCode);
            }
        }
    }

    private void handleGetWeatherAction(String zipCode){
        String weatherDescription = getWeather(zipCode);

        if (weatherDescription == null) {
            //broadcast so UI can update
            broadcastMessage(getString(R.string.failed_weather));

        } else {
            //broadcast so UI can update
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MyReceiver.ACTION);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(BROADCAST_MESSAGE,
                    "Weather Description:" + weatherDescription);

            //this will update the MainActivity UI (if it is running...)
            sendBroadcast(broadcastIntent);

            //this will trigger an Android notification (MainActivity need not be running!)
            triggerNotificationWithMessage(weatherDescription,zipCode);
        }
    }

    private void broadcastMessage(String message){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MyReceiver.ACTION);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(BROADCAST_MESSAGE,
                message);

        sendBroadcast(broadcastIntent);
    }

    private void triggerNotificationWithMessage(String message, String zipCode){
        // create a pending intent to bring user to webview of weather upon clicking notification
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.wunderground.com/cgi-bin/findweather/getForecast?query=" + zipCode));
        PendingIntent pendingIntent = PendingIntent.getActivity(MyIntentService.this, 0,
                browserIntent, 0);

        // create notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                MyIntentService.this);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);

        notificationBuilder.setContentTitle(zipCode + " - " + getString(R.string.weather));
        notificationBuilder.setContentText(message);

        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private String getWeather(String zipCode) {

        String weatherJSONString = null;
        try {
            weatherJSONString = Ion.with(this)
                    .load("http://api.wunderground.com/api/26f9a89412c778c5/forecast10day/q/" + zipCode + ".json").asString().get();
        } catch (Exception e) {
            return null;
        }

        String weatherDescription = "";
        try {
            weatherDescription = ((JSONObject) new JSONObject(weatherJSONString).getJSONObject("forecast").getJSONObject("txt_forecast").getJSONArray("forecastday").get(0)).getString("fcttext");
        } catch (Exception e) {
            return null;
        }

        return weatherDescription;
        }
    }