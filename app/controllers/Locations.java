package controllers;

import play.mvc.Controller;

public class Locations extends Controller {
	public static void search(String q){
		renderJSON(models.TimeZoneLocation.search(q));
	}
}
