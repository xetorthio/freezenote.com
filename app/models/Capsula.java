package models;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Capsula extends Model {
	public String message;
	public Date sendDate;
	public String receiver;

	public static Date getDefaultDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 30);
		return calendar.getTime();
	}
}
