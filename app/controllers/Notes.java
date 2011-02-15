package controllers;

import java.util.Date;

import controllers.auth.Auth;

import auth.UserAuth;

import models.Note;
import models.User;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.StatusCode;

public class Notes extends Controller {

    public static void displayForm() {
	render();
    }

    @Before(unless = "create")
    static void checkAuthenticated() {
	if (!UserAuth.isUserLoggedIn()) {
	    Auth.login();
	}
    }

    public static void created() {
	render();
    }

    public static void create(String message, Date when, String receiver,
	    String friend) {
	if (!UserAuth.isUserLoggedIn()) {
	    forbidden();
	}

	validation.required(message);
	validation.required(when);
	validation.future(when);
	if (receiver != null && receiver.length() > 0 && friend == null) {
	    validation.email(receiver);
	}

	if (!validation.hasErrors()) {
	    User user = UserAuth.getUser();
	    Note note = new Note();
	    note.sendDate = when;
	    note.message = message;
	    note.sender = user;
	    if (receiver != null && receiver.length() > 0 && friend == null) {
		note.receiver = receiver;
	    } else if (friend != null) {
		note.friend = Integer.parseInt(friend);
	    } else {
		note.receiver = user.email;
	    }
	    note.save();
	    response.status = StatusCode.CREATED;
	    renderJSON(user);
	} else {
	    badRequest();
	}
    }

    public static void last() {
	if (Play.mode.isDev()) {
	    Note lastNote = Note.last();
	    render(lastNote);
	} else {
	    notFound();
	}
    }

    public static void sendLast() {
	if (Play.mode.isDev()) {
	    Note lastNote = Note.last();
	    render(lastNote);
	} else {
	    notFound();
	}
    }
}