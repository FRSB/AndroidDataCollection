package de.unihalle.ebusiness.androiddatacollection;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private Button beginEndButton;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location location;
	public TextView latitude;
	public TextView longitude;
	public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        
        try {
			beginEndButton = ((Button) findViewById(R.id.beginEndButton));
			latitude = ((TextView) findViewById(R.id.latitude));
			longitude = ((TextView) findViewById(R.id.longitude));
			textView = ((TextView) findViewById(R.id.textView1));
			
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			location = locationManager.getLastKnownLocation("gps");
			
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
					textView.setText("GPS is deactivated");
					
				}
				
				@Override
				public void onLocationChanged(Location location) {
					
					latitude.setText(Double.toString(location.getLatitude()));
					longitude.setText(Double.toString(location.getLongitude()));
										
				}
			};
			
			Button.OnClickListener listener = new Button.OnClickListener() {

				@Override
				public void onClick(View arg0) {				
										
					if (beginEndButton.getText() == getString(R.string.button_begin)) {
						beginEndButton.setText(getString(R.string.button_end));
					} else {
						beginEndButton.setText(getString(R.string.button_begin));
					}
				}
				
			};

			((Button) findViewById(R.id.beginEndButton)).setOnClickListener(listener);
			locationManager.requestLocationUpdates("gps", 30000L, 10.0f, locationListener);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
