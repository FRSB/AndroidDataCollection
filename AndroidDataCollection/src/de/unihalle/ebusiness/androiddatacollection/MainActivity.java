package de.unihalle.ebusiness.androiddatacollection;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private Button beginEndButton;
	
	private TelephonyManager telephonyManager;
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	private SensorEventListener sensorEventListener;
	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private Sensor gyroscopeSensor;
	private Sensor magneticFieldSensor;
	private Sensor gravitySensor;
	private Sensor lightSensor;
	private Sensor proximitySensor;
	private Sensor linearAccelerationSensor;
	
	private TextView tvAccelerometer_x;
	private TextView tvAccelerometer_y;
	private TextView tvAccelerometer_z;
	
	private TextView tvCellId;
	private TextView tvCellLac;
	private TextView tvCellNeighbors;

	private TextView tvGpsAccuracy;
	private TextView tvGpsAltitude;
	private TextView tvGpsLatitude;
	private TextView tvGpsLongitude;
	private TextView tvGpsBearing;
	private TextView tvGpsSpeed;
	
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
						setUpSensorListener();
						getCellLocation();
						getGpsLocation();
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
    	
    	try {
    		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    		
			if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
				accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			}
			
			if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
				magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			}
			
			if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
				gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
			}
			
			if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
				gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			}
			
			if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
				lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			}
			
			if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
				linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			}
			
			if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
				proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public void setUpSensorListener() {
    	try {
    		
        	tvAccelerometer_x = (TextView) findViewById(R.id.accelerometer_x);
        	tvAccelerometer_y = (TextView) findViewById(R.id.accelerometer_y);
        	tvAccelerometer_z = (TextView) findViewById(R.id.accelerometer_z);
        	
			sensorEventListener = new SensorEventListener() {
				
				@Override
				public void onSensorChanged(SensorEvent event) {
					// get all sensor data
					if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
						tvAccelerometer_x.setText(Float.toString(event.values[0]));
						tvAccelerometer_y.setText(Float.toString(event.values[1]));
						tvAccelerometer_z.setText(Float.toString(event.values[2]));
					}
				}
				
				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
									
				}
			};
			
			sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
			
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
        	
        	        	
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			
			GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
			
			tvCellId.setText(Integer.toString(cellLocation.getCid()));
			tvCellLac.setText(Integer.toString(cellLocation.getLac()));
			
			List<NeighboringCellInfo> neighboringCellInfo = telephonyManager.getNeighboringCellInfo();
			
			String neighboringCellString = "CellID LAC RSSI | ";
			for (NeighboringCellInfo cell : neighboringCellInfo) {
				neighboringCellString = neighboringCellString + cell.getCid() + ", " + cell.getLac() + ", " + cell.getRssi() + " | ";
			}
			
			tvCellNeighbors.setText(neighboringCellString);
			
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
    
}
