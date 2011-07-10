import java.util.Date;

import models.FacebookAccount;
import models.Note;
import models.Receiver;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import services.Facebook;

public class FacebookTest extends UnitTest {
    private Note note;
    private Receiver receiver;
    private User user;

    @Before
    public void setUp() {
	FacebookAccount account = new FacebookAccount();
	account.userId = 100002031059460l;
	account.accessToken = "198949360120440|26826aeebae5022a148af2bd-100002031059460|GG3yiQBct06TE25AXrnIakWRh4E";
	account.save();
	User user = new User();
	user.email = "ycujdcg_huiwitz\u0040tfbnw.net";
	user.facebook = account;
	user.save();
	Note note = new Note();
	note.sender = user;
	note.message = "this is just a test";
	note.sendDate = new Date();
	MutableDateTime cd = new DateTime().toMutableDateTime();
	cd.addHours(-1);
	note.created = cd.toDate();
	note.save();
	Receiver r = new Receiver(note, 100002036699756l);
	r.save();

	note.receivers.add(r);
	note.save();

	this.note = note;
	this.receiver = r;
	this.user = user;
    }

    @Test
    public void includeContent() {
	Facebook.postToWall(note, receiver);
	String post = Facebook.lastPost;
	assertTrue(post.contains(note.message));
    }

    @Test
    public void dontIncludeContentWhenPrivate() {
	note.shared = false;
	Facebook.postToWall(note, receiver);
	String post = Facebook.lastPost;
	assertFalse(post.contains(note.message));
    }

    @Test
    public void facebookNotificationPrettyDateEnglishWhenPrivate() {
	note.shared = false;
	user.language = "en";
	Facebook.postToWall(note, receiver);
	String post = Facebook.lastPost;

	assertTrue(post
		.contains("1 hour ago I froze a note for you and now you can see it!"));
    }

    @Test
    public void facebookNotificationPrettyDateSpanishWhenPrivate() {
	note.shared = false;
	user.language = "es";
	Facebook.postToWall(note, receiver);
	String post = Facebook.lastPost;

	assertTrue(post
		.contains("Hace 1 hora congelé una nota para vos y ahora la podés ver!"));
    }

    @Test
    public void facebookNotificationPrettyDateEnglishWhenPublic() {
	user.language = "en";
	Facebook.postToWall(note, receiver);
	String post = Facebook.lastPost;

	assertTrue(post.contains("1 hour ago I froze this note for you"));
	assertTrue(post.contains(note.message));
    }

    @Test
    public void facebookNotificationPrettyDateSpanishWhenPublic() {
	user.language = "es";
	Facebook.postToWall(note, receiver);
	String post = Facebook.lastPost;

	assertTrue(post.contains("Hace 1 hora congelé esta nota para vos"));
	assertTrue(post.contains(note.message));
    }
}
