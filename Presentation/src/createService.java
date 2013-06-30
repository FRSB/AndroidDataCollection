public void createService() {
	Calendar calendar = Calendar.getInstance();        
	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
		calendar.getTimeInMillis(), fetchTime, pendingIntent);
}