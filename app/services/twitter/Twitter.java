package services.twitter;

import models.TwitterAccount;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import play.Play;
import play.exceptions.UnexpectedException;
import play.mvc.Http;
import play.mvc.Router;

import com.google.gson.Gson;

public class Twitter {
    private static OAuthService service = null;

    public static OAuthService getService() {
	if (service == null) {
	    String callback = getCallback();

	    service = new ServiceBuilder().provider(TwitterApi.class).callback(
		    callback).apiKey(
		    (String) Play.configuration.get("twitter.consumerKey"))
		    .apiSecret(
			    (String) Play.configuration
				    .get("twitter.consumerSecret")).build();
	}

	return service;
    }

    private static String getCallback() {
	String action = "auth.TwitterAuth.signInWithTwitterReturn";
	if (Play.id.equals("test")) {
	    return "http://localhost:" + Play.configuration.get("http.port")
		    + Router.reverse(action).toString();
	}

	return Router.getFullUrl(action);
    }

    public static void postMessage(TwitterAccount account, String message) {
	try {
	    String url = "http://api.twitter.com/1/statuses/update.json";
	    OAuthRequest request = new OAuthRequest(Verb.POST, url);
	    request.addQuerystringParameter("status", message);
	    sendRequest(account, request);

	} catch (Exception e) {
	    throw new UnexpectedException(e);
	}
    }

    public static TwitterMessage getLastMessage(TwitterAccount account) {
	try {
	    String url = "http://api.twitter.com/1/statuses/user_timeline.json";
	    OAuthRequest request = new OAuthRequest(Verb.GET, url);
	    request.addQuerystringParameter("count", "1");
	    request.addQuerystringParameter("trim_user", "true");
	    Response response = sendRequest(account, request);
	    TwitterMessage[] message = new Gson().fromJson(response.getBody(),
		    TwitterMessage[].class);

	    if (message.length == 0) {
		return null;
	    }

	    return message[0];

	} catch (Exception e) {
	    throw new UnexpectedException(e);
	}
    }

    public static void verifyCredentials(TwitterAccount account) {
	String url = "http://api.twitter.com/1/account/verify_credentials.json";
	OAuthRequest request = new OAuthRequest(Verb.GET, url);
	sendRequest(account, request);
    }

    private static Response sendRequest(TwitterAccount account,
	    OAuthRequest request) {
	Token accessToken = new Token(account.token, account.secret);
	getService().signRequest(accessToken, request);
	Response response = request.send();
	if (response.getCode() != Http.StatusCode.OK) {
	    throw new TwitterException(response.getCode(), response.getBody());
	}

	return response;
    }
}
