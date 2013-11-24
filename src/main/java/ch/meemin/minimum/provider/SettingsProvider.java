package ch.meemin.minimum.provider;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ch.meemin.minimum.entities.settings.Duration;
import ch.meemin.minimum.entities.settings.Duration.TimeSpan;
import ch.meemin.minimum.entities.settings.PrepaidSubscriptions;
import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.entities.settings.TimeSubscriptions;

import com.vaadin.addon.jpacontainer.provider.MutableLocalEntityProvider;

@Stateless
@LocalBean
public class SettingsProvider extends MutableLocalEntityProvider<Settings> {
	@PersistenceContext
	private EntityManager em;

	public SettingsProvider() {
		super(Settings.class);
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

	public Settings getSettings() {
		Settings settings = em.find(Settings.class, 1l);
		if (settings != null)
			return settings;
		settings = new Settings();
		TimeSubscriptions tss = new TimeSubscriptions();
		tss.setDuration(new Duration(1, TimeSpan.YEARS));
		tss.setName("Jahresabo");
		settings.getTimeSubscriptions().add(tss);
		PrepaidSubscriptions pss = new PrepaidSubscriptions();
		pss.setCredit(11);
		pss.setName("11er");
		settings.getPrepaidSubscriptions().add(pss);
		em.persist(settings);
		em.flush();
		return settings;
	}
}
