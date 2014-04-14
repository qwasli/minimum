package ch.meemin.minimum.provider;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import ch.meemin.minimum.entities.subscriptions.PrepaidSubscription;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.entities.subscriptions.TimeSubscription;

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

	public Map<String, Long> countSoldSubscriptionsByType(Date from, Date to) {

		from = from != null ? from : new Date(0);
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(from);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		from = cal.getTime();

		to = to != null ? to : new Date();
		cal.setTime(to);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		to = cal.getTime();
		TypedQuery<Object[]> q = em.createNamedQuery(Subscription.Q.CountByTypName, Object[].class);
		q.setParameter("from", from);
		q.setParameter("to", to);
		Map<String, Long> res = new HashMap<>();
		for (Object[] x : q.getResultList())
			res.put((String) x[0], (Long) x[1]);
		return res;
	}

	public Map<String, Long> countValidTimesubscriptions() {
		TypedQuery<Object[]> q = em.createNamedQuery(TimeSubscription.Q.CountValid, Object[].class);
		q.setParameter("date", new Date());
		Map<String, Long> res = new HashMap<>();
		for (Object[] x : q.getResultList())
			res.put((String) x[0], (Long) x[1]);
		return res;
	}

	public Map<String, Long> countValidPrepaidSubscriptions() {
		TypedQuery<Object[]> q = em.createNamedQuery(PrepaidSubscription.Q.CountValid, Object[].class);
		Map<String, Long> res = new HashMap<>();
		for (Object[] x : q.getResultList())
			res.put((String) x[0], (Long) x[1]);
		return res;
	}
}
