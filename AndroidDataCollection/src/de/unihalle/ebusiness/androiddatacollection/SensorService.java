package de.unihalle.ebusiness.androiddatacollection;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class SensorService extends Service {

	private Handler handler;
	private Runnable runnable;
	
	private SensorAccess sensorAccess;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate() {
		Log.i("Lifecycle", "ServiceOnCreate ");
		
        sensorAccess = new SensorAccess(this, true); //no writing to file if false
	}
	
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Lifecycle", "ServiceOnStartCommand " + startId + ": " + intent);     

        schedule();

        return START_STICKY;
    }
    
    public void onDestroy() {
    	Log.i("Lifecycle", "ServiceOnDestroy");
    	sensorAccess.stopSensors();
		handler.removeCallbacks(runnable);	
    }

	

	
    public void schedule() {
    	try {
			handler = new Handler();
			handler.postDelayed(runnable, 0);
			Log.i("Lifecycle", "ServiceOnSchedule1");
			runnable = new Runnable() {
				@Override
				public void run() {
					sensorAccess.startSensors();
					sensorAccess.stopSensors();
					Log.i("Lifecycle", "ServiceOnSchedule2");
					handler.postDelayed(runnable, 10000); 
				}
			};
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
