package unit;
import java.util.Date;

import models.Note;

import org.junit.Test;

import play.libs.Mail;
import play.test.UnitTest;
import controllers.NotesMailer;

public class NotesMailerTest extends UnitTest {
    @Test
    public void arrivalNotification() {

	Note note = new Note();
	note.message = "vos sos note";
	note.sendDate = new Date();
	note.receiver = "joe@example.com";
	note.save();

	NotesMailer.arrivalNotification(note);

	String email = Mail.Mock.getLastMessageReceivedBy("joe@example.com");

	assertNotNull(email);
	assertTrue(email.contains("vos sos note"));

    }
}
