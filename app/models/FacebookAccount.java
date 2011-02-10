package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class FacebookAccount extends Model {
    public String email;
    public Long userId;
    public String accessToken;
    public String name;
}