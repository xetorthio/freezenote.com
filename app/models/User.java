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
		    .find("select n FROM Receiver as r inner join r.note as n where (r.email=? or r.friend=?) and r.sent = ?",
			    email, facebook.userId, true).fetch();
	}
	return Note
		.find("select n from Receiver as r inner join r.note as n WHERE r.email = ? and r.sent = ?",
			email, true).fetch();
    }

    public int countUnreadNotes() {
	int unread = 0;
	List<Note> receivedNotes = getReceivedNotes();

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
