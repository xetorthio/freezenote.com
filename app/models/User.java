package models;

import javax.persistence.Entity;

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

    public static User login(String email) {
        return find("email=?", email).first();
    }
}
