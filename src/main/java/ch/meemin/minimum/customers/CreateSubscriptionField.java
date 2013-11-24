package ch.meemin.minimum.customers;

import java.util.Collection;
import java.util.List;

import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.PrepaidSubscriptions;
import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.settings.TimeSubscriptions;
import ch.meemin.minimum.entities.subscriptions.MasterSubscription;
import ch.meemin.minimum.entities.subscriptions.PrepaidSubscription;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.entities.subscriptions.TimeSubscription;
import ch.meemin.minimum.lang.Lang;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

class CreateSubscriptionField extends CustomField<Subscription> {
	private final VerticalLayout layout = new VerticalLayout();
	private final Customer customer;
	private final NativeSelect typeSelect = new NativeSelect();
	private NativeSelect secondComponent;

	public CreateSubscriptionField(Lang lang, Settings settings, Customer customer) {
		this.customer = customer;
		final List<TimeSubscriptions> tSubs = settings.getTimeSubscriptions();
		final List<PrepaidSubscriptions> pSubs = settings.getPrepaidSubscriptions();
		setBuffered(true);
		setImmediate(false);
		typeSelect.setImmediate(true);
		typeSelect.setNullSelectionAllowed(false);
		layout.addComponent(typeSelect);
		if (settings.is(Flag.USE_MASTER_SUBSCRIPTION)) {
			typeSelect.addItem(MasterSubscription.class);
			typeSelect.setItemCaption(MasterSubscription.class, lang.getText("MasterSubscription"));
		}
		if (!tSubs.isEmpty()) {
			typeSelect.addItem(TimeSubscription.class);
			typeSelect.setItemCaption(TimeSubscription.class, lang.getText("TimeSubscription"));
		}
		if (!pSubs.isEmpty()) {
			typeSelect.addItem(PrepaidSubscription.class);
			typeSelect.setItemCaption(PrepaidSubscription.class, lang.getText("PrepaidSubscription"));
		}
		typeSelect.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				Class<? extends Subscription> val = (Class<? extends Subscription>) typeSelect.getValue();
				NativeSelect cb = null;
				if (TimeSubscription.class.equals(val))
					cb = new NativeSelect("", tSubs);
				else if (PrepaidSubscription.class.equals(val))
					cb = new NativeSelect("", pSubs);
				if (cb == null && secondComponent != null)
					layout.removeComponent(secondComponent);
				else
					layout.replaceComponent(secondComponent, cb);
				cb.setNullSelectionAllowed(false);
				Collection<?> itemIds = cb.getItemIds();
				if (itemIds.size() == 1)
					cb.setValue(itemIds.iterator().next());
				secondComponent = cb;
			}
		});
		Collection<?> itemIds = typeSelect.getItemIds();
		if (itemIds.size() == 1)
			typeSelect.setValue(itemIds.iterator().next());
	}

	@Override
	protected Component initContent() {
		return layout;
	}

	@Override
	public void commit() throws SourceException, InvalidValueException {
		Subscription sub = null;
		setInternalValue(sub);
		super.commit();
	}

	@Override
	public Class<? extends Subscription> getType() {
		return Subscription.class;
	}
}