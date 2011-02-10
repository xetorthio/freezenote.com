package controllers;

import java.net.URISyntaxException;

import models.FacebookAccount;
import models.User;
import play.Play;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Router;
import services.Facebook;

public class Auth extends Controller {
    public static void fakeLogin(String user) {
	if (Play.mode.isDev()) {
	    doLogin(user);
	    Notes.displayForm();
	} else {
	    notFound();
	}
    }

    public static void fakeFacebookLogin(String token) {
	if (Play.mode.isDev()) {
	    doFacebookLogin(token);
	    Notes.displayForm();
	} else {
	    notFound();
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
	String action = Router.getFullUrl("Auth.signInWithFacebookReturn");
	redirect("https://www.facebook.com/dialog/oauth?client_id="
		+ Play.configuration.get("facebook.app_id") + "&redirect_uri="
		+ WS.encode(action.toString())
		+ "&scope=email,read_friendlists,publish_stream,offline_access");
    }

    public static void signInWithFacebookReturn(String code)
	    throws URISyntaxException {
	String action = Router.getFullUrl("Auth.signInWithFacebookReturn");
	String accessToken = Facebook.getAccessToken(code, action);
	if (accessToken != null) {
	    doFacebookLogin(accessToken);
	    Application.index();
	} else {
	    Auth.login();
	}
    }

    private static void doFacebookLogin(String accessToken) {
	FacebookAccount fbuser = Facebook.getUser(accessToken);
	User user = doLogin(fbuser.email);
	if (user.facebook == null) {
	    fbuser.save();
	    user.facebook = fbuser;
	}
	user.save();
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