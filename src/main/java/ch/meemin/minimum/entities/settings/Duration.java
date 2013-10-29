package ch.meemin.minimum.entities.settings;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.joda.time.DateTime;
import org.joda.time.Days;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Duration {
	public enum TimeSpan {
		// HOURS,
		DAYS,
		WEEKS,
		MONTHS,
		YEARS;
	}

	@Getter
	@Setter
	private Integer amount;

	@Getter
	@Setter
	private TimeSpan timeSpan;

	public Date calculateExpiry() {
		GregorianCalendar gc = new GregorianCalendar();
		switch (timeSpan) {
		// case HOURS:
		// gc.add(GregorianCalendar.HOUR, amount);
		// break;
		case DAYS:
			gc.add(GregorianCalendar.DAY_OF_YEAR, amount);
			break;
		case WEEKS:
			gc.add(GregorianCalendar.WEEK_OF_YEAR, amount);
			break;
		case MONTHS:
			gc.add(GregorianCalendar.MONTH, amount);
			break;
		case YEARS:
			gc.add(GregorianCalendar.YEAR, amount);
			break;
		}
		return gc.getTime();
	}

	public void calculateBalance(Date now, Date then) {
		switch (timeSpan) {
		// case HOURS:
		// break;
		default:
			amount = Days.daysBetween(new DateTime(now.getTime()), new DateTime(then.getTime())).getDays();
			timeSpan = TimeSpan.DAYS;
		}
	}
}
