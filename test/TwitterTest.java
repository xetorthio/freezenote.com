import java.util.Date;

import models.TwitterAccount;
import models.User;

import org.junit.Test;
import org.scribe.model.Response;

import play.mvc.Http;
import services.twitter.TwitterMessage;
import services.twitter.Twitter;

import com.google.gson.Gson;

public class TwitterTest extends MyFunctionalTest {
    static TwitterAccount account = new TwitterAccount();
    static {
	account.screenName = "testfreezenote";
	account.userId = 250146091l;
	account.token = "250146091-1hMmclYMhTSoUSP2Jk8EzCtaCPsq2CgL45UZFy4O";
	account.secret = "2S2hiB1Oteu8iN0TfQQrkviJGYQVxYRVF40JcP8M";
    }

    @Test
    public void verifyCredentials() {
	// Throws an exception on failure
	Twitter.verifyCredentials(account);
    }

    @Test
    public void getPostAndGetMessage() {
	String text = "Testing post message functionality " + new Date();
	Twitter.postMessage(account, text);
	TwitterMessage message = Twitter.getLastMessage(account);
	assertEquals(text, message.text);
    }

    @Test
    public void postStrangeCharacters() {
	String text = "Testing ññéí post strange characters " + new Date();
	Twitter.postMessage(account, text);
	TwitterMessage message = Twitter.getLastMessage(account);
	assertEquals(text, message.text);
    }
}
