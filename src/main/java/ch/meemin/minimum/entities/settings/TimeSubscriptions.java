package ch.meemin.minimum.entities.settings;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.Duration.TimeSpan;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.entities.subscriptions.TimeSubscription;

/**
 * Entity implementation class for Entity: TimeSubscriptions
 * 
 */
@Embeddable
public class TimeSubscriptions extends Subscriptions implements Serializable {

	@Getter
	@Setter
	@NotNull
	@Embedded
	private Duration duration = new Duration(0, TimeSpan.YEARS);

	@Override
	public Subscription createSubscription(Customer customer) {
		return new TimeSubscription(customer, this);
	}
	
}
