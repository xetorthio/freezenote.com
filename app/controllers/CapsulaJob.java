package controllers;

import java.util.List;

import models.Capsula;
import play.jobs.Job;

public class CapsulaJob extends Job {

	public void doJob() {
		List<Capsula> capsulas = Capsula.pendingForToday();
		
		for (Capsula capsula : capsulas) {
	        CapsulaMailer.arrivalNotification(capsula);
	        capsula.sent = true;
	        capsula.save();
		}
	}

}
