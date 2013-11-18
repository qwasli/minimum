package ch.meemin.minimum.customers;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.subscriptions.PrepaidSubscription;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.entities.subscriptions.TimeSubscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.utils.Props;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Reindeer;

public class SubscriptionInfo extends CustomComponent implements ClickListener, Property.ValueChangeListener {
	private final Lang lang;
	private final Minimum minimum;
	private final HorizontalLayout layout = new HorizontalLayout();

	private final Label label = new Label();
	private final Button editButton = new Button("", this);

	private EntityItem<Subscription> subItem;
	private EntityItem<Customer> cItem;

	public SubscriptionInfo(Lang lang, Minimum minimum) {
		this.lang = lang;
		this.minimum = minimum;
		editButton.setPrimaryStyleName(Props.MINIMUMBUTTON);
		editButton.setHeight(35, Unit.PIXELS);

		label.setStyleName(Reindeer.LABEL_H1);
		layout.setSpacing(true);
		setCompositionRoot(layout);
		// if (subsProperty instanceof ValueChangeNotifier)
		// ((ValueChangeNotifier) subsProperty).addValueChangeListener(this);
	}

	public void setSubscription(EntityItem<Subscription> subItem, EntityItem<Customer> cItem) {

		this.cItem = cItem;
		if (this.subItem != null)
			this.subItem.removeValueChangeListener(this);
		this.subItem = subItem;
		this.subItem.addValueChangeListener(this);
		refresh();
	}

	private void refresh() {
		Subscription sub = subItem.getEntity();
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
		layout.removeAllComponents();
		layout.addComponents(label, editButton);

	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		if (Subscription.class.isAssignableFrom(event.getProperty().getType()))
			refresh();
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button b = event.getButton();
		Subscription sub = subItem.getEntity();
		if (!sub.valid() && !sub.isSuspended())
			new SellSubscriptionWin(minimum, cItem);
		else
			new EditSubscriptionWin(minimum, subItem);
	}

}