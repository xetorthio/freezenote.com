import java.util.Date;

import models.Note;
import models.User;

import org.junit.AfterClass;
import org.junit.Test;

import play.i18n.Lang;
import play.libs.Mail;
import play.test.UnitTest;
import controllers.NotesMailer;

public class NotesMailerTest extends UnitTest {
    @Test
    public void arrivalNotification() {
	User sender = new User();
	sender.email = "test@test.com";
	sender.language = "en";
	sender.save();

	Note note = new Note();
	note.message = "vos sos note";
	note.sendDate = new Date();
	note.setReceivers(new String[] { "joe@example.com" });
	note.sender = sender;
	note.save();

	NotesMailer.arrivalNotification(note, note.receivers.get(0));

	String email = Mail.Mock.getLastMessageReceivedBy("joe@example.com");

	assertNotNull(email);
	assertTrue(email.contains("http://localhost:9000/login"));
	assertFalse(email.contains(note.message));
    }

    @Test
    public void ifGoogleLoginDirectly() {
	User sender = new User();
	sender.email = "test@test.com";
	sender.language = "en";
	sender.save();

	Note note = new Note();
	note.message = "vos sos note";
	note.sendDate = new Date();
	note.setReceivers(new String[] { "joe@gmail.com" });
	note.sender = sender;
	note.save();

	NotesMailer.arrivalNotification(note, note.receivers.get(0));

	note = new Note();
	note.message = "vos sos note";
	note.sendDate = new Date();
	note.setReceivers(new String[] { "joe@googlemail.com" });
	note.sender = sender;
	note.save();

	NotesMailer.arrivalNotification(note, note.receivers.get(0));

	note = new Note();
	note.message = "vos sos note";
	note.sendDate = new Date();
	note.setReceivers(new String[] { "joe@mail.google.com" });
	note.sender = sender;
	note.save();

	NotesMailer.arrivalNotification(note, note.receivers.get(0));

	String email = Mail.Mock.getLastMessageReceivedBy("joe@gmail.com");
	assertTrue(email.contains("http://localhost:9000/login/google"));

	email = Mail.Mock.getLastMessageReceivedBy("joe@googlemail.com");
	assertTrue(email.contains("http://localhost:9000/login/google"));

	email = Mail.Mock.getLastMessageReceivedBy("joe@mail.google.com");
	assertTrue(email.contains("http://localhost:9000/login/google"));
    }

    @Test
    public void useUserLanguage() {
	User sender = new User();
	sender.email = "test@test.com";
	sender.language = "es";
	sender.save();

	Note note = new Note();
	note.message = "vos sos note";
	note.sendDate = new Date();
	note.setReceivers(new String[] { "joe@example.com" });
	note.sender = sender;
	note.save();

	NotesMailer.arrivalNotification(note, note.receivers.get(0));

	String email = Mail.Mock.getLastMessageReceivedBy("joe@example.com");

	assertNotNull(email);
	assertTrue(email
		.contains("<meta content=\"es\" http-equiv=\"Content-Language\" />"));
    }

    @Test
    public void replyToSender() {
	User sender = new User();
	sender.email = "test@test.com";
	sender.language = "es";
	sender.save();

	Note note = new Note();
	note.message = "vos sos note";
	note.sendDate = new Date();
	note.setReceivers(new String[] { "joe@example.com" });
	note.sender = sender;
	note.save();

	NotesMailer.arrivalNotification(note, note.receivers.get(0));

	String email = Mail.Mock.getLastMessageReceivedBy("joe@example.com");

	assertNotNull(email);
	assertTrue(email.contains("ReplyTo: " + sender.email));
    }

    @AfterClass
    public static void returnToEnglish() {
	Lang.change("en");
    }
}
