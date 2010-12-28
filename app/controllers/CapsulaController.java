package controllers;

import java.util.Date;
import java.util.List;

import models.Capsula;
import play.mvc.Controller;

public class CapsulaController extends Controller {

	public static void displayForm() {
		render();
	}

	public static void list() {
		List<Capsula> capsulas = Capsula.findAll();
		render(capsulas);
	}

	public static void create(String message, Date when) {
		Capsula capsula = new Capsula();
		capsula.sendDate = when;
		capsula.message = message;

		capsula.save();

		render();
	}
}
