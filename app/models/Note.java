package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
    // TODO:
    // @Index(name = "IDX_RECEIVER")
    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL)
    public List<Receiver> receivers = new ArrayList<Receiver>();
    public Boolean sent = false;
    @Required
    public Date created = new Date();
    public Boolean shared = true;

    public static DateTime getDefaultDate() {
	MutableDateTime date = new DateTime().plusMonths(1).toMutableDateTime();
	date.setTime(date.getHourOfDay(), 0, 0, 0);

	return date.toDateTime();
    }

    public static List<Note> pendingForNotification() {
	int amount = Integer.valueOf((String) Play.configuration
		.get("mail.arrivalNotification.size"));
	Date now = new Date();

	return find("sent = ? and sendDate <= ?", false, now).fetch(amount);
    }

    public static Note last() {
	List<Note> notes = Note.findAll();
	if (notes.size() == 0)
	    return null;
	return notes.get(notes.size() - 1);
    }

    public void setReceivers(String[] receiversData) {
	receivers = new ArrayList<Receiver>(receiversData.length);
	for (String receiver : receiversData) {
	    Receiver r = null;
	    try {
		Long friend = Long.parseLong(receiver);
		r = new Receiver(this, friend);
	    } catch (NumberFormatException e) {
		r = new Receiver(this, receiver);
	    }
	    receivers.add(r);
	}
    }

    public boolean wasReadBy(User user) {
	for (Receiver receiver : receivers) {
	    if (!receiver.read
		    && (receiver.email != null && receiver.email
			    .equals(user.email))
		    || (receiver.friend != null && receiver.friend
			    .equals(user.facebook.userId))) {
		return false;
	    }
	}
	return true;
    }

    public void markReadBy(User user) {
	for (Receiver receiver : receivers) {
	    if (!receiver.read
		    && (receiver.email != null && receiver.email
			    .equals(user.email))
		    || (receiver.friend != null && receiver.friend
			    .equals(user.facebook.userId))) {
		receiver.read = true;
		receiver.readDate = new Date();
		receiver.save();
	    }
	}
    }
}
