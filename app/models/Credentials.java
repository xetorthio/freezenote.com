package models;

import javax.persistence.Entity;

import play.db.jpa.Model;
import play.modules.oauthclient.ICredentials;

@Entity
public class Credentials extends Model implements ICredentials {
    private String token;
    private String secret;

    public void setToken(String token) {
        this.token = token;
        save();
    }

    public String getToken() {
        return token;
    }

    public void setSecret(String secret) {
        this.secret = secret;
        save();
    }

    public String getSecret() {
        return secret;
    }
}
