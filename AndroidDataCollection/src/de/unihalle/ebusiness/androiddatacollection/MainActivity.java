package de.unihalle.ebusiness.androiddatacollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.NeighboringCellInfo;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private Button beginEndButton;
	
	private TelephonyManager telephonyManager;
	
	private PhoneStateListener phoneStateListener;
	
	private AudioManager audioManager;
	
	private PowerManager powerManager;
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	private SensorEventListener sensorEventListener;
	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private Sensor gyroscopeSensor;
	private Sensor magneticFieldSensor;
	private Sensor lightSensor;
	private Sensor proximitySensor;
	
	private WifiManager wifiManager;
	private WifiInfo wifiInfo;
	
	private Handler handler;
	private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        Log.i("Lifecycle", "onCreate");
        
        try {     	
     	
			beginEndButton = ((Button) findViewById(R.id.beginEndButton));
				
			Button.OnClickListener buttonListener = new Button.OnClickListener() {

				@Override
				public void onClick(View arg0) {				
										
					if (beginEndButton.getText() == getString(R.string.button_begin)) {
						
						beginEndButton.setText(getString(R.string.button_end));
			        	setUpSensors();						
						getCellInformation();
						getGpsLocation();
						getRinger();
						getAirplaneMode();
						getBluetoothMode();
						getDeviceInformation();
						getWifiInformation();
						getOperatorInformation();
						getBatteryStatus();
						getSimInformation();
						getScreenBrightness();
						writeData();
						schedule();				
					} else {
						
						beginEndButton.setText(getString(R.string.button_begin));
						sensorManager.unregisterListener(sensorEventListener);
						locationManager.removeUpdates(locationListener);
						telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
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
    }
    
    protected void onPause() {
    	super.onPause();
    	Log.i("Lifecycle", "onPause");    	
    	
		beginEndButton.setText(getString(R.string.button_begin));
		sensorManager.unregisterListener(sensorEventListener);
		locationManager.removeUpdates(locationListener);
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		handler.removeCallbacks(runnable);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	Log.i("Lifecycle", "onStop");
    }
    
    public void schedule() {
    	try {
			final TextView tv1 = (TextView) findViewById(R.id.textView1);
			handler = new Handler();
			handler.postDelayed(runnable, 10000);
			
			runnable = new Runnable() {
				@Override
				public void run() {
					tv1.setText(Long.toString(System.currentTimeMillis()));
					handler.postDelayed(runnable, 10000);
				}
			};
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void setUpSensors() {
    	// the accelerometer is the result of gravity and linear acceleration, so they have been omitted
    	try {
    		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    		
			if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
				accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			}
			
			if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
				magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			}
			
			if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
				gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			}
			
			if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
				lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			}
			
			if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
				proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			}
			
			setUpSensorListener();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public void setUpSensorListener() {
    	try {
    		
    		final TextView tvProximity = (TextView) findViewById(R.id.proximity);
    		final TextView tvLight = (TextView) findViewById(R.id.light);
    		
    		final TextView tvAccelerometer_x = (TextView) findViewById(R.id.accelerometer_x);
    		final TextView tvAccelerometer_y = (TextView) findViewById(R.id.accelerometer_y);
    		final TextView tvAccelerometer_z = (TextView) findViewById(R.id.accelerometer_z);
        	
    		final TextView tvGyroscope_x = (TextView) findViewById(R.id.gyroscope_x);
    		final TextView tvGyroscope_y = (TextView) findViewById(R.id.gyroscope_y);
    		final TextView tvGyroscope_z = (TextView) findViewById(R.id.gyroscope_z);
        	
    		final TextView tvMagneticField_x = (TextView) findViewById(R.id.magneticfield_x);
    		final TextView tvMagneticField_y = (TextView) findViewById(R.id.magneticfield_y);
    		final TextView tvMagneticField_z = (TextView) findViewById(R.id.magneticfield_z);
        	
			sensorEventListener = new SensorEventListener() {
				
				@Override
				public void onSensorChanged(SensorEvent event) {
					// get all sensor data
					if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
						tvAccelerometer_x.setText(Float.toString(event.values[0]));
						tvAccelerometer_y.setText(Float.toString(event.values[1]));
						tvAccelerometer_z.setText(Float.toString(event.values[2]));
					}
					
					if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
						tvGyroscope_x.setText(Float.toString(event.values[0]));
						tvGyroscope_y.setText(Float.toString(event.values[1]));
						tvGyroscope_z.setText(Float.toString(event.values[2]));
					}
					
					if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
						tvMagneticField_x.setText(Float.toString(event.values[0]));
						tvMagneticField_y.setText(Float.toString(event.values[1]));
						tvMagneticField_z.setText(Float.toString(event.values[2]));
					}
									
					if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
						tvLight.setText(Float.toString(event.values[0]));
					}
					
					if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
						tvProximity.setText(Float.toString(event.values[0]));
					}
				}
				
				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
									
				}
			};
			
			if (accelerometerSensor != null) {
				sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
			
			if (gyroscopeSensor != null) {
				sensorManager.registerListener(sensorEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
			
			if (lightSensor != null) {
				sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
			
			if (proximitySensor != null) {
				sensorManager.registerListener(sensorEventListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
			
			if (magneticFieldSensor != null) {
				sensorManager.registerListener(sensorEventListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void getCellInformation() {
    	try {
    		    		
    		final TextView tvCellId = (TextView) findViewById(R.id.cellId);
    		final TextView tvCellLac = (TextView) findViewById(R.id.cellLac);
    		final TextView tvCellNeighbors = (TextView) findViewById(R.id.cellNeighbors);
        	        	
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			
			GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
			
			if (cellLocation != null) {
				tvCellId.setText(Integer.toString(cellLocation.getCid()));
				tvCellLac.setText(Integer.toString(cellLocation.getLac()));				
				
				
				List<NeighboringCellInfo> neighboringCellInfo = telephonyManager.getNeighboringCellInfo();
				
				String neighboringCellString = "CellID LAC RSSI | ";
				for (NeighboringCellInfo cell : neighboringCellInfo) {
					neighboringCellString = neighboringCellString + cell.getCid() + ", " + cell.getLac() + ", " + cell.getRssi() + " | ";
				}
				
				tvCellNeighbors.setText(neighboringCellString);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    public void getGpsLocation() {
    	try {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);			
						   	
			Location location = locationManager.getLastKnownLocation("gps");
			
			final TextView tvGpsAccuracy = (TextView) findViewById(R.id.gps_accuracy);
			final TextView tvGpsAltitude = (TextView) findViewById(R.id.gps_altitude);
			final TextView tvGpsLatitude = (TextView) findViewById(R.id.gps_latitude);
			final TextView tvGpsLongitude = (TextView) findViewById(R.id.gps_longiitude);
			final TextView tvGpsBearing = (TextView) findViewById(R.id.gps_bearing);
			final TextView tvGpsSpeed = (TextView) findViewById(R.id.gps_speed);
			
			if (location != null) {
				tvGpsAccuracy.setText(Double.toString(location.getAccuracy()));
				tvGpsAltitude.setText(Double.toString(location.getAltitude()));
				tvGpsLatitude.setText(Double.toString(location.getLatitude()));
				tvGpsLongitude.setText(Double.toString(location.getLongitude()));
				tvGpsBearing.setText(Double.toString(location.getBearing()));
				tvGpsSpeed.setText(Double.toString(location.getSpeed()));
			}
			
			locationListener = new LocationListener() {
				
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLocationChanged(Location location) {
			    	tvGpsAccuracy.setText(Double.toString(location.getAccuracy()));
			    	tvGpsAltitude.setText(Double.toString(location.getAltitude()));
			    	tvGpsLatitude.setText(Double.toString(location.getLatitude()));
			    	tvGpsLongitude.setText(Double.toString(location.getLongitude()));
			    	tvGpsBearing.setText(Double.toString(location.getBearing()));
			    	tvGpsSpeed.setText(Double.toString(location.getSpeed()));				
				}
			};
			
			locationManager.requestLocationUpdates("gps", 30000L, 1, locationListener);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void getRinger() {
    	
    	try {
    		final TextView tvRingerMode = (TextView) findViewById(R.id.ringer_mode);
			audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			
			switch (audioManager.getRingerMode()) {
				case AudioManager.RINGER_MODE_NORMAL:
					tvRingerMode.setText("RINGER_MODE_NORMAL");
					break;
				case AudioManager.RINGER_MODE_VIBRATE:
					tvRingerMode.setText("RINGER_MODE_VIBRATE");
					break;
				case AudioManager.RINGER_MODE_SILENT:
					tvRingerMode.setText("RINGER_MODE_SILENT");
					break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void getAirplaneMode() {    	
    	try {
    		final TextView tvAirplaneMode = (TextView) findViewById(R.id.airplane_mode);
    		
			if (Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON) == 1) {
				tvAirplaneMode.setText("AIRPLANE_MODE_ON");
			} else {
				tvAirplaneMode.setText("AIRPLANE_MODE_OFF");
			}
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void getBluetoothMode() {    	
    	try {
    		final TextView tvBluetoothMode = (TextView) findViewById(R.id.bluetooth_mode);
    		
			if (Settings.System.getInt(getContentResolver(), Settings.Global.BLUETOOTH_ON) == 1) {
				tvBluetoothMode.setText("Bluetooth_ON");
			} else {
				tvBluetoothMode.setText("Bluetooth_OFF");
			}
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void getDeviceInformation() {
    	try {
    		final TextView tvDeviceId = (TextView) findViewById(R.id.deviceid);
    		final TextView tvPhoneType = (TextView) findViewById(R.id.phone_type);
			
			tvDeviceId.setText(telephonyManager.getDeviceId());
			
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			
			if (telephonyManager.getPhoneType() == 1) {
				tvPhoneType.setText("Phone_Type_GSM");
			} else {
				    	if (telephonyManager.getPhoneType() == 2) {
				    		tvPhoneType.setText("Phone_Type_CDMA");
				    	} else {
				    		tvPhoneType.setText("Phone_Type_NONE");
				    		}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void getWifiInformation() {
    	try {
    		final TextView tvWifiSsid = (TextView) findViewById(R.id.wifi_ssid);
    		final TextView tvWifiRssi = (TextView) findViewById(R.id.wifi_rssi);
			
			wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			wifiInfo = wifiManager.getConnectionInfo();
			
			if (wifiInfo.getSSID() != null) {
				// null if not connected
				tvWifiSsid.setText(wifiInfo.getSSID());
				tvWifiRssi.setText(Integer.toString(wifiInfo.getRssi()));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void getOperatorInformation() {
		try {
			final TextView tvOperatorState = (TextView) findViewById(R.id.operator_state);
			final TextView tvOperatorRoaming = (TextView) findViewById(R.id.operator_roaming);
			final TextView tvOperatorName = (TextView) findViewById(R.id.operator_name);
			final TextView tvIncomingNumber = (TextView) findViewById(R.id.incoming_number);
    		final TextView tvCellSignalStrength = (TextView) findViewById(R.id.cellsignalstrength);			
			
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			
			phoneStateListener = new PhoneStateListener() {
				
				public void onCallStateChanged (int state, String incomingNumber) {
					tvIncomingNumber.setText(incomingNumber);
				}
				
				public void onServiceStateChanged (ServiceState serviceState) {
					switch (serviceState.getState()) {
						case 0:				
							tvOperatorState.setText("STATE_IN_SERVICE");
							break;
						case 1:				
							tvOperatorState.setText("STATE_OUT_OF_SERVICE");
							break;
						case 2:				
							tvOperatorState.setText("STATE_EMERGENCY_ONLY ");
							break;
						case 3:				
							tvOperatorState.setText("STATE_POWER_OFF ");
							break;
					};
					
					tvOperatorRoaming.setText(Boolean.toString(serviceState.getRoaming()));
					tvOperatorName.setText(serviceState.getOperatorAlphaLong());
					
				}
				
				public void onSignalStrengthsChanged (SignalStrength signalStrength) {
					tvCellSignalStrength.setText(Integer.toString(signalStrength.getGsmSignalStrength()));
				}
			};
			
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE);
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void getBatteryStatus() {
    	try {
    		final TextView tvBatteryLevel = (TextView) findViewById(R.id.battery_level);
    		final TextView tvBatteryPlugged = (TextView) findViewById(R.id.battery_plugged);
    		final TextView tvBatteryTemperature = (TextView) findViewById(R.id.battery_temperature);
			
			IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = registerReceiver(null, intentFilter);
						
			if (batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) == 0) {
				tvBatteryPlugged.setText(Boolean.toString(false));
			} else {
				tvBatteryPlugged.setText(Boolean.toString(true));
			}

			tvBatteryLevel.setText(Integer.toString(batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)));
			tvBatteryTemperature.setText(Integer.toString(batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
    
    public void getSimInformation() {
    	try {
    		final TextView tvSimCountry = (TextView) findViewById(R.id.sim_country);
    		final TextView tvSimOperator = (TextView) findViewById(R.id.sim_operator);
    		final TextView tvSimOperatorName = (TextView) findViewById(R.id.sim_operator_name);
    		final TextView tvSimSerialNumber = (TextView) findViewById(R.id.sim_serial_number);
    		final TextView tvSimState = (TextView) findViewById(R.id.sim_state);
    		final TextView tvSimSubscriberId = (TextView) findViewById(R.id.sim_subscriber_id);
			
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			
			tvSimCountry.setText(telephonyManager.getSimCountryIso());
			tvSimOperator.setText(telephonyManager.getSimOperator());
			tvSimOperatorName.setText(telephonyManager.getSimOperatorName());
			tvSimSerialNumber.setText(telephonyManager.getSimSerialNumber());
			tvSimSubscriberId.setText(telephonyManager.getSubscriberId());
			
			switch (telephonyManager.getSimState()) {
				case 0:	tvSimState.setText("SIM_STATE_UNKNOWN");
						break;
				case 1:	tvSimState.setText("SIM_STATE_ABSENT");
						break;
				case 2:	tvSimState.setText("SIM_STATE_PIN_REQUIRED");
						break;
				case 3:	tvSimState.setText("SIM_STATE_PUK_REQUIRED");
						break;
				case 4:	tvSimState.setText("SIM_STATE_NETWORK_LOCKED");
						break;
				case 5:	tvSimState.setText("SIM_STATE_READY");
						break;    
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
    
    public void getScreenBrightness() {
    	try {
			final TextView tvScreenBrightness = (TextView) findViewById(R.id.screen_brightness);
			final TextView tvScreenOn = (TextView) findViewById(R.id.screen_on);
			
			powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			
			tvScreenOn.setText(Boolean.toString(powerManager.isScreenOn()));			
			tvScreenBrightness.setText(Integer.toString(Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void writeData() {
    	try {
			String FILENAME = "data";
			String string = "hello world!";

			FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(string.getBytes());
			fos.close();

			Log.i("STORAGE", getFilesDir().getAbsolutePath());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
 