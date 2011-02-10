package services;

import models.FacebookAccount;
import play.Logger;
import play.Play;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Http;

import com.google.gson.JsonElement;

public class Facebook {
    public static FacebookAccount getUser(String accessToken) {
	JsonElement json = WS
		.url("https://graph.facebook.com/me?access_token=%s",
			WS.encode(accessToken)).get().getJson();
	FacebookAccount user = new FacebookAccount();
	user.email = json.getAsJsonObject().get("email").getAsString();
	user.name = json.getAsJsonObject().get("name").getAsString();
	user.userId = json.getAsJsonObject().get("id").getAsLong();
	user.accessToken = accessToken;
	return user;
    }

    public static String getAccessToken(String code, String returnTo) {
	String service = "https://graph.facebook.com/oauth/access_token?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s";
	HttpResponse httpResponse = WS
		.url(service,
			WS.encode(Play.configuration
				.getProperty("facebook.app_id")),
			returnTo,
			WS.encode(Play.configuration
				.getProperty("facebook.app_secret")),
			WS.encode(code)).get();
	return parseToken(httpResponse);
    }

    private static String parseToken(HttpResponse httpResponse) {
	if (httpResponse.getStatus().equals(Http.StatusCode.OK)) {
	    String response = httpResponse.getString();
	    String accessToken = response.substring(13);
	    return accessToken;
	} else {
	    Logger.info("Facebook oauth error " + httpResponse.getStatus()
		    + ": " + httpResponse.getString());
	    return null;
	}
    }

    public static String getApplicationAccessToken() {
	String service = "https://graph.facebook.com/oauth/access_token?client_id=%s&client_secret=%s&grant_type=client_credentials";
	HttpResponse httpResponse = WS
		.url(service,
			WS.encode(Play.configuration
				.getProperty("facebook.app_id")),
			WS.encode(Play.configuration
				.getProperty("facebook.app_secret"))).get();
	return parseToken(httpResponse);
    }

    public static String getFriends(String accessToken) {
	String service = "https://graph.facebook.com/me/friends?access_token=%s";
	HttpResponse httpResponse = WS.url(service, accessToken).get();
	return httpResponse.getJson().getAsJsonObject().get("data")
		.getAsJsonArray().toString();
    }
}
