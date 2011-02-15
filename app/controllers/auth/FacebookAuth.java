package controllers.auth;

import java.net.URISyntaxException;

import models.FacebookAccount;
import models.User;
import play.Play;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Router;
import services.Facebook;
import auth.UserAuth;
import controllers.Application;

public class FacebookAuth extends Controller {

    public static void signInWithFacebook() {
        String action = Router.getFullUrl("auth.FacebookAuth.signInWithFacebookReturn");
        redirect("https://www.facebook.com/dialog/oauth?client_id="
                + Play.configuration.get("facebook.app_id") + "&redirect_uri="
                + WS.encode(action.toString())
                + "&scope=email,read_friendlists,publish_stream,offline_access");
    }

    public static void signInWithFacebookReturn(String code) throws URISyntaxException {
        String action = Router.getFullUrl("auth.FacebookAuth.signInWithFacebookReturn");
        String accessToken = Facebook.getAccessToken(code, action);
        if (accessToken != null) {
            FacebookAuth.doFacebookLogin(accessToken);
            Application.index();
        } else {
            Auth.login();
        }
    }

    public static void doFacebookLogin(String accessToken) {
        FacebookAccount fbuser = Facebook.getUser(accessToken);
        User user = UserAuth.doLogin(fbuser.email);
        if (user.facebook == null) {
            fbuser.save();
            user.facebook = fbuser;
        }
        user.save();
    }

}
