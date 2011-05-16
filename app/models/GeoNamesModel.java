package models;

import play.Logger;
import play.Play;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Http;

import com.google.gson.JsonObject;

public class GeoNamesModel {

    protected static JsonObject Query(String serviceUrl, String queryString,
            String... params) {
        String service = String.format(
                "http://api.geonames.org/%s?username=%s&%s",
                        serviceUrl, WS
                        .encode(Play.configuration
                                .getProperty("geonames.username")), queryString);
       
        HttpResponse httpResponse = WS.url(service, params).get();

        if (httpResponse.getStatus().equals(Http.StatusCode.OK)) {
            return httpResponse.getJson().getAsJsonObject();
        } else {
            Logger.info("GeoNames error " + httpResponse.getStatus() + ": "
                    + httpResponse.getString());
            return null;
        }
    }

}
