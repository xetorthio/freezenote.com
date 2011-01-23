package controllers;

import models.User;
import play.Play;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Controller;

public class Auth extends Controller {
    public static void fakeLogin() {
        if (Play.mode.isDev()) {
            doLogin("test@test.com");
            Capsulas.displayForm();
        }
    }

    public static void login() {
        render();
    }

    public static void logout() {
        doLogout();
        Application.index();
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

    private static void doLogout() {
        session.clear();
    }

    public static void signInWithGoogle() {
        if (OpenID.isAuthenticationResponse()) {
            UserInfo verifiedUser = OpenID.getVerifiedID();
            if (verifiedUser == null) {
                flash.put("error", "Oops. Authentication has failed");
                login();
            }
            doLogin(verifiedUser.extensions.get("email"));
            Application.index();
        } else {
            OpenID.id("https://www.google.com/accounts/o8/id").required(
                    "email", "http://axschema.org/contact/email").verify();
        }
    }

    static boolean isUserLoggedIn() {
        if (!session.contains("user")) {
            return false;
        }
        User user = getUser();
        if (user == null) {
            session.clear();
            return false;
        }
        return true;
    }

    static User getUser() {
        User user = User.findById(Long.parseLong(session.get("user")));
        return user;
    }
}