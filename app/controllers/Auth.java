package controllers;

import java.net.URISyntaxException;

import models.User;
import play.Logger;
import play.Play;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;

import com.google.gson.JsonElement;

public class Auth extends Controller {
    public static void fakeLogin(String user) {
	if (Play.mode.isDev()) {
	    doLogin(user);
	    Notes.displayForm();
	}
    }

    public static void login() {
	render();
    }

    public static void logout() {
	doLogout();
	Application.index();
    }

    private static User doLogin(String email) {
	User user = User.login(email);
	if (user == null) {
	    user = new User();
	    user.email = email;
	    user.save();
	}

	session.put("user", user.id);
	return user;
    }

    private static void doLogout() {
	session.clear();
    }

    public static void signInWithGoogle() {
	if (OpenID.isAuthenticationResponse()) {
	    UserInfo verifiedUser = OpenID.getVerifiedID();
	    if (verifiedUser == null) {
		flash.put("error", "Oops. Authentication has failed");
		login();
	    }
	    doLogin(verifiedUser.extensions.get("email"));
	    Application.index();
	} else {
	    OpenID.id("https://www.google.com/accounts/o8/id")
		    .required("email", "http://axschema.org/contact/email")
		    .verify();
	}
    }

    public static void signInWithFacebook() {
	ActionDefinition action = Router
		.reverse("Auth.signInWithFacebookReturn");
	action.absolute();
	redirect("https://www.facebook.com/dialog/oauth?client_id="
		+ Play.configuration.get("facebook.app_id") + "&redirect_uri="
		+ WS.encode(action.toString())
		+ "&scope=email,read_friendlists,publish_stream,offline_access");
    }

    public static void signInWithFacebookReturn(String code)
	    throws URISyntaxException {
	ActionDefinition action = Router
		.reverse("Auth.signInWithFacebookReturn");
	action.absolute();
	String service = "https://graph.facebook.com/oauth/access_token?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s";
	HttpResponse httpResponse = WS
		.url(service,
			WS.encode(Play.configuration.get("facebook.app_id")
				.toString()),
			action.toString(),
			WS.encode(Play.configuration.get("facebook.app_secret")
				.toString()), WS.encode(code)).get();
	if (httpResponse.getStatus() == 200) {
	    String response = httpResponse.getString();
	    String accessToken = response.substring(13);
	    JsonElement json = WS
		    .url("https://graph.facebook.com/me?access_token=%s",
			    WS.encode(accessToken)).get().getJson();
	    String email = json.getAsJsonObject().get("email").toString();
	    int id = json.getAsJsonObject().get("id").getAsInt();
	    User user = doLogin(email);
	    user.fbAccessToken = accessToken;
	    user.fbId = id;
	    user.save();
	    Application.index();
	} else {
	    Logger.info("Facebook oauth error " + httpResponse.getStatus()
		    + ": " + httpResponse.getString());
	    Auth.login();
	}
    }

    static boolean isUserLoggedIn() {
	if (!session.contains("user")) {
	    return false;
	}
	User user = getUser();
	if (user == null) {
	    session.clear();
	    return false;
	}
	return true;
    }

    static User getUser() {
	User user = User.findById(Long.parseLong(session.get("user")));
	return user;
    }
}