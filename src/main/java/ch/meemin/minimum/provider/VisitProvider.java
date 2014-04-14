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

import ch.meemin.minimum.entities.Visit;

import com.vaadin.addon.jpacontainer.provider.MutableLocalEntityProvider;

@Stateless
@LocalBean
public class VisitProvider extends MutableLocalEntityProvider<Visit> {
	@PersistenceContext
	private EntityManager em;

	public VisitProvider() {
		super(Visit.class);
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

	public Map<String, Long> countVisitsSubscriptionType(Date from, Date to) {

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
		TypedQuery<Object[]> q = em.createNamedQuery(Visit.Q.CountBySubsType, Object[].class);
		q.setParameter("from", from);
		q.setParameter("to", to);
		Map<String, Long> res = new HashMap<>();
		for (Object[] x : q.getResultList())
			res.put((String) x[0], (Long) x[1]);
		return res;
	}
}
