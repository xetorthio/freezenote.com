package controllers;

import models.Capsula;
import play.mvc.Mailer;

public class CapsulaMailer extends Mailer {

    public static void arrivalNotification(Capsula capsula) {
        setFrom("noreply@capsula.com");
        setSubject("You Received a new capsula");
        addRecipient(capsula.receiver);
        send(capsula);
    }

}
