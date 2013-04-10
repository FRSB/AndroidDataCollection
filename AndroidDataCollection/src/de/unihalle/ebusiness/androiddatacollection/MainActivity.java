package de.unihalle.ebusiness.androiddatacollection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	private ToggleButton showSensorDataButton;
	private ToggleButton sensorsButton;
	
	private Handler handler;
	private Runnable runnable;
	
	private Boolean isGettingSensorData = false;
	
	private SensorAccess sensorAccess;
	
	private CollectedDataMap collectedDataMap;
	
	private List<String> collectedDataList;
	private ListAdapter adapter;
	private ListView listView;
	
	private Intent service;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        Log.i("Lifecycle", "onCreate");
 
    	sensorAccess = new SensorAccess(this, false); //no writing to file if false
    	
    	handler = new Handler();
    	
    	service = new Intent(this, SensorService.class);
    	
    	addViewList();
    	addShowSensorDataButton();
    	addSensorsOnOffButton();
    }    
       
    @Override
    protected void onResume() {
    	super.onResume();
    	Log.i("Lifecycle", "onResume");
    }
    
    protected void onPause() {
    	super.onPause();
    	Log.i("Lifecycle", "onPause");    	
    	
    	if (isGettingSensorData) {
    		sensorAccess.stopSensors();
			handler.removeCallbacks(runnable);
			isGettingSensorData = false;
			showSensorDataButton.setChecked(false);
    	}
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	Log.i("Lifecycle", "onStop");
    }
    
    public void schedule() {
    	try {
    		final int fetchTime = (Integer.parseInt(((EditText) findViewById(R.id.changeFetchRateEditText)).getText().toString())) * 1000;    		
    		
			runnable = new Runnable() {
				@Override
				public void run() {
					
					sensorAccess.startSensors();
					sensorAccess.stopSensors();					
					updateUI();
					isGettingSensorData = true;
					handler.postDelayed(runnable, fetchTime); 
				}
			};
			
			handler.post(runnable);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void addViewList() {
    	collectedDataList = new ArrayList<String>();        	
    	collectedDataMap = sensorAccess.getUIData();
    	listView = (ListView) findViewById(R.id.listview);
    	
    	convertMapToList();
    	
    	adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_view_item, collectedDataList);
    	
    	listView.setAdapter(adapter);
    }
    
    public void updateUI() { 
    	int index = listView.getFirstVisiblePosition();
    	collectedDataMap = sensorAccess.getUIData();
    	
    	convertMapToList();
    	
    	adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_view_item, collectedDataList);
    	
    	listView.setAdapter(adapter);
    	listView.setSelectionFromTop(index, 0);
    }
    
    public void convertMapToList() {
    	collectedDataList = new ArrayList<String>();
    	Set<String> keySet = collectedDataMap.getAllKeys();
    	
    	for (String string : keySet) {
    		collectedDataList.add(string + ": " + collectedDataMap.get(string));
    	}
    		
    }
    
    public void addShowSensorDataButton() {
		showSensorDataButton = ((ToggleButton) findViewById(R.id.showSensorDataButton));
		
		if (isGettingSensorData) {
			showSensorDataButton.setChecked(true);
		} else showSensorDataButton.setChecked(false);
		
		Button.OnClickListener beginEndButtonListener = new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {				
									
				if (showSensorDataButton.isChecked()) {				
					schedule();		
				} else	handler.removeCallbacks(runnable);				
			}			
		};

		showSensorDataButton.setOnClickListener(beginEndButtonListener);
    }
    
    public void addSensorsOnOffButton() {
    	sensorsButton = (ToggleButton) findViewById(R.id.serviceButton);
    	
    	if (getServiceState()) {
    		sensorsButton.setChecked(true);
    	} else sensorsButton.setChecked(false);

    	OnClickListener sensorsOnOffButtonListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (sensorsButton.isChecked()) {
					createService();				
				}	else {
					destroyService();
				}
			}
    		
    	};

    	sensorsButton.setOnClickListener(sensorsOnOffButtonListener);
    }
    
    public Boolean getServiceState() {
    	    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    	    for (RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
    	        if (SensorService.class.getName().equals(service.service.getClassName())) {
    	            return true;
    	        }
    	    }
    	    return false;    	
    }
    
    public void createService() {
		final int fetchTime = (Integer.parseInt(((EditText) findViewById(R.id.changeFetchRateEditText)).getText().toString())) * 1000;
    	
    	pendingIntent = PendingIntent.getService(MainActivity.this, 0, service, 0);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), fetchTime, pendingIntent);
    }
    
    public void destroyService() {
    	alarmManager.cancel(pendingIntent);
    	stopService(service);
    }
}
 