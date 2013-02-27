package de.unihalle.ebusiness.androiddatacollection;

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
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.app.Activity;
import android.content.Context;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private Button beginEndButton;
	
	private TelephonyManager telephonyManager;
	private SignalStrength signalStrength;
	
	private ServiceState serviceState;
	
	private AudioManager audioManager;
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	private SensorEventListener sensorEventListener;
	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private Sensor gyroscopeSensor;
	private Sensor magneticFieldSensor;
	private Sensor lightSensor;
	private Sensor proximitySensor;
	
	private WifiInfo wifiInfo;
	
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
	private TextView tvCellSignalStrength;

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
	private TextView tvOperatorId;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        
        try {     	
			
			beginEndButton = ((Button) findViewById(R.id.beginEndButton));
				
			Button.OnClickListener buttonListener = new Button.OnClickListener() {

				@Override
				public void onClick(View arg0) {				
										
					if (beginEndButton.getText() == getString(R.string.button_begin)) {
						
						beginEndButton.setText(getString(R.string.button_end));
			        	setUpSensors();						
						getCellLocation();
						getGpsLocation();
						getRinger();
						getAirplaneMode();
						getBluetoothMode();
						getDeviceInformation();
						getWifiInformation();
						getOperatorInformation();
						
					} else {
						
						beginEndButton.setText(getString(R.string.button_begin));
						sensorManager.unregisterListener(sensorEventListener);
						locationManager.removeUpdates(locationListener);
						
					}
				}
				
			};

			((Button) findViewById(R.id.beginEndButton)).setOnClickListener(buttonListener);
			

			
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
    
    public void getCellLocation() {
    	try {
    		    		
        	tvCellId = (TextView) findViewById(R.id.cellId);
        	tvCellLac = (TextView) findViewById(R.id.cellLac);
        	tvCellNeighbors = (TextView) findViewById(R.id.cellNeighbors);
        	tvCellSignalStrength = (TextView) findViewById(R.id.cellsignalstrength);
        	        	
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			
//			tvCellSignalStrength.setText(Integer.toString(signalStrength.getGsmSignalStrength()));
			
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
			
			tvGpsAccuracy = (TextView) findViewById(R.id.gps_accuracy);
			tvGpsAltitude = (TextView) findViewById(R.id.gps_altitude);
			tvGpsLatitude = (TextView) findViewById(R.id.gps_latitude);
			tvGpsLongitude = (TextView) findViewById(R.id.gps_longiitude);
			tvGpsBearing = (TextView) findViewById(R.id.gps_bearing);
			tvGpsSpeed = (TextView) findViewById(R.id.gps_speed);
			
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
    		tvRingerMode = (TextView) findViewById(R.id.ringer_mode);
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
    		tvAirplaneMode = (TextView) findViewById(R.id.airplane_mode);
    		
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
    		tvBluetoothMode = (TextView) findViewById(R.id.bluetooth_mode);
    		
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
			tvDeviceId = (TextView) findViewById(R.id.deviceid);
			tvPhoneType = (TextView) findViewById(R.id.phone_type);
			
			tvDeviceId.setText(telephonyManager.getDeviceId());
			
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
			tvWifiSsid = (TextView) findViewById(R.id.wifi_ssid);
			tvWifiRssi = (TextView) findViewById(R.id.wifi_rssi);
			
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
			tvOperatorState = (TextView) findViewById(R.id.operator_state);
			tvOperatorRoaming = (TextView) findViewById(R.id.operator_roaming);
			tvOperatorName = (TextView) findViewById(R.id.operator_name);
			tvOperatorState = (TextView) findViewById(R.id.operator_id);
			
			serviceState = new ServiceState();
				
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
			}
			
			tvOperatorRoaming.setText(Boolean.toString(serviceState.getRoaming()));
			tvOperatorName.setText(serviceState.getOperatorAlphaLong());
			tvOperatorId.setText(serviceState.getOperatorNumeric());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
 