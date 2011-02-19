package controllers;

import models.Note;
import play.Play;
import play.i18n.Lang;
import play.mvc.Mailer;
import play.mvc.Router;

public class NotesMailer extends Mailer {

    public static void arrivalNotification(Note note) {
	Lang.change(note.sender.language);
	setFrom(Play.configuration.getProperty("mail.from"));
	setSubject("mail.arrival.subject");
	setReplyTo(note.sender.email);
	addRecipient(note.receiver);
	String loginUrl = Play.configuration.getProperty("baseUrl");
	if (note.receiver.endsWith("@gmail.com")
		|| note.receiver.endsWith("@googlemail.com")
		|| note.receiver.endsWith("@mail.google.com")) {
	    loginUrl += Router.reverse("auth.GoogleAuth.signInWithGoogle").url;
	} else {
	    loginUrl += Router.reverse("auth.Auth.login").url;
	}
	send(note, loginUrl);
    }
}
