package ch.meemin.minimum.provider;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ch.meemin.minimum.entities.Customer;

import com.vaadin.addon.jpacontainer.provider.MutableLocalEntityProvider;

@Stateless
@LocalBean
public class CustomerProvider extends MutableLocalEntityProvider<Customer> {
	@PersistenceContext
	private EntityManager em;

	public CustomerProvider() {
		super(Customer.class);
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

	public Customer getCustomer(Long customerID) {
		return em.find(Customer.class, customerID);
	}

}
