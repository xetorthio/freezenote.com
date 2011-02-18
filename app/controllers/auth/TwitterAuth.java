package controllers.auth;

import java.util.Map;

import models.TwitterAccount;
import models.User;
import play.Play;
import play.modules.oauthclient.OAuthClient;
import play.mvc.Controller;
import play.mvc.Router;
import auth.UserAuth;
import controllers.Application;

public class TwitterAuth extends Controller {
    private static OAuthClient connector = new OAuthClient(
	    "http://api.twitter.com/oauth/request_token",
	    "http://api.twitter.com/oauth/access_token",
	    "http://api.twitter.com/oauth/authorize",
	    (String) Play.configuration.get("twitter.consumerKey"),
	    (String) Play.configuration.get("twitter.consumerSecret"));

    public static void signInWithTwitter() throws Exception {
	String action = Router
		.getFullUrl("auth.TwitterAuth.signInWithTwitterReturn");

	// Note: This is empty, but even so the OAuth module requires it as a
	// parameter
	TwitterAccount ignored = new TwitterAccount();
	connector.authenticate(ignored, action);
    }

    public static void signInWithTwitterReturn(String oauth_token,
	    String oauth_verifier) throws Exception {

	TwitterAccount account = new TwitterAccount();
	connector.retrieveAccessToken(account, oauth_verifier);
	Map<String, String> response = connector.getProvider()
		.getResponseParameters();
	account.userId = Long.parseLong(response.get("user_id"));
	account.screenName = response.get("screen_name");

	// TODO: Will a null email address on the user object cause problems in
	// other parts of the site?
	User user = UserAuth.getOrCreateUser(null);
	user.twitter = account;
	user.twitter.save();
	user.save();

	Application.index();
    }
}
