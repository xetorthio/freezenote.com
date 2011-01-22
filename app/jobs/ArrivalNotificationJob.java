package jobs;

import java.util.List;

import controllers.CapsulaMailer;

import models.Capsula;
import play.jobs.Job;
import play.jobs.On;

@On("cron.notification.arrival")
public class ArrivalNotificationJob extends Job {

    public void doJob() {
	List<Capsula> capsulas = Capsula.pendingForNotification();

	for (Capsula capsula : capsulas) {
	    CapsulaMailer.arrivalNotification(capsula);
	    capsula.sent = true;
	    capsula.save();
	}
    }

}
