package de.unihalle.ebusiness.androiddatacollection;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedIntentReceiver extends BroadcastReceiver{

	private Intent service;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	private int fetchTime = 60000;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		service = new Intent(context, SensorService.class);
    	
    	pendingIntent = PendingIntent.getService(context, 0, service, 0);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); 

        Calendar calendar = Calendar.getInstance();
        
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), fetchTime, pendingIntent);
		
        Log.i("Lifecycle", "onReceive");
	}

}
