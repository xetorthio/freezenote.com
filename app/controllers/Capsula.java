package controllers;

import java.util.Date;
import java.util.List;

import play.mvc.Controller;

public class Capsula extends Controller {

	public static void displayForm() {
		render();
	}

	public static void list() {
		List<models.Capsula> capsulas = models.Capsula.findAll();
		render(capsulas);
	}

	
	public static void create(String message, Date when) {
		models.Capsula capsula = new models.Capsula();
		capsula.sendDate = when;
		capsula.message = message;
		
		capsula.save();
		
		render();
	}
}
