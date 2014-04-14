package ch.meemin.minimum.entities.subscriptions;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.Visit;
import ch.meemin.minimum.entities.settings.PrepaidSubscriptions;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({ @NamedQuery(name = PrepaidSubscription.Q.CountValid, query = PrepaidSubscription.Q.CountValidQ) })
public class PrepaidSubscription extends Subscription {

	public PrepaidSubscription(Customer customer, PrepaidSubscriptions type) {
		super(customer, type);
		this.credit = type.getCredit();
	}

	@Override
	public boolean valid() {
		return !isReplaced() && credit > 0;
	}

	@Override
	public Visit checkIn() {
		credit--;
		return super.checkIn();

	}

	@Override
	public void replace() {
		PrepaidSubscription ps = new PrepaidSubscription();
		ps.credit = credit;
		doReplace(ps);
	}

	@Override
	public void reactivate() {}

	@Override
	public void suspend() {}

	public class Q {

		public static final String CountValid = "PrepaidSubscription.CountValid";
		protected static final String CountValidQ = "SELECT s.typeName, COUNT(s) FROM PrepaidSubscription s WHERE s.typeName IS NOT NULL AND s.credit > 0 AND s.replacedBy IS NULL GROUP BY s.typeName";

	}

}
