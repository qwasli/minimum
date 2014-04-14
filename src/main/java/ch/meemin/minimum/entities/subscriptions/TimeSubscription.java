package ch.meemin.minimum.entities.subscriptions;

import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.lang3.time.DateUtils;

import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.Duration;
import ch.meemin.minimum.entities.settings.TimeSubscriptions;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({ @NamedQuery(name = TimeSubscription.Q.CountValid, query = TimeSubscription.Q.CountValidQ) })
public class TimeSubscription extends Subscription {

	@Getter
	@Setter
	@Embedded
	private Duration balance = null;

	public TimeSubscription(Customer customer, TimeSubscriptions type) {
		super(customer, type);
		this.balance = type.getDuration();
		reactivate();
	}

	@Override
	public boolean valid() {
		if (expiry == null)
			return false;
		Date now = new Date();
		return !isReplaced() && (DateUtils.isSameDay(now, expiry) || expiry.after(now));
	}

	@Override
	public void replace() {
		TimeSubscription ts = new TimeSubscription();
		ts.setBalance(getBalance());
		ts.setExpiry(getExpiry());
		doReplace(ts);
	}

	@Override
	public boolean isSuspended() {
		return expiry == null;
	}

	@Override
	public void suspend() {
		balance.calculateBalance(new Date(), expiry);
		expiry = null;
	}

	@Override
	public void reactivate() {
		expiry = balance.calculateExpiry();
	}

	public class Q {
		/**
		 * @param date
		 */
		public static final String CountValid = "TimeSubscription.CountValid";
		protected static final String CountValidQ = "SELECT s.typeName,  COUNT(s) FROM TimeSubscription s WHERE s.typeName IS NOT NULL AND s.createdAt <= :date AND s.expiry >= :date AND s.replacedBy IS NULL GROUP BY s.typeName";

	}
}
