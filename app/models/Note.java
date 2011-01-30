package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import net.sf.oval.constraint.Email;

import org.hibernate.annotations.Index;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import play.Play;
import play.data.validation.InFuture;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Note extends Model {
    @Required
    @MinSize(value = 2)
    @MaxSize(value = 255)
    public String message;
    @Required
    @InFuture
    public Date sendDate;
    @Required
    @ManyToOne
    public User sender;
    @Email
    @Required
    @Index(name = "IDX_RECEIVER")
    public String receiver;
    public Boolean sent = false;

    public static Date getDefaultDate() {
    	MutableDateTime date = new DateTime().plusMonths(1).toMutableDateTime(); 
        date.setTime(date.getHourOfDay(),0,0,0);
        return date.toDate();
    }

    public static List<Note> pendingForNotification() {
        int amount = Integer.valueOf((String) Play.configuration
                .get("mail.arrivalNotification.size"));
        DateMidnight date = new DateMidnight().plusDays(1);
                
        return find("sent = ? and sendDate < ?", false,
                date.toDate()).fetch(
                amount);
    }

	public static Note last() {
		List<Note> notes = Note.findAll();
		if(notes.size() == 0)
			return null;
        return notes.get(notes.size()-1);	
    }
}
