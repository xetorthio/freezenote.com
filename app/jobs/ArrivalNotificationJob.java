package jobs;

import java.util.List;

import models.Note;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.On;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;
import controllers.NotesMailer;

@On("cron.notification.arrival")
public class ArrivalNotificationJob extends Job {

    public void doJob() {
	Logger.info("Starting arrival notification job.");
	List<Note> notes = Note.pendingForNotification();
	Logger.info("There are " + notes.size() + " notifications to send.");
	for (Note note : notes) {
	    if (note.sendByEmail()) {
		NotesMailer.arrivalNotification(note);
		note.sent = true;
		note.save();
		Logger.info("Sent notification to " + note.receiver
			+ " of note #" + note.id);
	    } else if (note.sendToFacebookWall()) {
		if (Play.mode.isProd()) {
		    ActionDefinition action = Router
			    .reverse("Auth.signInWithFacebook");
		    action.absolute();
		    WSRequest request = WS
			    .url("https://graph.facebook.com/%s/feed?access_token=%s",
				    String.valueOf(note.friend),
				    WS.encode(note.sender.fbAccessToken));
		    request.setParameter(
			    "message",
			    "You have received note freezed for you on "
				    + note.created
				    + ". To see the note please follow the link.");
		    request.setParameter("link", action.toString());
		    request.setParameter("name", "See the note");
		    HttpResponse post = request.post();
		    if (post.getStatus() != 200) {
			Logger.warn("Couldn't send note " + note.id
				+ ". Facebook returned: " + post.getStatus()
				+ " - " + request.post().getString());
		    } else {
			note.sent = true;
			note.save();
		    }
		} else {
		    note.sent = true;
		    note.save();
		}
	    }
	}
    }

}
