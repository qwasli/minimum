package ch.meemin.minimum.entities.subscriptions;

import javax.persistence.Entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ch.meemin.minimum.entities.Customer;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasicSubscription extends Subscription {
	public BasicSubscription(Customer customer) {
		super(customer, null);
	}

	@Override
	public boolean valid() {
		return false;
	}

	@Override
	public void replace() {
		doReplace(new BasicSubscription());
	}
}
