package controllers.auth;

import controllers.Application;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Controller;
import auth.UserAuth;

public class GoogleAuth extends Controller {

    public static void signInWithGoogle() {
        if (OpenID.isAuthenticationResponse()) {
            UserInfo verifiedUser = OpenID.getVerifiedID();
            if (verifiedUser == null) {
                flash.put("error", "Oops. Authentication has failed");
                Auth.login();
            }
            UserAuth.doLogin(verifiedUser.extensions.get("email"));
            Application.index();
        } else {
            OpenID.id("https://www.google.com/accounts/o8/id").required("email",
                    "http://axschema.org/contact/email").verify();
        }
    }
}
