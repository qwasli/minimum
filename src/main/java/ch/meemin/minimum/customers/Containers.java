package ch.meemin.minimum.customers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.Getter;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.provider.CustomerProvider;
import ch.meemin.minimum.provider.SubscriptionProvider;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.cdi.UIScoped;

@UIScoped
public class Containers {
	@Getter
	JPAContainer<Customer> custContainer;
	@Inject
	private CustomerProvider customerProvider;

	@Getter
	JPAContainer<Subscription> subsContainer;
	@Inject
	private SubscriptionProvider subsProvider;

	@PostConstruct
	public void init() {
		this.custContainer = new JPAContainer<Customer>(Customer.class);
		this.custContainer.setEntityProvider(customerProvider);
		this.subsContainer = new JPAContainer<Subscription>(Subscription.class);
		this.subsContainer.setEntityProvider(subsProvider);
	}
}
