package de.unihalle.ebusiness.androiddatacollection;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class SensorService extends Service {
	
	private SensorAccess sensorAccess;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate() {
		Log.i("Lifecycle", "ServiceOnCreate ");
		
        try {
			sensorAccess = new SensorAccess(this, true); //no writing to file if false
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Lifecycle", "ServiceOnStartCommand " + startId + ": " + intent);
        try {
			sensorAccess.startSensors();
			sensorAccess.stopSensors();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return START_STICKY;
    }
    
    public void onDestroy() {
    	Log.i("Lifecycle", "ServiceOnDestroy");
    	try {
			sensorAccess.stopSensors();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }

}
