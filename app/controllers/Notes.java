package controllers;

import java.util.Date;
import java.util.Set;

import models.Note;
import models.User;

import org.joda.time.DateTimeZone;

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
	if (!Auth.isUserLoggedIn()) {
	    Auth.login();
	}
    }

    public static void created() {
	render();
    }

    public static void create(String message, Date when, String receiver,
	    String friend) {
	if (!Auth.isUserLoggedIn()) {
	    forbidden();
	}

	validation.required(message);
	validation.required(when);
	validation.future(when);
	if (receiver != null && receiver.length() > 0 && friend == null) {
	    validation.email(receiver);
	}

	if (!validation.hasErrors()) {
	    User user = Auth.getUser();
	    Note note = new Note();
	    note.sendDate = when;
	    note.message = message;
	    note.sender = user;
	    if (receiver != null && receiver.length() > 0 && friend == null) {
		note.receiver = receiver;
	    } else if (friend != null) {
		note.friend = Long.parseLong(friend);
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