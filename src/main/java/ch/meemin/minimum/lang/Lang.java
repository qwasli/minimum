package ch.meemin.minimum.lang;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.Getter;

import com.vaadin.cdi.UIScoped;

@SuppressWarnings("serial")
@UIScoped
public class Lang implements Serializable {
	public static final Locale DE_CH = new Locale("de", "CH");
	@Getter
	private String dateFormatNoTime;
	@Getter
	private String dateFormatWithTime;
	@Getter
	private Locale locale = DE_CH;

	private ResourceBundle resourceBundle = ResourceBundle.getBundle(Lang.class.getCanonicalName(), locale);

	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", DE_CH);
	private SimpleDateFormat sdfwt = new SimpleDateFormat("dd.MM.yyyy kk:mm", DE_CH);

	public String get(String key) {
		return getText(key);
	}

	public String getText(String key, Object... params) {
		String value;
		if (resourceBundle == null) {
			value = "No bundle!";
		} else {
			try {
				value = MessageFormat.format(resourceBundle.getString(key), params);
			} catch (MissingResourceException e) {
				value = "!" + key;
			}
		}
		return value;
	}

	public void setLocale(Locale locale) {
		try {
			resourceBundle = ResourceBundle.getBundle(Lang.class.getCanonicalName(), locale);
			this.dateFormatNoTime = getText("dateFormatNoTime");
			this.dateFormatWithTime = getText("dateFormatWithTime");
		} catch (MissingResourceException e) {
			// NOP
		}
	}

	public String formatDate(Date date) {
		return formatDate(date, false);
	}

	public String formatDate(Date date, boolean withtime) {
		if (date == null)
			return "";
		else if (withtime)
			return sdfwt.format(date);
		else
			return sdf.format(date);
	}
}