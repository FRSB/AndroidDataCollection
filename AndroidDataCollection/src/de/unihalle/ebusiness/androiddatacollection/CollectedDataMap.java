package de.unihalle.ebusiness.androiddatacollection;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

public class CollectedDataMap {

	private TreeMap<String, String> collectedSensorData;
	
	public CollectedDataMap() {
		collectedSensorData = new TreeMap<String, String>();
		
		collectedSensorData.put("time", "");
		collectedSensorData.put("light", "");
		collectedSensorData.put("proximity", "");
		collectedSensorData.put("accx", "");
		collectedSensorData.put("accy", "");
		collectedSensorData.put("accz", "");
		collectedSensorData.put("gyrox", "");
		collectedSensorData.put("gyroy", "");
		collectedSensorData.put("gyroz", "");
		collectedSensorData.put("magneticx", "");
		collectedSensorData.put("magneticy", "");
		collectedSensorData.put("magneticz", "");
		collectedSensorData.put("cellid", "");
		collectedSensorData.put("celllac", "");
		collectedSensorData.put("cellneighbors", "");
		collectedSensorData.put("gpsaccuracy", "");
		collectedSensorData.put("gpsaltitude", "");
		collectedSensorData.put("gpslatitude", "");
		collectedSensorData.put("gpslongitude", "");
		collectedSensorData.put("gpsbearing", "");
		collectedSensorData.put("gpsspeed", "");
		collectedSensorData.put("ringermode", "");
		collectedSensorData.put("airplanemode", "");
		collectedSensorData.put("bluetoothmode", "");
		collectedSensorData.put("deviceid", "");
		collectedSensorData.put("phonetype", "");
		collectedSensorData.put("wifissid", "");
		collectedSensorData.put("wifirssi", "");
		collectedSensorData.put("operatorstate", "");
		collectedSensorData.put("operatorroaming", "");
		collectedSensorData.put("operatorname", "");
		collectedSensorData.put("incomingnumber", "");
		collectedSensorData.put("cellsignalstrength", "");
		collectedSensorData.put("batterylevel", "");
		collectedSensorData.put("batteryplugged", "");
		collectedSensorData.put("batterytemperature", "");
		collectedSensorData.put("simcountry", "");
		collectedSensorData.put("simoperator", "");
		collectedSensorData.put("simoperatorname", "");
		collectedSensorData.put("simserialnumber", "");
		collectedSensorData.put("simstate", "");
		collectedSensorData.put("simsubscriberid", "");
		collectedSensorData.put("screenbrightness", "");
		collectedSensorData.put("screenon", "");
	}
	
	public void put(String key, String value) {
		collectedSensorData.put(key, value);
	}
	
	public String get(String key) {
		if (collectedSensorData.get(key) != null) {
			return collectedSensorData.get(key);
		} else {
			return "";
		}
	}
	
	public String getHeadline() {
		Set<String> set = collectedSensorData.keySet();
		String headline = "";
		
		for (String string : set) {
			headline += string + ";";
		}
		
		headline = headline.substring(0, headline.length() - 1);
		
		return headline;
	}
	
	public String getAllValues() {
		Collection<String> collection = collectedSensorData.values();
		String values = "";
		
		for (String string : collection) {
			values += string + ";";
		}
		
		values = values.substring(0, values.length() - 1);
		
		return values;
	}
	
	public String toString() {
		return collectedSensorData.toString();		
	}
}
