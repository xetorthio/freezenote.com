package controllers;

import java.io.File;

import models.User;
import play.mvc.Controller;
import auth.UserAuth;

public class Application extends Controller {
    public static void index() {
        User user = UserAuth.getUser();
        render(user);
    }

    public static void robots() {
        File file = play.Play.getFile("public/robots.txt");
        response.cacheFor("24h");
        renderBinary(file);
    }
}
