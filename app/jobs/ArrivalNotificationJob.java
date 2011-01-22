package jobs;

import java.util.List;

import models.Capsula;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import controllers.CapsulaMailer;

@On("cron.notification.arrival")
public class ArrivalNotificationJob extends Job {

    public void doJob() {
        Logger.info("Starting arrival notification job.");
        List<Capsula> capsulas = Capsula.pendingForNotification();
        Logger.info("There are " + capsulas.size() + " notifications to send.");
        for (Capsula capsula : capsulas) {
            CapsulaMailer.arrivalNotification(capsula);
            capsula.sent = true;
            capsula.save();
            Logger.info("Sent notification to " + capsula.receiver
                    + " of capsula #" + capsula.id);
        }
    }

}
