package controllers;

import java.util.Locale;

import models.Note;
import models.Receiver;
import play.Logger;
import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Mailer;

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

	String creation = new PrettyTime(new Locale(note.sender.language))
		.format(note.created);
	send(note, creation);
    }
}
