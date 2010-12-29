package controllers;

import java.util.Date;

import models.Capsula;
import play.mvc.Controller;

public class CapsulaController extends Controller {

    public static void displayForm() {
	render();
    }

    public static void create(String message, Date when) {
	Capsula capsula = new Capsula();
	capsula.sendDate = when;
	capsula.message = message;

	capsula.save();

	render();
    }
}
