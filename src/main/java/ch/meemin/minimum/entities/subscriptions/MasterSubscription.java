package ch.meemin.minimum.entities.subscriptions;

import javax.persistence.Entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ch.meemin.minimum.entities.Customer;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MasterSubscription extends Subscription {

	private boolean suspended = false;

	public MasterSubscription(Customer customer) {
		super(customer, null);
	}

	@Override
	public boolean valid() {
		return !isReplaced();
	}

	@Override
	public void replace() {
		doReplace(new MasterSubscription());
	}

	@Override
	public boolean isSuspended() {
		return suspended;
	}

	@Override
	public void suspend() {
		suspended = true;
	}

	@Override
	public void reactivate() {
		suspended = false;
	}
}
