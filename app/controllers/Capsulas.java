package controllers;

import java.util.Date;

import models.Capsula;
import models.User;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.StatusCode;

public class Capsulas extends Controller {

    public static void displayForm() {
	render();
    }

    @Before(unless = "create")
    static void checkAuthenticated() {
	if (!Auth.isUserLoggedIn()) {
	    Auth.login();
	}
    }

    public static void created() {
	render();
    }

    public static void create(String message, Date when, String receiver) {
	if (!Auth.isUserLoggedIn()) {
	    forbidden();
	}

	validation.required(message);
	validation.required(when);
	validation.future(when);
	if (receiver != null && receiver.length() > 0) {
	    validation.email(receiver);
	}

	if (!validation.hasErrors()) {
	    User user = Auth.getUser();
	    Capsula capsula = new Capsula();
	    capsula.sendDate = when;
	    capsula.message = message;
	    capsula.sender = user;
	    if (receiver != null && receiver.length() > 0) {
		capsula.receiver = receiver;
	    } else {
		capsula.receiver = user.email;
	    }
	    capsula.save();
	    response.status = StatusCode.CREATED;
	    renderJSON(user);
	} else {
	    badRequest();
	}
    }

    public static void last() {
	if (Play.mode.isDev()) {
	    Capsula lastCapsula = Capsula.last();
	    render(lastCapsula);
	}
    }
}