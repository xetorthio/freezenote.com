package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Index;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class User extends Model {
    @Index(name = "IDX_EMAIL")
    @Required
    @Email
    public String email;
    @ManyToOne
    public FacebookAccount facebook;
    @Required
    public String language = "en";

    public static User login(String email) {
	return find("email=?", email).first();
    }

    public List<Note> getReceivedNotes() {
	if (hasFacebookAccess()) {
	    return Note
		    .find("select n FROM Note as n inner join n.receivers as r where (r.email=? or r.friend=?) and n.sent = ?",
			    email, facebook.userId, true).fetch();
	}
	return Note
		.find("FROM Note as N WHERE exists("
			+ "select R from Receiver R where R.note = N and R.email=?"
			+ ") and sent = true", email).fetch();
    }

    public int countUnreadNotes() {
	int unread = 0;
	List<Note> receivedNotes = getReceivedNotes();
	/*
	System.out.println(((Note)Note.all().fetch().get(0)).receivers.get(0).email);
	System.out.println(((Note)Note.all().fetch().get(1)).receivers.get(0).friend);
	System.out.println(facebook.userId);
	*/
	for (Note note : receivedNotes) {
	    if (!note.wasReadBy(this)) {
		unread++;
	    }
	}
	return unread;
    }

    public boolean hasFacebookAccess() {
	return facebook != null;
    }

    public String toString() {
	return email;
    }
}
