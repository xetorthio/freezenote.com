package controllers;

import java.util.Date;
import java.util.List;

import models.Capsula;
import models.User;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.StatusCode;

public class Capsulas extends Controller {

    public static void displayForm() {
        render();
    }

    @Before(unless = "create")
    static void checkAuthenticated() {
        if (!Auth.isUserLoggedIn()) {
            Auth.login();
        }
    }

    public static void created() {
        render();
    }
    
    public static void create(String message, Date when) {
        if (!Auth.isUserLoggedIn()) {
            forbidden();
        }

        validation.required(message);
        validation.required(when);
        validation.future(when);

        if (!validation.hasErrors()) {
            User user = Auth.getUser();
            Capsula capsula = new Capsula();
            capsula.sendDate = when;
            capsula.message = message;
            capsula.sender = user;
            capsula.receiver = user.email;
            capsula.save();
            response.status = StatusCode.CREATED;
            renderJSON(user);
        } else {
            badRequest();
        }
    }
    public static void last(){
    	if (Play.mode.isDev()) {
    		Capsula lastCapsula = Capsula.last(); 
    		render(lastCapsula);
        }
    }
}