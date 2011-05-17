package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Note;
import models.TimeZoneLocation;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.StatusCode;
import services.GeoIP;
import auth.UserAuth;

import com.maxmind.geoip.Location;

import controllers.auth.Auth;

public class Notes extends Controller {

    public static void displayForm() {
	User user = UserAuth.getUser();

	String ip;

	if (Play.mode.isDev()) {
	    ip = "186.22.156.151";
	} else {
	    ip = request.remoteAddress;
	}

	Location geoIplocation = GeoIP.locateCity(ip);
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

    public static void create(String message, int year, int month, int day, int hour,
	    String timeZoneId, String location, String[] receivers, String friend) {
	if (!UserAuth.isUserLoggedIn()) {
	    forbidden();
	}

	List<String> filteredReceivers = new ArrayList<String>(receivers.length);
	if (receivers != null) {
	    for (String receiver : receivers) {
		if (receiver != null && receiver.length() > 0) {
		    filteredReceivers.add(receiver);
		}
	    }
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
	if (filteredReceivers != null && friend == null) {
	    for (String receiverEmail : filteredReceivers) {
		validation.email(receiverEmail);
	    }
	}

	if (!validation.hasErrors()) {
	    User user = UserAuth.getUser();
	    Note note = new Note();
	    note.sendDate = when;
	    note.message = message;
	    note.sender = user;
	    note.location = location;
	    if (friend != null) {
		note.friend = Long.parseLong(friend);
	    } else if (filteredReceivers.size() > 0) {
		note.setReceiverEmails(filteredReceivers.toArray(new String[] {}));
	    } else {
		note.setReceiverEmails(new String[] { user.email });
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