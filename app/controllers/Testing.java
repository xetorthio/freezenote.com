package controllers;

import java.util.Date;

import javax.xml.ws.Service.Mode;

import models.Note;
import models.Receiver;
import models.User;
import play.Logger;
import play.Play;
import play.mvc.Controller;

public class Testing extends Controller {
    public static void sendMail() {
	if (Play.mode.isDev() || Play.mode.equals(Mode.valueOf("staging"))) {
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
    }
}
