package services;

import java.util.Locale;

import models.FacebookAccount;
import models.Note;
import models.Receiver;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.i18n.Messages;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Http;
import play.mvc.Router;

import com.google.gson.JsonElement;
import com.ocpsoft.pretty.time.PrettyTime;

public class Facebook {
    public static String lastPost;

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

    public static String postToWall(Note note, Receiver receiver) {
	String action = Play.configuration.getProperty("baseUrl")
		+ Router.reverse("auth.FacebookAuth.signInWithFacebook").url;
	WSRequest request = WS.url(
		"https://graph.facebook.com/%s/feed?access_token=%s",
		String.valueOf(receiver.friend),
		WS.encode(note.sender.facebook.accessToken));
	String text;
	if (note.shared) {
	    text = StringUtils.capitalize(Messages.getMessage(
		    note.sender.language, "facebook.arrival.intro.public",
		    new PrettyTime(new Locale(note.sender.language))
			    .format(note.created), note.message));
	} else {
	    text = StringUtils.capitalize(Messages.getMessage(
		    note.sender.language, "facebook.arrival.intro.private",
		    new PrettyTime(new Locale(note.sender.language))
			    .format(note.created)));
	}
	request.setParameter("message", text);

	request.setParameter("link", action);
	request.setParameter("name", Messages.getMessage(note.sender.language,
		"facebook.arrival.seeNote"));
	// FB is so crappy that it has limits on test users. fuck them for
	// forcing me to put here this hack
	if (Play.mode.isDev()) {
	    lastPost = request.parameters.toString();
	    return "123456";
	}
	HttpResponse post = request.post();
	if (post.getStatus() != 200) {
	    lastPost = null;

	    Logger.error("Couldn't send note " + note.id + " to receiver #"
		    + receiver.id + ". Facebook returned: " + post.getStatus()
		    + " - " + request.post().getString());
	    return null;
	} else {
	    lastPost = request.parameters.toString();

	    String postId = post.getJson().getAsJsonObject().get("id")
		    .getAsString();
	    return postId;
	}
    }
}
