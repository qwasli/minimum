package ch.meemin.minimum.utils;

import java.util.Date;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import com.ocpsoft.pretty.time.PrettyTime;
import com.vaadin.data.util.converter.Converter;

@AllArgsConstructor
@NoArgsConstructor
public class PrettyTimeConverter implements Converter<String, Date> {
	private String nullText;

	@Override
	public Date convertToModel(String value, Class<? extends Date> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return null;
	}

	@Override
	public String convertToPresentation(Date value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value == null)
			return nullText;

		PrettyTime prettyTime = new PrettyTime(locale);
		return prettyTime.format(value);
	}

	@Override
	public Class<Date> getModelType() {
		return Date.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}
