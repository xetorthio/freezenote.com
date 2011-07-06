package jobs;

import java.util.List;

import models.Note;
import models.Receiver;
import play.Logger;
import play.Play;
import play.i18n.Messages;
import play.jobs.Job;
import play.jobs.On;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Router;
import controllers.NotesMailer;

@On("cron.notification.arrival")
public class ArrivalNotificationJob extends Job {

    public void doJob() {
	String jobenabled = Play.configuration
		.getProperty("jobs.arrivalnotification");
	if (jobenabled != null && jobenabled.equals("disabled")) {
	    Logger.info("Arrival notification job is disabled!!!");
	    return;
	}
	Logger.info("Starting arrival notification job.");
	List<Note> notes = Note.pendingForNotification();
	Logger.info("There are " + notes.size() + " notifications to send.");
	for (Note note : notes) {
	    for (Receiver receiver : note.receivers) {
		if (receiver.sendByEmail()) {
		    NotesMailer.arrivalNotification(note, receiver);
		    receiver.sent = true;
		    receiver.save();
		    Logger.info("Sent notification to " + note.receivers.size()
			    + " receivers of note #" + note.id);
		} else if (receiver.sendToFacebookWall()) {
		    String action = Play.configuration.getProperty("baseUrl")
			    + Router.reverse("auth.FacebookAuth.signInWithFacebook").url;
		    WSRequest request = WS
			    .url("https://graph.facebook.com/%s/feed?access_token=%s",
				    String.valueOf(receiver.friend),
				    WS.encode(note.sender.facebook.accessToken));
		    request.setParameter("message", Messages.get(
			    "facebook.arrival.intro", note.created));
		    request.setParameter("link", action);
		    request.setParameter("name",
			    Messages.get("facebook.arrival.seeNote"));
		    HttpResponse post = request.post();
		    if (post.getStatus() != 200) {
			Logger.warn("Couldn't send note " + note.id
				+ ". Facebook returned: " + post.getStatus()
				+ " - " + request.post().getString());
		    } else {
			receiver.sent = true;
			receiver.save();
		    }
		}
	    }

	    boolean sentToAll = true;
	    for (Receiver receiver : note.receivers) {
		if (!receiver.sent) {
		    sentToAll = false;
		    break;
		}
	    }

	    if (sentToAll) {
		note.sent = true;
		note.save();
	    }
	}
    }

}
