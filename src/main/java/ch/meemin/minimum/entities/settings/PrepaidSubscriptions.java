package ch.meemin.minimum.entities.settings;

import java.io.Serializable;

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
	public Subscription createSubscription(Customer customer) {
		return new PrepaidSubscription(customer, this);

	}
}
