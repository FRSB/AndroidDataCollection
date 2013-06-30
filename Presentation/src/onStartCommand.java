public int onStartCommand(Intent intent, int flags, int startId) {
	try {
		sensorAccess.startSensors();
		sensorAccess.stopSensors();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return START_STICKY;
}