package models;

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
    
    public Receiver(Note note, String email) {
	this.note = note;
	this.email = email;
    }
}