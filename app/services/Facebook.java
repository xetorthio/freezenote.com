package services;

import play.Logger;
import play.Play;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Facebook {
    public static FBUser getUser(String accessToken) {
	JsonElement json = WS
		.url("https://graph.facebook.com/me?access_token=%s",
			WS.encode(accessToken)).get().getJson();
	FBUser user = new FBUser();
	user.email = json.getAsJsonObject().get("email").getAsString();
	user.name = json.getAsJsonObject().get("name").getAsString();
	user.id = json.getAsJsonObject().get("id").getAsLong();
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

    public static class FBUser {
	public String email;
	public long id;
	public String accessToken;
	public String name;
    }

    public static FBUser createTestUser() {
	String applicationAccessToken = getApplicationAccessToken();
	if (applicationAccessToken != null) {
	    String service = "https://graph.facebook.com/%s/accounts/test-users?installed=true&permissions=email,read_friendlists,publish_stream,offline_access&access_token=%s";
	    HttpResponse httpResponse = WS
		    .url(service,
			    WS.encode(Play.configuration
				    .getProperty("facebook.app_id")),
			    WS.encode(applicationAccessToken)).post();
	    JsonObject json = httpResponse.getJson().getAsJsonObject();
	    String accessToken = json.get("access_token").getAsString();
	    return getUser(accessToken);
	}
	return null;
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

    public static void makeFriends(FBUser user, FBUser friend) {
	String service = "https://graph.facebook.com/%s/friends/%s?access_token=%s";
	WS.url(service, String.valueOf(user.id), String.valueOf(friend.id),
		user.accessToken).post();
	WS.url(service, String.valueOf(friend.id), String.valueOf(user.id),
		friend.accessToken).post();
    }

    public static String getFriends(String accessToken) {
	String service = "https://graph.facebook.com/me/friends?access_token=%s";
	HttpResponse httpResponse = WS.url(service, accessToken).get();
	return httpResponse.getJson().getAsJsonObject().get("data")
		.getAsJsonArray().toString();
    }
}
