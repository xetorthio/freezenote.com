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
	    return Note.find(
		    "FROM Note as N WHERE (exists("
			    + "select R from Receiver R where R.note = N and R.email=?)"
			    + ") or friend="+facebook.userId+") and sent = true", email).fetch();
	}
	return Note.find(
		"FROM Note as N WHERE exists("
			+ "select R from Receiver R where R.note = N and R.email=?"
			+ ") and sent = true", email).fetch();
    }

    public boolean hasFacebookAccess() {
	return facebook != null;
    }

    public String toString() {
	return email;
    }
}
