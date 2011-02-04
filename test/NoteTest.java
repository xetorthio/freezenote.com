import java.util.Date;

import models.Note;
import models.User;

import org.joda.time.DateTime;
import org.junit.Test;

import play.test.FunctionalTest;

public class NoteTest extends FunctionalTest {

    @Test
    public void shouldSaveHours() {

        DateTime today = new org.joda.time.DateTime();
        Date date = today.plusMonths(1).toDate();

        Note note = new Note();

        note.sendDate = date;
        note.message = "test";
        note.sender = new User();
        note.sender.email = "test@test.com";
        note.receiver = note.sender.email;

        note.sender.save();
        note.save();

        Note last = Note.last();

        DateTime expected = new DateTime(date);
        DateTime noteTime = new DateTime(last.sendDate);
        assertEquals(expected.getHourOfDay(), noteTime.getHourOfDay());
    }

}
