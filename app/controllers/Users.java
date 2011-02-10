package controllers;

import models.User;
import play.mvc.Controller;
import services.Facebook;

public class Users extends Controller {
    public static void getFacebookFriends(int userId) {
	User user = Auth.getUser();
	if (user.id == userId && user.hasFacebookAccess()) {
	    renderText(Facebook.getFriends(user.facebook.accessToken));
	    ok();
	} else {
	    forbidden();
	}
    }
}
