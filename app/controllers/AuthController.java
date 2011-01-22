package controllers;

import models.User;
import play.Play;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Controller;

public class AuthController extends Controller {
    public static void fakeLogin() {
        if (Play.mode.isDev()) {
            doLogin("test@test.com");
            CapsulaController.displayForm();
        }
    }

    public static void login() {
        render();
    }

    private static void doLogin(String email) {
        User user = User.login(email);
        if (user == null) {
            user = new User();
            user.email = email;
            user.save();
        }

        session.put("user", user.id);
    }

    public static void signInWithGoogle() {
        if (OpenID.isAuthenticationResponse()) {
            UserInfo verifiedUser = OpenID.getVerifiedID();
            if (verifiedUser == null) {
                flash.put("error", "Oops. Authentication has failed");
                login();
            }
            doLogin(verifiedUser.extensions.get("email"));
            CapsulaController.displayForm();
        } else {
            OpenID.id("https://www.google.com/accounts/o8/id").required(
                    "email", "http://axschema.org/contact/email").verify();
        }
    }
}