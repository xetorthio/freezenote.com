package controllers;

import java.util.Locale;

import models.Note;
import models.Receiver;
import play.Logger;
import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Mailer;
import play.mvc.Router;

import com.ocpsoft.pretty.time.PrettyTime;

public class NotesMailer extends Mailer {
    public static void arrivalNotification(Note note, Receiver receiver) {
	Logger.info("Sending e-mail notificaion of note " + note.id
		+ " to receiver " + receiver.id);
	Lang.set(note.sender.language);
	setFrom(Play.configuration.getProperty("mail.from"));
	setSubject(Messages.get("mail.arrival.subject"));
	setReplyTo(note.sender.email);
	addRecipient(receiver.email);
	String loginUrl = Play.configuration.getProperty("baseUrl");
	if (receiver.email.endsWith("@gmail.com")
		|| receiver.email.endsWith("@googlemail.com")
		|| receiver.email.endsWith("@mail.google.com")) {
	    loginUrl += Router.reverse("auth.GoogleAuth.signInWithGoogle").url;
	} else {
	    loginUrl += Router.reverse("auth.Auth.login").url;
	}
	String creation = new PrettyTime(new Locale(note.sender.language))
		.format(note.created);
	send(note, loginUrl, creation);
    }
}
