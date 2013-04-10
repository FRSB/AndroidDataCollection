package de.unihalle.ebusiness.androiddatacollection;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.NeighboringCellInfo;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

	public class SensorAccess {
		
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
		
		private DataWriter dataWriter;
		
		private CollectedDataMap collectedDataMap;

	    private Context context;
	    
	    private Boolean writeToFile;
	    
	    public SensorAccess(Context context, Boolean writeToFile) {
	    	this.context = context;
	    	collectedDataMap = new CollectedDataMap();
	    	
	    	this.writeToFile = writeToFile;
	    	
	    	if (writeToFile) {
		    	String headline = collectedDataMap.getHeadline();	
		    	dataWriter = new DataWriter(headline);
	    	}
	    }
	    
	    public void startSensors() {
	    	collectedDataMap.put("time", getTimestamp());    	
	    	setUpListenerSensors();						
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
	    }
	    
	    public void stopSensors() {
			sensorManager.unregisterListener(sensorEventListener);
			// gps stays always on, check battery consumption
//			locationManager.removeUpdates(locationListener);
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
			
			if (writeToFile) {
				dataWriter.writeToFile(collectedDataMap.getAllValues());
			}
		}
	    	    
	    public CollectedDataMap getUIData() {
	    	return collectedDataMap;
	    }
	    
	    public void setUpListenerSensors() {
	    	// the accelerometer is the result of gravity and linear acceleration, so they have been omitted
	    	try {
	    		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	    		
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
				 
				sensorEventListener = new SensorEventListener() {
					
					@Override
					public void onSensorChanged(SensorEvent event) {
						// get all sensor data
						if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
							collectedDataMap.put("accx", Float.toString(event.values[0]));
							collectedDataMap.put("accy", Float.toString(event.values[1]));
							collectedDataMap.put("accz", Float.toString(event.values[2]));
						}
						
						if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
							collectedDataMap.put("gyrox", Float.toString(event.values[0]));
							collectedDataMap.put("gyroy", Float.toString(event.values[1]));
							collectedDataMap.put("gyroz", Float.toString(event.values[2]));
						}
						
						if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
							collectedDataMap.put("magneticx", Float.toString(event.values[0]));
							collectedDataMap.put("magneticy", Float.toString(event.values[1]));
							collectedDataMap.put("magneticz", Float.toString(event.values[2]));
						}
										
						if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
							collectedDataMap.put("light", Float.toString(event.values[0]));
							
						}
						
						if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
							collectedDataMap.put("proximity", Float.toString(event.values[0]));
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
				telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				int networkType = telephonyManager.getNetworkType();
				
				switch (networkType) {
					case TelephonyManager.NETWORK_TYPE_EDGE: {
						collectedDataMap.put("networktype", "NETWORK_TYPE_EDGE");
						break;
					}
					case TelephonyManager.NETWORK_TYPE_GPRS: {
						collectedDataMap.put("networktype", "NETWORK_TYPE_GPRS");
						break;
					}
					case TelephonyManager.NETWORK_TYPE_HSPA: {
						collectedDataMap.put("networktype", "NETWORK_TYPE_HSPA");
						break;
					}
					case TelephonyManager.NETWORK_TYPE_HSDPA: {
						collectedDataMap.put("networktype", "NETWORK_TYPE_HSDPA");
						break;
					}
					case TelephonyManager.NETWORK_TYPE_HSUPA: {
						collectedDataMap.put("networktype", "NETWORK_TYPE_HSUPA");
						break;
					}
					case TelephonyManager.NETWORK_TYPE_HSPAP: {
						collectedDataMap.put("networktype", "NETWORK_TYPE_HSPAP");
						break;
					}
					case TelephonyManager.NETWORK_TYPE_UMTS: {
						collectedDataMap.put("networktype", "NETWORK_TYPE_UMTS");
						break;
					}
					case TelephonyManager.NETWORK_TYPE_LTE: {
						collectedDataMap.put("networktype", "NETWORK_TYPE_LTE");
						break;
					}
					case TelephonyManager.NETWORK_TYPE_UNKNOWN: {
						collectedDataMap.put("networktype", "NETWORK_TYPE_UNKKNOWN");
						break;
					}
				};
				
				GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
				
				if (cellLocation != null) {
					collectedDataMap.put("cellid", Integer.toString(cellLocation.getCid()));
					collectedDataMap.put("celllac", Integer.toString(cellLocation.getLac()));
									
					List<NeighboringCellInfo> neighboringCellInfo = telephonyManager.getNeighboringCellInfo();
					
					String neighboringCellString = "CellID LAC RSSI | ";
					for (NeighboringCellInfo cell : neighboringCellInfo) {
						neighboringCellString = neighboringCellString + cell.getCid() + ", " + cell.getLac() + ", " + cell.getRssi() + " | ";
					}
					
					collectedDataMap.put("cellneighbors", neighboringCellString);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	    }
	    
	    public void getGpsLocation() {
	    	try {
				locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);			
							   	
				Location location = locationManager.getLastKnownLocation("gps");
				
				if (location != null) {
					collectedDataMap.put("gpsaccuracy", Double.toString(location.getAccuracy()));
					collectedDataMap.put("gpsaltitude", Double.toString(location.getAltitude()));
					collectedDataMap.put("gpslatitude", Double.toString(location.getLatitude()));
					collectedDataMap.put("gpslongitude", Double.toString(location.getLongitude()));
					collectedDataMap.put("gpsbearing", Double.toString(location.getBearing()));
					collectedDataMap.put("gpsspeed", Double.toString(location.getSpeed()));
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
						collectedDataMap.put("gpsaccuracy", Double.toString(location.getAccuracy()));
						collectedDataMap.put("gpsaltitude", Double.toString(location.getAltitude()));
						collectedDataMap.put("gpslatitude", Double.toString(location.getLatitude()));
						collectedDataMap.put("gpslongitude", Double.toString(location.getLongitude()));
						collectedDataMap.put("gpsbearing", Double.toString(location.getBearing()));
						collectedDataMap.put("gpsspeed", Double.toString(location.getSpeed()));			
					}
				};
				
				locationManager.requestLocationUpdates("gps", 10000, 1, locationListener);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public void getRinger() {
	    	
	    	try {
				audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				
				switch (audioManager.getRingerMode()) {
					case AudioManager.RINGER_MODE_NORMAL:
						collectedDataMap.put("ringermode", "RINGER_MODE_NORMAL");
						break;
					case AudioManager.RINGER_MODE_VIBRATE:
						collectedDataMap.put("ringermode", "RINGER_MODE_VIBRATE");
						break;
					case AudioManager.RINGER_MODE_SILENT:
						collectedDataMap.put("ringermode", "RINGER_MODE_SILENT");
						break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public void getAirplaneMode() {    	
	    	try {
	
				if (Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON) == 1) {
					collectedDataMap.put("airplanemode", "AIRPLANE_MODE_ON");
				} else {
					collectedDataMap.put("airplanemode", "AIRPLANE_MODE_OFF");
				}
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public void getBluetoothMode() {    	
	    	try {
				if (Settings.System.getInt(context.getContentResolver(), Settings.Global.BLUETOOTH_ON) == 1) {
					collectedDataMap.put("bluetoothmode", "BLUETOOTH_MODE_ON");
				} else {
					collectedDataMap.put("bluetoothmode", "BLUETOOTH_MODE_OFF");
				}
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public void getDeviceInformation() {
	    	try {
				collectedDataMap.put("deviceid", telephonyManager.getDeviceId());
				
				telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				
				if (telephonyManager.getPhoneType() == 1) {
					collectedDataMap.put("phonetype", "Phone_Type_GSM");
				} else {
					    	if (telephonyManager.getPhoneType() == 2) {
					    		collectedDataMap.put("phonetype", "Phone_Type_CDMA");
					    	} else {
					    		collectedDataMap.put("phonetype", "Phone_Type_NONE");
					    		}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public void getWifiInformation() {
	    	try {
				wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				wifiInfo = wifiManager.getConnectionInfo();
				
				if (wifiInfo.getSSID() != null) {
					// null if not connected
					collectedDataMap.put("wifissid", wifiInfo.getSSID());
					collectedDataMap.put("wifirssi", Integer.toString(wifiInfo.getRssi()));
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    public void getOperatorInformation() {
			try {
				telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				
				phoneStateListener = new PhoneStateListener() {
					
					public void onCallStateChanged (int state, String incomingNumber) {
						collectedDataMap.put("incomingnumber", incomingNumber);
					}
					
					public void onServiceStateChanged (ServiceState serviceState) {
						switch (serviceState.getState()) {
							case 0:
								collectedDataMap.put("operatorstate", "STATE_IN_SERVICE");
								break;
							case 1:				
								collectedDataMap.put("operatorstate", "STATE_OUT_OF_SERVICE");
								break;
							case 2:				
								collectedDataMap.put("operatorstate", "STATE_EMERGENCY_ONLY");
								break;
							case 3:				
								collectedDataMap.put("operatorstate", "STATE_POWER_OFF");
								break;
						};
						
						collectedDataMap.put("operatorroaming", Boolean.toString(serviceState.getRoaming()));
						collectedDataMap.put("operatorname", serviceState.getOperatorAlphaLong());
					}
					
					public void onSignalStrengthsChanged (SignalStrength signalStrength) {
						collectedDataMap.put("cellsignalstrength", "GSM: " + Integer.toString(signalStrength.getGsmSignalStrength()) + " UMTS: " + Integer.toString(signalStrength.getCdmaDbm()));
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
				IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
				Intent batteryStatus = context.registerReceiver(null, intentFilter);
							
				if (batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) == 0) {
					collectedDataMap.put("batteryplugged", Boolean.toString(false));

				} else {
					collectedDataMap.put("batteryplugged", Boolean.toString(true));
				}

				collectedDataMap.put("batterylevel", Integer.toString(batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)));
				collectedDataMap.put("batterytemperature", Integer.toString(batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    	
	    }
	    
	    public void getSimInformation() {
	    	try {
				telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

				collectedDataMap.put("simcountry", telephonyManager.getSimCountryIso());
				collectedDataMap.put("simoperator", telephonyManager.getSimOperator());
				collectedDataMap.put("simoperatorname", telephonyManager.getSimOperatorName());
				collectedDataMap.put("simserialnumber", telephonyManager.getSimSerialNumber());
				collectedDataMap.put("simsubscriberid", telephonyManager.getSubscriberId());

				
				switch (telephonyManager.getSimState()) {
					case 0:	collectedDataMap.put("simstate", "SIM_STATE_UNKNOWN");
							break;
					case 1:	collectedDataMap.put("simstate", "SIM_STATE_ABSENT");
							break;
					case 2:	collectedDataMap.put("simstate", "SIM_STATE_PIN_REQUIRED");
							break;
					case 3:	collectedDataMap.put("simstate", "SIM_STATE_PUK_REQUIRED");
							break;
					case 4:	collectedDataMap.put("simstate", "SIM_STATE_NETWORK_LOCKED");
							break;
					case 5:	collectedDataMap.put("simstate", "SIM_STATE_READY");
							break;    
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    	
	    }
	    
	    public void getScreenBrightness() {
	    	try {
				powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				
				collectedDataMap.put("screenbrightness", Integer.toString(Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)));
				collectedDataMap.put("screenon", Boolean.toString(powerManager.isScreenOn()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	 
	    public String getTimestamp() {
	    	
	    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	    	Calendar cal = Calendar.getInstance();
	    	
			return simpleDateFormat.format(cal.getTime()).toString();
	    	
	    }
}
