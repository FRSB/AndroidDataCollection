package de.unihalle.ebusiness.androiddatacollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	private Button beginEndButton;
	private ToggleButton sensorsOnOffButton;
	
	private Handler handler;
	private Runnable runnable;
	
	private Boolean isGettingSensorData = false;
	
	private SensorAccess sensorAccess;
	
	private CollectedDataMap collectedDataMap;
	
	private List<String> collectedDataList;
	private ListAdapter adapter;
	private ListView listView;
	
	private Intent service;
	private Boolean isServiceOn;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        Log.i("Lifecycle", "onCreate");
        
        try {  
        	
        	collectedDataList = new ArrayList<String>();
        	
        	adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, collectedDataList);
        	
        	listView = (ListView) findViewById(R.id.listview);
        	
        	listView.setAdapter(adapter);
        	
        	sensorAccess = new SensorAccess(this, false); //no writing to file if false
        	
        	service = new Intent(this, SensorService.class);
        	
        	addBeginEndButton();
        	addSensorsOnOffButton();
//        	getServiceState();
        	
//        	startService(service);
        	isServiceOn = true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    	}
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	Log.i("Lifecycle", "onStop");
    }
    
    public void schedule() {
    	try {
			handler = new Handler();
			handler.postDelayed(runnable, 0);
			
			runnable = new Runnable() {
				@Override
				public void run() {
//					sensorAccess.startSensors();
					sensorAccess.stopSensors();
					collectedDataMap = sensorAccess.getUIData();
					updateUI();
					isGettingSensorData = true;
					handler.postDelayed(runnable, 10000); 
				}
			};
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void updateUI() {    	
    	convertMapToList();
    	
    	adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, collectedDataList);
    	
    	listView.setAdapter(adapter);
    }
    
    public void convertMapToList() {
    	collectedDataList = new ArrayList<String>();
    	Set<String> keySet = collectedDataMap.getAllKeys();
    	
    	for (String string : keySet) {
    		collectedDataList.add(string + ": " + collectedDataMap.get(string));
    	}
    		
    }
    
    public void addBeginEndButton() {
		beginEndButton = ((Button) findViewById(R.id.beginEndButton));				
		
		Button.OnClickListener beginEndButtonListener = new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {				
									
				if (beginEndButton.getText() == getString(R.string.button_begin)) {
					
					beginEndButton.setText(getString(R.string.button_end));
					schedule();
		
				} else {
					beginEndButton.setText(getString(R.string.button_begin));
					handler.removeCallbacks(runnable);		
				}
			}
			
		};

		beginEndButton.setOnClickListener(beginEndButtonListener);
    }
    
    public void addSensorsOnOffButton() {
    	sensorsOnOffButton = (ToggleButton) findViewById(R.id.serviceOnOffButton);

    	Button.OnClickListener sensorsOnOffButtonListener = new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (sensorsOnOffButton.isChecked()) {
					sensorsOnOffButton.setChecked(false);
					sensorsOnOffButton.setText(R.string.button_service_off);			
					
					stopService(service);
					
				}	else {
					sensorsOnOffButton.setChecked(true);
					sensorsOnOffButton.setText(R.string.button_service_on);
					
					startService(service);
				}
			}
    		
    	};

    	sensorsOnOffButton.setOnClickListener(sensorsOnOffButtonListener);
    }
    
//    public void getServiceState() {
//    	    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//    	    for (RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
//    	        if (SensorService.class.getName().equals(service.service.getClassName())) {
//    	            isServiceOn = true;
//    	        }
//    	    }
//    	    isServiceOn = false;    	
//    }
}
 