package services.twitter;

public class TwitterException extends RuntimeException {
    private int code;
    private String body;

    public TwitterException(int code, String body) {
	super(getMessage(code, body));
	this.code = code;
	this.body = body;
    }

    public static String getMessage(int code, String body) {
	return "Bad response code from twitter: " + code + "\n" + body;
    }

    public String getMessage() {
	return getMessage(code, body);
    }
}