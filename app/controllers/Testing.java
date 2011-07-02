package controllers;

import java.util.Date;

import models.Note;
import models.Receiver;
import models.User;
import play.Logger;
import play.Play;
import play.mvc.Controller;

public class Testing extends Controller {
    public static void sendMail() {
	Logger.info("Sending test e-mail...");
	User u = new User();
	u.email = "ionathan@gmail.com";
	u.language = "es";

	Note n = new Note();
	n.created = new Date();
	n.id = 123l;
	n.message = "A test note";
	n.sendDate = new Date();
	n.sender = u;

	Receiver r = new Receiver(n, "ionathan@gmail.com");
	n.receivers.add(r);

	NotesMailer.arrivalNotification(n, r);
    }

    public static void viewMail() {
	if (Play.mode.isDev()) {
	    User u = new User();
	    u.email = "ionathan@gmail.com";
	    u.language = "es";

	    Note note = new Note();
	    note.created = new Date();
	    note.id = 123l;
	    note.message = "A test note";
	    note.sendDate = new Date();
	    note.sender = u;

	    Receiver r = new Receiver(note, "ionathan@gmail.com");
	    note.receivers.add(r);
	    renderTemplate("NotesMailer/arrivalNotification.html", note);
	} else {
	    notFound();
	}
    }

}
