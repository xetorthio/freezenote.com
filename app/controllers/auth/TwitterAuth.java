package controllers.auth;

import java.util.HashMap;
import java.util.Map;

import controllers.Application;

import play.modules.oauthclient.OAuthClient;
import play.mvc.Controller;
import play.mvc.Router;
import auth.UserAuth;

public class TwitterAuth extends Controller {
    private static OAuthClient connector = null;

    public static OAuthClient getConnector() {
        if (TwitterAuth.connector == null) {
            TwitterAuth.connector = new OAuthClient("http://api.twitter.com/oauth/request_token",
                    "http://api.twitter.com/oauth/access_token", "http://api.twitter.com/oauth/authorize",
                    "Qzma4C9KbKK2iPUsrh7QQ", "6MOb4LZ5QQbJVpFP3VvT7uDFEZvNFGAlFWXdlKCoIQ");
        }
        return TwitterAuth.connector;
    }

    public static void signInWithTwitter() throws Exception {
        String callbackURL = Router.getFullUrl(request.controller + ".signInWithTwitterReturn");
        getConnector().authenticate(UserAuth.getUser().twitterCreds, callbackURL);
    }

    public static void signInWithTwitterReturn(String oauth_token,
            String oauth_verifier) throws Exception {
        getConnector().retrieveAccessToken(UserAuth.getUser().twitterCreds, oauth_verifier);
        //UserAuth.doLogin(verifiedUser.extensions.get("email"));
        Application.index();
    }

}
