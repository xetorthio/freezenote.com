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
    public String id;
    
    public Location(String name, Double latitude, Double longitude) {
        super();
        this.name = name;
        this.id = new GeoLocation(latitude, longitude).toString();
    }

    public static List<Location> search(String pattern){
        JsonObject result = Query("searchJSON","q=%s&maxRows=10&featureCode=ppl&featureCode=pplc&featureCode=adm1&isNameRequired=true", WS.encode(pattern));
        JsonArray geonames = result.get("geonames").getAsJsonArray();
        
        List<Location> locations = new ArrayList<Location>();
        
        for(JsonElement element : geonames){
            JsonObject obj = element.getAsJsonObject();
            String name = String.format("%s, %s", obj.get("name").getAsString(), obj.get("countryName").getAsString());
            Double latitude = obj.get("lat").getAsDouble();
            Double longitud = obj.get("lng").getAsDouble();
            locations.add(new Location(name,latitude, longitud));    
        }
        return locations;
    }
}
