package controllers;

import java.io.File;

import play.mvc.Controller;

public class Application extends Controller {
    public static void index() {
	render();
    }

    public static void robots() {
	File file = play.Play.getFile("public/robots.txt");
	response.cacheFor("24h");
	renderBinary(file);
    }
}
