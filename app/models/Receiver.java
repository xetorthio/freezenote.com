package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import net.sf.oval.constraint.Email;
import play.db.jpa.Model;

@Entity
public class Receiver extends Model {
    @ManyToOne
    public Note note;

    @Email
    public String email;

    public Boolean sent = false;

    public Long friend;

    public Integer attempts = 0;

    @Column(name = "`read`")
    public Boolean read = false;
    public Date readDate;

    public Receiver(Note note, String email) {
	this.note = note;
	this.email = email;
    }

    public Receiver(Note note, Long friend) {
	this.note = note;
	this.friend = friend;
    }

    public boolean sendByEmail() {
	return email != null;
    }

    public boolean sendToFacebookWall() {
	return friend != null;
    }
}
