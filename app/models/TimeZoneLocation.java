package models;

import java.util.ArrayList;
import java.util.List;

import play.libs.WS;
import play.libs.WS.WSRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.maxmind.geoip.Location;

public class TimeZoneLocation extends GeoNamesModel {
    public String name;
    public String timeZoneId;
    
    private TimeZoneLocation(String name, String timeZoneId) {
        super();
        this.name = name;
        this.timeZoneId = timeZoneId;
    }
    
    private static List<TimeZoneLocation> search(String pattern, Integer maxResults, String queryString){
    	JsonObject result = Query("searchJSON","q=%s&maxRows=%s&featureCode=ppl&featureCode=pplc&featureCode=adm1&featureCode=pcli&isNameRequired=true&style=full&"+queryString, WS.encode(pattern), Integer.toString(maxResults));
        JsonArray geonames = result.get("geonames").getAsJsonArray();
        
        List<TimeZoneLocation> locations = new ArrayList<TimeZoneLocation>();
        
        for(JsonElement element : geonames){
            JsonObject obj = element.getAsJsonObject();
            String name = obj.get("name").getAsString();
            String timeZone = obj.get("timezone").getAsJsonObject().get("timeZoneId").getAsString();
            String country = obj.get("countryName").getAsString();
            
            String fullName;
            
            if(country.equals(name))
            	fullName = country;
            else
            	fullName = String.format("%s, %s", name, country);
            
            locations.add(new TimeZoneLocation(fullName, timeZone));    
        }
        return locations;	
    }
    
    public static List<TimeZoneLocation> search(String pattern){
        return search(pattern, 10, "");
    }

	public static TimeZoneLocation from(Location location) {
		JsonObject result = Query("timezoneJSON","lat=%s&lng=%s", Float.toString(location.latitude), Float.toString(location.longitude));
		String countryCode = result.get("countryCode").getAsString();
		String timeZoneId = result.get("timezoneId").getAsString();
        
		String searchPattern = timeZoneId.replace("/", "-");
		List<TimeZoneLocation> locations = search(searchPattern, 1, "country="+countryCode);
		
		if(locations.size() == 0)
			return new TimeZoneLocation(location.countryName, timeZoneId);
		
		return locations.get(0); 
	}
	
}
