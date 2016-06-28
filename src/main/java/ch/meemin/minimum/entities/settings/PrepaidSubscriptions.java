package ch.meemin.minimum.entities.settings;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.subscriptions.PrepaidSubscription;
import ch.meemin.minimum.entities.subscriptions.Subscription;

/**
 * Entity implementation class for Entity: TimeSubscriptions
 * 
 */
@Embeddable
public class PrepaidSubscriptions extends Subscriptions implements Serializable {
	@Getter
	@Setter
	@NotNull
	private Integer credit;

	@Override
	public boolean mayKeepId(Customer customer) {
		Subscription sub = customer.getCurrentSubscription();
		if (sub != null && sub instanceof PrepaidSubscription)
			return true;
		return false;
	}

	@Override
	public Subscription createSubscription(Customer customer, boolean keepId) {
		if (keepId && mayKeepId(customer))
			return fillSubscription(customer);
		return new PrepaidSubscription(customer, this);
	}

	public Subscription fillSubscription(Customer customer) {
		Subscription cs = customer.getCurrentSubscription();
		PrepaidSubscription ps = new PrepaidSubscription(customer, this);
		cs.setCredit(ps.getCredit());
		ps.setCredit(0);
		Date tmp = ps.getCreatedAt();
		ps.setCreatedAt(cs.getCreatedAt());
		cs.setCreatedAt(tmp);
		customer.getSubscriptions().add(ps);
		return cs;
	}
}
