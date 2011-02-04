package jobs;

import java.util.Properties;

import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {
    public void doJob() {
        Properties props = play.libs.Mail.getSession().getProperties();
        props.setProperty("mail.transport.protocol.rfc822", Play.configuration
                .getProperty("mail.transport.protocol.rfc822", "smtp"));
        props.setProperty("mail.aws.user", Play.configuration.getProperty(
                "mail.aws.user", ""));
        props.setProperty("mail.aws.password", Play.configuration.getProperty(
                "mail.aws.password", ""));
    }
}