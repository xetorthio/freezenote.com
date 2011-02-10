package models;

import java.util.TimeZone;

import play.Logger;
import play.Play;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Http;

import com.google.gson.JsonObject;

public class GeoLocation extends GeoNamesModel {
    public Double latitude;
    public Double longitude;

    public GeoLocation(double latitude, double longitude) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static GeoLocation parse(String value) {
        String[] parts = value.split("\\|");
        return new GeoLocation(Double.parseDouble(parts[0]), Double
                .parseDouble(parts[1]));
    }

    public String toString() {
        return latitude + "|" + longitude;
    }

    public TimeZone getTimeZone() {
        JsonObject json = Query("timezoneJSON", "lat=%s&lng=%s", WS
                .encode(latitude.toString()), WS.encode(longitude.toString()));
        if (json == null)
            return null;
        return TimeZone.getTimeZone(json.get("timezoneId").getAsString());
    }

}
