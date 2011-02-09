import org.junit.Test;

import play.Play;
import play.libs.WS;
import play.mvc.Http;
import play.mvc.Http.Response;

public class FacebookLoginTest extends MyFunctionalTest {

    @Test
    public void shouldRedirectToFacebook() {
	String appId = Play.configuration.getProperty("facebook.app_id");
	String redirectUri = WS
		.encode("http://localhost:9000/login/facebook/done");

	Response response = GET("/login/facebook");
	assertStatus(Http.StatusCode.FOUND, response);
	assertHeaderEquals(
		"Location",
		"https://www.facebook.com/dialog/oauth?client_id="
			+ appId
			+ "&redirect_uri="
			+ redirectUri
			+ "&scope=email,read_friendlists,publish_stream,offline_access",
		response);
    }
}
