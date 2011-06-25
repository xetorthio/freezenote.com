import java.lang.reflect.Field;
import java.util.Map;

import play.libs.Mail;

public class MailHelper {
    
    public static void clear() {
	try {
	    for (Field field : Mail.Mock.class.getDeclaredFields()) {
		if (field.getName().equals("emails")) {
		    field.setAccessible(true);
		    Map<String, String> emails = (Map<String, String>) field
			    .get(null);
		    emails.clear();
		}
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }
    
}
