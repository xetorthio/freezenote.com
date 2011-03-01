package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class TwitterAccount extends Model {
    public Long userId;
    public String screenName;
    public String secret;
    public String token;
}
