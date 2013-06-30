public void getLocation() {
   	try {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);			
					   				
		locationListener = new LocationListener() {					
			...
			
			@Override
			public void onLocationChanged(Location location) {
				collectedDataMap.put("gpsaccuracy", Double.toString(location.getAccuracy()));
				collectedDataMap.put("gpslatitude", Double.toString(location.getLatitude()));
				collectedDataMap.put("gpslongitude", Double.toString(location.getLongitude()));
				locationManager.removeUpdates(locationListener);
			}
		};
		
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				10000, 100, locationListener);
		}
...
}