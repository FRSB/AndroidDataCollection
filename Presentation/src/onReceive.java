@Override
public void onReceive(Context context, Intent intent) {
	service = new Intent(context, SensorService.class);    	
	pendingIntent = PendingIntent.getService(context, 0, service, 0);
	alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	Calendar calendar = Calendar.getInstance();
	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
		calendar.getTimeInMillis(), fetchTime, pendingIntent);
}