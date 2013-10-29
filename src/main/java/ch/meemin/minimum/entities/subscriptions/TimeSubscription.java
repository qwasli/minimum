package ch.meemin.minimum.entities.subscriptions;

import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;

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

	public boolean isSuspended() {
		return expiry == null;
	}

	public void suspend() {
		balance.calculateBalance(new Date(), expiry);
		expiry = null;
	}

	public void reactivate() {
		expiry = balance.calculateExpiry();
	}
}
