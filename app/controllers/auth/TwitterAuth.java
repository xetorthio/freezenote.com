package controllers.auth;

import java.util.HashMap;
import java.util.Map;

import models.TwitterAccount;
import models.User;

import org.scribe.extractors.UrlParameterExtractor;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import auth.UserAuth;

import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Router;
import services.twitter.Twitter;
import controllers.Application;

public class TwitterAuth extends Controller {
    public static void signInWithTwitter() throws Exception {
	Token requestToken = Twitter.getService().getRequestToken();
	Cache.set(requestToken.getToken(), requestToken, "60min");
	
	String authorizationUrl = Twitter.getService()
		.getAuthorizationUrl(requestToken);
	redirect(authorizationUrl);
    }

    public static void signInWithTwitterReturn(String oauth_token,
	    String oauth_verifier) throws Exception {
	Verifier verifier = new Verifier(oauth_verifier);
	Token requestToken = (Token) Cache.get(oauth_token);
	Token accessToken = Twitter.getService().getAccessToken(requestToken,
		verifier);
	Map<String, String> params = UrlParameterExtractor.extract(accessToken.getRawResponse());

	User user = UserAuth.getOrCreateUser(null);
	user.twitter = new TwitterAccount();
	user.twitter.token = accessToken.getToken();
	user.twitter.secret = accessToken.getSecret();
	user.twitter.screenName = params.get("screen_name");
	user.twitter.userId = Long.parseLong(params.get("user_id"));
	
	user.twitter.save();
	user.save();

	Application.index();
    }
}
