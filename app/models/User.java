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

    public static User login(String email) {
	return find("email=?", email).first();
    }

    public List<Note> getReceivedNotes() {
	if (hasFacebookAccess()) {
	    return Note.find("(receiver=? or friend=?) and sent = ?", email,
		    facebook.userId, true).fetch();
	}
	return Note.find("receiver=? and sent = ?", email, true).fetch();
    }

    public boolean hasFacebookAccess() {
	return facebook != null;
    }

    public String toString() {
	return email;
    }
}
