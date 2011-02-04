package controllers;

import models.Note;
import play.Play;
import play.mvc.Mailer;

public class NotesMailer extends Mailer {

    public static void arrivalNotification(Note note) {
        setFrom(Play.configuration.getProperty("mail.from"));
        setSubject("You have received a new note");
        addRecipient(note.receiver);
        send(note);
    }
}
