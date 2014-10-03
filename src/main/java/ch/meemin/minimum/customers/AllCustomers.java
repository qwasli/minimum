package ch.meemin.minimum.customers;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import ch.meemin.minimum.CurrentSettings;
import ch.meemin.minimum.Minimum.LoggedInEvent;
import ch.meemin.minimum.Minimum.SelectEvent;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.SubscriptionProvider;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.cdi.UIScoped;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;

@UIScoped
public class AllCustomers extends Table {
	@Inject
	Containers containers;
	@Inject
	private SubscriptionProvider subsProvider;
	@Inject
	private Lang lang;
	@Inject
	private CurrentSettings currSet;

	@Inject
	private javax.enterprise.event.Event<SelectEvent> selectEvent;

	@PostConstruct
	public void init() {
		this.setContainerDataSource(containers.getCustContainer());
		addGeneratedColumn("currentSubscription", new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				return lang.getText(source.getItem(itemId).getItemProperty(columnId).getValue().getClass().getSimpleName());
			}
		});
		setVisibleColumns("name", "email", "currentSubscription");
		setSizeFull();
		setImmediate(true);
		setSelectable(true);
		addValueChangeListener(new SelectUser());
		setSortContainerPropertyId("name");
		setPageLength(5);
		addStyleName(ValoTheme.TABLE_NO_HEADER);
		addStyleName(ValoTheme.TABLE_COMPACT);
		addStyleName(ValoTheme.TABLE_SMALL);
	}

	public void setFilter(String filter) {
		JPAContainer<Customer> container = containers.getCustContainer();
		container.removeAllContainerFilters();
		container.addContainerFilter("name", filter, true, false);
		select(container.firstItemId());
	}

	public void clearFilter() {
		containers.getCustContainer().removeAllContainerFilters();
	}

	public void setNullValue(@Observes LoggedInEvent event) {
		setValue(null);
	}

	private class SelectUser implements Property.ValueChangeListener {
		@Override
		public void valueChange(Property.ValueChangeEvent event) {
			Long id = (Long) getValue();
			if (id != null) {
				if (currSet.getSettings().is(Flag.SUBSCRIPTIONIDONCARD)) {
					Subscription sub = subsProvider.getByCustomer(id);
					selectEvent.fire(new SelectEvent(sub.getId()));
				} else
					selectEvent.fire(new SelectEvent(id));
			}

		}
	}

}
