package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import org.joda.time.DateTime;

import play.Play;
import play.db.jpa.Model;

@Entity
public class Capsula extends Model {
    public String message;
    public Date sendDate;
    public String receiver;
    public Boolean sent = false;

    public static Date getDefaultDate() {
	return new DateTime().plusMonths(1).toDate();
    }

    public static List<Capsula> pendingForNotification() {
	int amount = Integer.valueOf((String) Play.configuration.get("mail.arrivalNotification.size"));
	return find("sent = ? and sendDate <= ?", false, new Date()).fetch(amount);
    }
}
