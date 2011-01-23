import java.util.Date;

import models.Capsula;
import models.User;

import org.joda.time.DateTime;
import org.junit.Test;

import controllers.Auth;
import controllers.Capsulas;

import play.test.FunctionalTest;

public class CapsulaTest extends FunctionalTest {

	@Test
	public void shouldSaveHours() {

		DateTime today = new org.joda.time.DateTime();
		Date date = today.plusMonths(1).toDate();

		Capsula capsula = new Capsula();

		capsula.sendDate = date;
		capsula.message = "test";
		capsula.sender = new User();
		capsula.sender.email = "test@test.com";
		capsula.receiver = capsula.sender.email;
		
		capsula.sender.save();
		capsula.save();

		Capsula last = Capsula.last();

		DateTime expected = new DateTime(date);
		DateTime capsulaTime = new DateTime(last.sendDate);
		assertEquals(expected.getHourOfDay(), capsulaTime.getHourOfDay());
	}

}
