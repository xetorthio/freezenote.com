import java.util.Calendar;
import java.util.Date;

import models.Capsula;

import org.junit.Test;

import play.libs.Mail;
import play.test.UnitTest;
import controllers.CapsulaJob;
import controllers.CapsulaMailer;

public class CapsulaJobTest extends UnitTest {
	@Test
	public void sendArrivalNotification() throws Exception {
		Capsula.deleteAll();
		
		Date today = new Date();
		Capsula capsula1 = new Capsula();
		capsula1.message = "You are capsulo!";
		capsula1.sendDate = today;
		capsula1.receiver = "joe1@example.com";
		capsula1.save();
				
		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.DAY_OF_MONTH, 1);
		Date tomorrow = calendar2.getTime();
		Capsula capsula2 = new Capsula();
		capsula2.message = "You will be capsulo";
		capsula2.sendDate = tomorrow;
		capsula2.receiver = "joe2@example.com";
		capsula2.save();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		Date todayButLater = calendar.getTime();
		Capsula capsula3 = new Capsula();
		capsula3.message = "You are about to be capsulo";
		capsula3.sendDate = todayButLater;
		capsula3.receiver = "joe3@example.com";
		capsula3.save();

		Capsula capsula4 = new Capsula();
		capsula4.message = "You were capsulo!";
		capsula4.sendDate = today;
		capsula4.receiver = "joe4@example.com";
		capsula4.sent = true;
		capsula4.save();
		
		assertEquals(2, Capsula.pendingForToday().size());
		
		CapsulaJob job = new CapsulaJob();
		job.doJob();
		
		String mail1 = Mail.Mock.getLastMessageReceivedBy("joe1@example.com");
		String mail2 = Mail.Mock.getLastMessageReceivedBy("joe2@example.com");
		String mail3 = Mail.Mock.getLastMessageReceivedBy("joe3@example.com");
		String mail4 = Mail.Mock.getLastMessageReceivedBy("joe4@example.com");
		
		assertNotNull(mail1);
		assertTrue(capsula1.sent);
		
		assertNull(mail2);
		assertFalse(capsula2.sent);
		
		assertNotNull(mail3);
		assertTrue(capsula3.sent);
		
		assertNull(mail4);
		assertTrue(capsula4.sent);
	}
}
