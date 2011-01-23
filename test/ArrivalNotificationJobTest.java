import java.util.Date;

import jobs.ArrivalNotificationJob;

import models.Capsula;

import org.joda.time.DateTime;
import org.junit.Test;

import play.Play;
import play.libs.Mail;
import play.test.UnitTest;

public class ArrivalNotificationJobTest extends UnitTest {
	@Test
	public void onlySendNotSent() throws Exception {
		Capsula.deleteAll();

		Date today = new Date();

		Capsula c1 = new Capsula();
		c1.message = "m1";
		c1.sendDate = today;
		c1.receiver = "foo@example.com";
		c1.save();

		Capsula c2 = new Capsula();
		c2.message = "m2";
		c2.sendDate = today;
		c2.receiver = "bar@example.com";
		c2.sent = true;
		c2.save();

		ArrivalNotificationJob job = new ArrivalNotificationJob();
		job.doJob();

		String mailFoo = Mail.Mock.getLastMessageReceivedBy("foo@example.com");
		String mailBar = Mail.Mock.getLastMessageReceivedBy("bar@example.com");

		assertNotNull(mailFoo);
		assertTrue(c1.sent);

		assertNull(mailBar);
	}

	@Test
	public void onlySendWhenOld() throws Exception {
		Capsula.deleteAll();

		Date now = new Date();
		Date tomorrow = new DateTime().plusDays(1).toDate();
		
		Capsula c1 = new Capsula();
		c1.message = "m1";
		c1.sendDate = now;
		c1.receiver = "foo@example.com";
		c1.save();

		Capsula c2 = new Capsula();
		c2.message = "m2";
		c2.sendDate = tomorrow;
		c2.receiver = "bar@example.com";
		c2.save();

		ArrivalNotificationJob job = new ArrivalNotificationJob();
		job.doJob();

		String mailFoo = Mail.Mock.getLastMessageReceivedBy("foo@example.com");
		String mailBar = Mail.Mock.getLastMessageReceivedBy("bar@example.com");

		assertNotNull(mailFoo);
		assertTrue(c1.sent);

		assertNull(mailBar);
	}

	@Test
	public void onlySendSpecificAmount() throws Exception {
		Play.configuration.put("mail.arrivalNotification.size", "10");

		Capsula.deleteAll();

		Date now = new Date();

		for (int n = 0; n < 20; n++) {
			Capsula c = new Capsula();
			c.message = "m1";
			c.sendDate = now;
			c.receiver = "foo@example.com";
			c.save();
		}

		ArrivalNotificationJob job = new ArrivalNotificationJob();

		job.doJob();
		assertEquals(10, Capsula.find("sent = ?", true).fetch().size());

		job.doJob();
		assertEquals(20, Capsula.find("sent = ?", true).fetch().size());
	}
}
