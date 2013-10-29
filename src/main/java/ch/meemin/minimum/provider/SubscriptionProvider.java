package ch.meemin.minimum.provider;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ch.meemin.minimum.entities.subscriptions.Subscription;

import com.vaadin.addon.jpacontainer.provider.MutableLocalEntityProvider;

@Stateless
@LocalBean
public class SubscriptionProvider extends MutableLocalEntityProvider<Subscription> {
	@PersistenceContext
	private EntityManager em;

	public SubscriptionProvider() {
		super(Subscription.class);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void runInTransaction(Runnable operation) {
		super.runInTransaction(operation);
	}

	@PostConstruct
	public void init() {
		setEntityManager(em);
		setTransactionsHandledByProvider(false);
		setEntitiesDetached(false);
	}
}
