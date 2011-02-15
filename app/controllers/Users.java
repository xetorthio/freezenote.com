package controllers;

import models.User;
import play.mvc.Controller;
import services.Facebook;
import auth.UserAuth;

public class Users extends Controller {

    public static void getFacebookFriends(int userId) {
        User user = UserAuth.getUser();
        if (user.id == userId && user.hasFacebookAccess()) {
            renderText(Facebook.getFriends(user.facebook.accessToken));
            ok();
        } else {
            forbidden();
        }
    }

}
