package controllers;

import java.util.Date;

import models.Capsula;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;

public class CapsulaController extends Controller {

    public static void displayForm() {
        render();
    }

    @Before
    static void checkAuthenticated() {
        if (!session.contains("user")) {
            AuthController.login();
        } else {
            getUser();
        }
    }

    public static void create(String message, Date when) {
        validation.required(message);
        validation.required(when);
        validation.future(when);

        if (!validation.hasErrors()) {
            User user = getUser();
            Capsula capsula = new Capsula();
            capsula.sendDate = when;
            capsula.message = message;
            capsula.sender = user;
            capsula.receiver = user.email;
            capsula.save();
            render();
        } else {
            render("CapsulaController/displayForm.html", message);
        }
    }

    private static User getUser() {
        User user = User.findById(Long.parseLong(session.get("user")));
        if (user == null) {
            session.clear();
            AuthController.login();
        }
        return user;
    }
}