package models;

import java.util.ArrayList;
import java.util.List;

import play.libs.WS;
import play.libs.WS.WSRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Location extends GeoNamesModel {
    public String name;
    public String timeZoneId;
    
    private Location(String name, String timeZoneId) {
        super();
        this.name = name;
        this.timeZoneId = timeZoneId;
    }

    public static List<Location> search(String pattern){
        JsonObject result = Query("searchJSON","q=%s&maxRows=10&featureCode=ppl&featureCode=pplc&featureCode=adm1&featureCode=pcli&isNameRequired=true&style=full", WS.encode(pattern));
        JsonArray geonames = result.get("geonames").getAsJsonArray();
        
        List<Location> locations = new ArrayList<Location>();
        
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
            
            locations.add(new Location(fullName, timeZone));    
        }
        return locations;
    }
}
