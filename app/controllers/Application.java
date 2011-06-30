package controllers;

import java.io.File;

import models.User;
import play.i18n.Lang;
import play.mvc.Controller;
import auth.UserAuth;
import controllers.auth.Auth;

public class Application extends Controller {
    public static void index() {
	if (auth.UserAuth.isUserLoggedIn()) {
	    if (UserAuth.getUser() == null) {
		Auth.logout();
	    } else {
		Users.notes();
	    }
	}
	User user = UserAuth.getUser();
	render(user);
    }

    public static void robots() {
	File file = play.Play.getFile("public/robots.txt");
	response.cacheFor("24h");
	renderBinary(file);
    }

    public static void languageSwitch(String language) {
	Lang.change(language);
	User user = UserAuth.getUser();
	if (user != null) {
	    user.language = language;
	    user.save();
	}
	index();
    }

    public static void home() {
	render();
    }
}
