package controllers;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

import models.Note;
import models.User;
import models.TimeZoneLocation;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.StatusCode;
import auth.UserAuth;
import controllers.auth.Auth;

import services.GeoIP;
import com.maxmind.geoip.Location;

public class Notes extends Controller {

    public static void displayForm() {
        User user = UserAuth.getUser();
        Location geoIplocation = GeoIP.locateCity("186.22.156.151"); 
        TimeZoneLocation location = TimeZoneLocation.from(geoIplocation);
        
        render(user, location);
    }

    @Before(unless = { "create", "last", "sendAll" })
    static void checkAuthenticated() {
        if (!UserAuth.isUserLoggedIn()) {
            Auth.login();
        }
    }

    public static void created() {
        render();
    }

    public static void create(String message, int year, int month, int day, int hour, String timeZoneId, String location, String receiver, String friend) {
        if (!UserAuth.isUserLoggedIn()) {
            forbidden();
        }
        
        validation.required(message);
        validation.required(day);
        validation.required(month);
        validation.required(year);
        validation.required(hour);
        validation.required(timeZoneId);
        validation.required(location);
        
        DateTimeZone timezone = DateTimeZone.forID(timeZoneId);
        
        Date when = new DateTime(year, month, day, hour, 0, 0, 0, timezone).toDate();
        
        validation.future(when);
        if (receiver != null && receiver.length() > 0 && friend == null) {
            validation.email(receiver);
        }

        if (!validation.hasErrors()) {
            User user = UserAuth.getUser();
            Note note = new Note();
            note.sendDate = when;
            note.message = message;
            note.sender = user;
            note.location = location;
            if (receiver != null && receiver.length() > 0 && friend == null) {
                note.receiver = receiver;
            } else if (friend != null) {
                note.friend = Long.parseLong(friend);
            } else {
                note.receiver = user.email;
            }
            note.save();
            response.status = StatusCode.CREATED;
            renderJSON(user);
        } else {
            badRequest();
        }
    }

    public static void last() {
        if (Play.mode.isDev()) {
            Note lastNote = Note.last();
            render(lastNote);
        } else {
            notFound();
        }
    }

    public static void sendAll() {
        if (Play.mode.isDev()) {
            List<Note> notes = Note.findAll();
            for (Note note : notes) {
                note.sent = true;
                note.save();
            }
        } else {
            notFound();
        }
    }
}