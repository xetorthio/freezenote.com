package models;

import javax.persistence.Entity;

import play.db.jpa.Model;
import play.modules.oauthclient.ICredentials;

@Entity
public class TwitterAccount extends Model implements ICredentials {
    public Long userId;
    public String screenName;
    public String secret;
    public String token;

    public String getSecret() {
	return secret;
    }

    public void setSecret(String secret) {
	this.secret = secret;
    }

    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }
}
