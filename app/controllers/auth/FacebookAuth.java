package controllers.auth;

import java.net.URISyntaxException;

import models.User;
import play.Logger;
import play.Play;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;
import auth.UserAuth;

import com.google.gson.JsonElement;

import controllers.Application;

public class FacebookAuth extends Controller {

    public static void signInWithFacebook() {
        ActionDefinition action = Router.reverse("Auth.signInWithFacebookReturn");
        action.absolute();
        redirect("https://www.facebook.com/dialog/oauth?client_id="
                + Play.configuration.get("facebook.app_id") + "&redirect_uri="
                + WS.encode(action.toString())
                + "&scope=email,read_friendlists,publish_stream,offline_access");
    }

    public static void signInWithFacebookReturn(String code) throws URISyntaxException {
        ActionDefinition action = Router.reverse("Auth.signInWithFacebookReturn");
        action.absolute();
        String service = "https://graph.facebook.com/oauth/access_token?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s";
        HttpResponse httpResponse = WS.url(service,
                WS.encode(Play.configuration.get("facebook.app_id").toString()), action.toString(),
                WS.encode(Play.configuration.get("facebook.app_secret").toString()),
                WS.encode(code)).get();
        if (httpResponse.getStatus() == 200) {
            String response = httpResponse.getString();
            String accessToken = response.substring(13);
            JsonElement json = WS.url("https://graph.facebook.com/me?access_token=%s",
                    WS.encode(accessToken)).get().getJson();
            String email = json.getAsJsonObject().get("email").toString();
            int id = json.getAsJsonObject().get("id").getAsInt();
            User user = UserAuth.doLogin(email);
            user.fbAccessToken = accessToken;
            user.fbId = id;
            user.save();
            Application.index();
        } else {
            Logger.info("Facebook oauth error " + httpResponse.getStatus() + ": "
                    + httpResponse.getString());
            Auth.login();
        }
    }
}
