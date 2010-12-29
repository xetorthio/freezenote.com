package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import org.joda.time.DateTime;

import play.db.jpa.Model;

@Entity
public class Capsula extends Model {
    public String message;
    public Date sendDate;
    public String receiver;
    public Boolean sent;

    public Capsula() {
	sent = false;
    }

    public static Date getDefaultDate() {
	return new DateTime().plusMonths(1).toDate();
    }

    public static List<Capsula> pendingForToday() {
	try {
	    Date date = new Date();
	    DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
	    DateFormat format = new SimpleDateFormat("MM-dd-yyyy 00:00:00");
	    Date from = dateFormat.parse(format.format(date));
	    format = new SimpleDateFormat("MM-dd-yyyy 23:59:59");
	    Date to = dateFormat.parse(format.format(date));

	    return find(
		    "select c from Capsula c where c.sent = :sent and sendDate between :from and :to")
		    .bind("sent", false).bind("from", from).bind("to", to)
		    .fetch();

	} catch (ParseException e) {
	    e.printStackTrace();
	    return null;
	}
    }
}
