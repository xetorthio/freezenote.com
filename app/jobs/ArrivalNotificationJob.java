package jobs;

import java.util.List;

import models.Note;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import controllers.NotesMailer;

@On("cron.notification.arrival")
public class ArrivalNotificationJob extends Job {

    public void doJob() {
        Logger.info("Starting arrival notification job.");
        List<Note> notes = Note.pendingForNotification();
        Logger.info("There are " + notes.size() + " notifications to send.");
        for (Note note : notes) {
            NotesMailer.arrivalNotification(note);
            note.sent = true;
            note.save();
            Logger.info("Sent notification to " + note.receiver
                    + " of note #" + note.id);
        }
    }

}
