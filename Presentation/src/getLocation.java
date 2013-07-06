locationListener = new LocationListener() {
		...

		@Override
		public void onLocationChanged(Location location) {
			collectedDataMap.put("gpsaccuracy", Double.toString(location.getAccuracy()));
			collectedDataMap.put("gpslatitude", Double.toString(location.getLatitude()));
			collectedDataMap.put("gpslongitude", Double.toString(location.getLongitude()));
			...
		}
};

	
	
	