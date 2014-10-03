package ch.meemin.minimum.customers;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import ch.meemin.minimum.CurrentSettings;
import ch.meemin.minimum.Minimum.LoggedInEvent;
import ch.meemin.minimum.Minimum.SelectEvent;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.subscriptions.PrepaidSubscription;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.entities.subscriptions.TimeSubscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.SubscriptionProvider;
import ch.meemin.minimum.utils.Props;

import com.vaadin.cdi.UIScoped;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

@UIScoped
public class SubscriptionInfo extends HorizontalLayout implements ClickListener, Property.ValueChangeListener {
	@Inject
	private Lang lang;
	@Inject
	private SellSubscriptionWin sellSubscriptionWin;
	@Inject
	private EditSubscriptionWin editSubscriptionWin;
	@Inject
	private SubscriptionProvider subsProvider;
	@Inject
	private CurrentSettings currSet;

	private Label label = new Label();
	private Button editButton = new Button("", this);

	private Long customerId;
	private Long subId;

	@PostConstruct
	public void init() {
		editButton.addStyleName(Props.MINIMUMBUTTON);
		editButton.setHeight(50, Unit.PIXELS);

		label.setStyleName(ValoTheme.LABEL_H2);
		label.setSizeUndefined();
		setWidth(100, Unit.PERCENTAGE);
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
	}

	public void clear() {
		removeAllComponents();
		customerId = null;
		subId = null;
	}

	public void setSubscription(@Observes SelectEvent event) {
		if (event.isClear()) {
			clear();
			return;
		}
		Subscription sub = subsProvider.getBySetting(event.getId(), currSet.getSettings().is(Flag.SUBSCRIPTIONIDONCARD));
		if (sub == null) {
			clear();
			return;
		}
		customerId = sub.getCustomer().getId();
		subId = sub.getId();
		refresh();
	}

	public void setCustomer(@Observes LoggedInEvent event) {
		refresh();
	}

	public void refresh() {
		if (subId == null)
			return;
		Subscription sub = subsProvider.getSubscription(subId);
		String text = sub.getTypeName(lang);
		if (sub instanceof PrepaidSubscription) {
			text += " (" + Integer.toString(((PrepaidSubscription) sub).getCredit()) + ")";
		} else if (sub instanceof TimeSubscription) {
			if (sub.isSuspended())
				text += " (" + lang.getText("suspended") + ")";
			else
				text += " (" + lang.formatDate(((TimeSubscription) sub).getExpiry()) + ")";
		}
		label.setValue(text);

		editButton.setCaption(sub.valid() || sub.isSuspended() ? lang.getText("Edit") : lang.getText("SellSubscription"));
		editButton.setVisible(!sub.isReplaced());
		removeAllComponents();
		Label spacer = new Label();
		addComponents(label, spacer, editButton);
		setExpandRatio(spacer, 1f);

	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		if (Subscription.class.isAssignableFrom(event.getProperty().getType()))
			refresh();
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Subscription sub = subsProvider.getSubscription(subId);
		if (!sub.valid() && !sub.isSuspended())
			sellSubscriptionWin.show(customerId);
		else
			editSubscriptionWin.show(subId);
	}

}