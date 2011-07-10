import java.util.Date;

import models.Note;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.junit.AfterClass;
import org.junit.Test;

import play.i18n.Lang;
import play.libs.Mail;
import play.test.UnitTest;
import controllers.NotesMailer;

public class NotesMailerTest extends UnitTest {
    @Test
    public void invite() {
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
	assertTrue(email.contains("http://localhost:9000/"));
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

    @Test
    public void showFriendlyCreationDate() {
	MutableDateTime cd = new DateTime().toMutableDateTime();
	cd.addHours(-1);

	checkMailCreationDate(cd.toDate(), "en",
		"1 hour ago I froze this note for you");
    }

    @Test
    public void showFriendlyCreationDateSpanish() {
	MutableDateTime cd = new DateTime().toMutableDateTime();
	cd.addHours(-1);

	checkMailCreationDate(cd.toDate(), "es",
		"Hace 1 hora congel&eacute; esta nota para vos");
    }

    private void checkMailCreationDate(Date creation, String locale, String text) {
	MutableDateTime dt = new DateTime().toMutableDateTime();
	dt.addMinutes(1);

	User sender = new User();
	sender.email = "test@test.com";
	sender.language = locale;
	sender.save();

	Note note = new Note();
	note.message = "vos sos note";
	note.sendDate = dt.toDate();
	note.setReceivers(new String[] { "joe@example.com" });
	note.sender = sender;
	note.created = creation;
	note.save();

	NotesMailer.arrivalNotification(note, note.receivers.get(0));

	String email = Mail.Mock.getLastMessageReceivedBy("joe@example.com");

	assertNotNull(email);
	assertTrue(email.contains(text));
    }

    @Test
    public void includeNoteContent() {
	MutableDateTime dt = new DateTime().toMutableDateTime();
	dt.addMinutes(1);

	User sender = new User();
	sender.email = "test@test.com";
	sender.save();

	Note note = new Note();
	note.message = "vos sos note";
	note.sendDate = dt.toDate();
	note.setReceivers(new String[] { "joe@example.com" });
	note.sender = sender;
	note.save();

	NotesMailer.arrivalNotification(note, note.receivers.get(0));

	String email = Mail.Mock.getLastMessageReceivedBy("joe@example.com");

	assertTrue(email.contains(note.message));

    }

    @AfterClass
    public static void returnToEnglish() {
	Lang.change("en");
    }
}
