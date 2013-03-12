package de.unihalle.ebusiness.androiddatacollection;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private Button beginEndButton;
	
	private Handler handler;
	private Runnable runnable;
	
	private SensorAccess sensorAccess;
	
	private CollectedDataMap collectedDataMap;
	
	private TextView tvProximity;
	private TextView tvLight;
	
	private TextView tvAccelerometer_x;
	private TextView tvAccelerometer_y;
	private TextView tvAccelerometer_z;
	
	private TextView tvGyroscope_x;
	private TextView tvGyroscope_y;
	private TextView tvGyroscope_z;
	
	private TextView tvMagneticField_x;
	private TextView tvMagneticField_y;
	private TextView tvMagneticField_z;
	
	private TextView tvCellId;
	private TextView tvCellLac;
	private TextView tvCellNeighbors;
	
	private TextView tvGpsAccuracy;
	private TextView tvGpsAltitude;
	private TextView tvGpsLatitude;
	private TextView tvGpsLongitude;
	private TextView tvGpsBearing;
	private TextView tvGpsSpeed;
	
	private TextView tvRingerMode;

	private TextView tvAirplaneMode;
	
	private TextView tvBluetoothMode;

	private TextView tvDeviceId;
	private TextView tvPhoneType;

	private TextView tvWifiSsid;
	private TextView tvWifiRssi;
	
	private TextView tvOperatorState;
	private TextView tvOperatorRoaming;
	private TextView tvOperatorName;
	private TextView tvIncomingNumber;
	private TextView tvCellSignalStrength;
	
	private TextView tvBatteryLevel;
	private TextView tvBatteryPlugged;
	private TextView tvBatteryTemperature;
	
	private TextView tvSimCountry;
	private TextView tvSimOperator;
	private TextView tvSimOperatorName;
	private TextView tvSimSerialNumber;
	private TextView tvSimState;
	private TextView tvSimSubscriberId;
	
	private TextView tvScreenBrightness;
	private TextView tvScreenOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        Log.i("Lifecycle", "onCreate");
        
        try {        	
        	sensorAccess = new SensorAccess(this);
        	
			beginEndButton = ((Button) findViewById(R.id.beginEndButton));
			
			tvProximity = (TextView) findViewById(R.id.proximity);
			tvLight = (TextView) findViewById(R.id.light);
			
			tvAccelerometer_x = (TextView) findViewById(R.id.accelerometer_x);
			tvAccelerometer_y = (TextView) findViewById(R.id.accelerometer_y);
			tvAccelerometer_z = (TextView) findViewById(R.id.accelerometer_z);
			
			tvGyroscope_x = (TextView) findViewById(R.id.gyroscope_x);
			tvGyroscope_y = (TextView) findViewById(R.id.gyroscope_y);
			tvGyroscope_z = (TextView) findViewById(R.id.gyroscope_z);
			
			tvMagneticField_x = (TextView) findViewById(R.id.magneticfield_x);
			tvMagneticField_y = (TextView) findViewById(R.id.magneticfield_y);
			tvMagneticField_z = (TextView) findViewById(R.id.magneticfield_z);
			
			tvCellId = (TextView) findViewById(R.id.cellId);
			tvCellLac = (TextView) findViewById(R.id.cellLac);
			tvCellNeighbors = (TextView) findViewById(R.id.cellNeighbors);
			
			tvGpsAccuracy = (TextView) findViewById(R.id.gps_accuracy);
			tvGpsAltitude = (TextView) findViewById(R.id.gps_altitude);
			tvGpsLatitude = (TextView) findViewById(R.id.gps_latitude);
			tvGpsLongitude = (TextView) findViewById(R.id.gps_longiitude);
			tvGpsBearing = (TextView) findViewById(R.id.gps_bearing);
			tvGpsSpeed = (TextView) findViewById(R.id.gps_speed);
			
			tvRingerMode = (TextView) findViewById(R.id.ringer_mode);

			tvAirplaneMode = (TextView) findViewById(R.id.airplane_mode);
			
			tvBluetoothMode = (TextView) findViewById(R.id.bluetooth_mode);

			tvDeviceId = (TextView) findViewById(R.id.deviceid);
			tvPhoneType = (TextView) findViewById(R.id.phone_type);

			tvWifiSsid = (TextView) findViewById(R.id.wifi_ssid);
			tvWifiRssi = (TextView) findViewById(R.id.wifi_rssi);
			
			tvOperatorState = (TextView) findViewById(R.id.operator_state);
			tvOperatorRoaming = (TextView) findViewById(R.id.operator_roaming);
			tvOperatorName = (TextView) findViewById(R.id.operator_name);
			tvIncomingNumber = (TextView) findViewById(R.id.incoming_number);
			tvCellSignalStrength = (TextView) findViewById(R.id.cellsignalstrength);
			
			tvBatteryLevel = (TextView) findViewById(R.id.battery_level);
			tvBatteryPlugged = (TextView) findViewById(R.id.battery_plugged);
			tvBatteryTemperature = (TextView) findViewById(R.id.battery_temperature);
			
			tvSimCountry = (TextView) findViewById(R.id.sim_country);
			tvSimOperator = (TextView) findViewById(R.id.sim_operator);
			tvSimOperatorName = (TextView) findViewById(R.id.sim_operator_name);
			tvSimSerialNumber = (TextView) findViewById(R.id.sim_serial_number);
			tvSimState = (TextView) findViewById(R.id.sim_state);
			tvSimSubscriberId = (TextView) findViewById(R.id.sim_subscriber_id);
			
			tvScreenBrightness = (TextView) findViewById(R.id.screen_brightness);
			tvScreenOn = (TextView) findViewById(R.id.screen_on);
			
				
			Button.OnClickListener buttonListener = new Button.OnClickListener() {

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

			((Button) findViewById(R.id.beginEndButton)).setOnClickListener(buttonListener);
			

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }    
       
    @Override
    protected void onResume() {
    	super.onResume();
    	Log.i("Lifecycle", "onResume");
    	schedule();
    }
    
    protected void onPause() {
    	super.onPause();
    	Log.i("Lifecycle", "onPause");    	
    	
    	sensorAccess.stopSensors();
		handler.removeCallbacks(runnable);						
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
					sensorAccess.startSensors();
					sensorAccess.stopSensors();
					collectedDataMap = sensorAccess.getUIData();
					updateUI();
					handler.postDelayed(runnable, 10000);
				}
			};
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void updateUI() {
		tvAccelerometer_x.setText(collectedDataMap.get("accx"));
		tvAccelerometer_y.setText(collectedDataMap.get("accy"));
		tvAccelerometer_z.setText(collectedDataMap.get("accz"));
		
		tvGyroscope_x.setText(collectedDataMap.get("gyrox"));
		tvGyroscope_y.setText(collectedDataMap.get("gyroy"));
		tvGyroscope_z.setText(collectedDataMap.get("gyroz"));
		
		tvMagneticField_x.setText(collectedDataMap.get("magneticx"));
		tvMagneticField_y.setText(collectedDataMap.get("magneticy"));
		tvMagneticField_z.setText(collectedDataMap.get("magneticz"));
		
		tvLight.setText(collectedDataMap.get("light"));
		tvProximity.setText(collectedDataMap.get("proximity"));
		
		tvCellId.setText(collectedDataMap.get("cellid"));
		tvCellLac.setText(collectedDataMap.get("celllac"));	
		tvCellNeighbors.setText(collectedDataMap.get("cellneighbors"));
		
		tvGpsAccuracy.setText(collectedDataMap.get("gpsaccuracy"));
		tvGpsAltitude.setText(collectedDataMap.get("gpsaltitude"));
		tvGpsLatitude.setText(collectedDataMap.get("gpslatitude"));
		tvGpsLongitude.setText(collectedDataMap.get("longitude"));
		tvGpsBearing.setText(collectedDataMap.get("gpsbearing"));
		tvGpsSpeed.setText(collectedDataMap.get("gpsspeed"));
		
		tvRingerMode.setText(collectedDataMap.get("ringermode"));

		tvAirplaneMode.setText(collectedDataMap.get("airplanemode"));
		
		tvBluetoothMode.setText(collectedDataMap.get("bluetoothmode"));

		tvDeviceId.setText(collectedDataMap.get("deviceid"));
		
		tvPhoneType.setText(collectedDataMap.get("phonetype"));

		tvWifiSsid.setText(collectedDataMap.get("wifissid"));
		tvWifiRssi.setText(collectedDataMap.get("wifirssi"));
		
		tvIncomingNumber.setText(collectedDataMap.get("incomingnumber"));
		tvOperatorState.setText(collectedDataMap.get("operatorstate"));
		tvOperatorRoaming.setText(collectedDataMap.get("operatorroaming"));
		tvOperatorName.setText(collectedDataMap.get("operatorname"));
		tvCellSignalStrength.setText(collectedDataMap.get("cellsignalstrength"));

		tvBatteryPlugged.setText(collectedDataMap.get("batteryplugged"));
		tvBatteryLevel.setText(collectedDataMap.get("batterylevel"));
		tvBatteryTemperature.setText(collectedDataMap.get("batterytemperature"));
		
		tvSimCountry.setText(collectedDataMap.get("simcountry"));
		tvSimOperator.setText(collectedDataMap.get("simoperator"));
		tvSimOperatorName.setText(collectedDataMap.get("simoperatorname"));
		tvSimSerialNumber.setText(collectedDataMap.get("simserialnumber"));
		tvSimSubscriberId.setText(collectedDataMap.get("simsubscriberid"));
		tvSimState.setText(collectedDataMap.get("simstate"));
		
		tvScreenOn.setText(collectedDataMap.get("screenon"));			
		tvScreenBrightness.setText(collectedDataMap.get("screenbrightness"));
    }
    
}
 