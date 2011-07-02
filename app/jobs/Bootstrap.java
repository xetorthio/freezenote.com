package jobs;

import java.util.Properties;

import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {
    public void doJob() {
	if (Play.mode.isDev()) {
	    Fixtures.deleteAllModels();
	    Fixtures.loadModels("dev-data.yaml");
	}
	if (!Play.mode.isDev()) {
	    Properties props = play.libs.Mail.getSession().getProperties();
	    props.setProperty("mail.transport.protocol.rfc822",
		    Play.configuration.getProperty(
			    "mail.transport.protocol.rfc822", "smtp"));
	    props.setProperty("mail.aws.user",
		    Play.configuration.getProperty("mail.aws.user", ""));
	    props.setProperty("mail.aws.password",
		    Play.configuration.getProperty("mail.aws.password", ""));
	}
    }
}