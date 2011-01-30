package controllers;

import models.Note;
import play.mvc.Mailer;

public class NotesMailer extends Mailer {

    public static void arrivalNotification(Note note) {
        setFrom("noreply@freezenote.com");
        setSubject("You have received a new note");
        addRecipient(note.receiver);
        send(note);
    }

}
