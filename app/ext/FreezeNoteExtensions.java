package ext;

import java.util.Date;

import play.i18n.Lang;
import play.i18n.Messages;
import play.templates.JavaExtensions;

import com.ocpsoft.pretty.time.PrettyTime;

public class FreezeNoteExtensions extends JavaExtensions {

    public static String link(String text, Object href) {
	return link(text, href, null);
    }

    public static String link(String text, Object href, String id) {
	text = Messages.get(text);
	String idPart = id == null ? "" : " id='" + id + "'";
	return "<a href='" + href + "'" + idPart + ">" + text + "</a>";
    }

    public static String ago(Date date) {
	return new PrettyTime(Lang.getLocale()).format(date);
    }
}
