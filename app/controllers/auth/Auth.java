package controllers.auth;

import controllers.Application;
import controllers.Notes;
import play.Play;
import play.mvc.Controller;
import auth.UserAuth;

public class Auth extends Controller {
    public static void fakeLogin(String user) {
        if (Play.mode.isDev()) {
            UserAuth.doLogin(user);
            Notes.displayForm();
        }
    }

    public static void login() {
        render();
    }

    public static void logout() {
        UserAuth.doLogout();
        Application.index();
    }

}