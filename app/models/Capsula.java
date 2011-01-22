package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import net.sf.oval.constraint.Email;

import org.joda.time.DateTime;

import play.Play;
import play.data.validation.InFuture;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Capsula extends Model {
    @Required
    @MinSize(value = 2)
    public String message;
    @Required
    @InFuture
    public Date sendDate;
    @Required
    public User sender;
    @Email
    @Required
    public String receiver;
    public Boolean sent = false;

    public static Date getDefaultDate() {
        return new DateTime().plusMonths(1).toDate();
    }

    public static List<Capsula> pendingForNotification() {
        int amount = Integer.valueOf((String) Play.configuration
                .get("mail.arrivalNotification.size"));
        return find("sent = ? and sendDate <= ?", false, new Date()).fetch(
                amount);
    }
}
