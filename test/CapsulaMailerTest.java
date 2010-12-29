import java.util.Date;

import models.Capsula;

import org.junit.Test;

import play.libs.Mail;
import play.test.FunctionalTest;
import controllers.CapsulaMailer;

public class CapsulaMailerTest extends FunctionalTest {
    @Test
    public void arrivalNotification() {

	Capsula capsula = new Capsula();
	capsula.message = "vos sos capsulo";
	capsula.sendDate = new Date();
	capsula.receiver = "joe@example.com";
	capsula.save();

	CapsulaMailer.arrivalNotification(capsula);

	String email = Mail.Mock.getLastMessageReceivedBy("joe@example.com");

	assertNotNull(email);
	assertTrue(email.contains("vos sos capsulo"));

    }
}
