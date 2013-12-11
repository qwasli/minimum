package ch.meemin.minimum.customers;

import java.util.List;

import lombok.Getter;
import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.PrepaidSubscriptions;
import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.settings.Subscriptions;
import ch.meemin.minimum.entities.settings.TimeSubscriptions;
import ch.meemin.minimum.entities.subscriptions.MasterSubscription;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.utils.DiscardClickListener;
import ch.meemin.minimum.utils.EntityFieldGroup;
import ch.meemin.minimum.utils.FormLayout;
import ch.meemin.minimum.utils.Props;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This is the window used to add new contacts to the 'address book'. It does not do proper validation - you can add
 * weird stuff.
 */
public class SellSubscriptionWin extends Window {
	private static final long serialVersionUID = 1L;

	// private final JPAContainer<Customer> container;
	private final Lang lang;

	private final FieldGroup form = new EntityFieldGroup<Customer>(Customer.class) {
		private static final long serialVersionUID = -1026191871445717584L;

		@Override
		public void commit() throws CommitException {
			super.commit();
			((EntityItem<Customer>) getItemDataSource()).commit();
			Minimum minimum = (Minimum) getUI();
			minimum.selectCustomer(getEntity().getId(), true);
			close();
		}

		@Override
		public void discard() throws SourceException {
			super.discard();
			close();
		};
	};

	public SellSubscriptionWin(Minimum minimum, EntityItem<Customer> item) {
		super(minimum.getLang().getText("SellSub"));
		this.lang = minimum.getLang();
		setModal(true);
		setSizeUndefined();

		item.refresh();
		item.setBuffered(false);
		Customer customer = item.getEntity();

		FormLayout formLayout = new FormLayout();
		formLayout.setSizeUndefined();
		formLayout.setDescription(lang.getText("SellSubscriptionFormDescription"));
		CreateSubscriptionButtons csf = new CreateSubscriptionButtons(lang, minimum.getSettings(), customer);

		formLayout.addComponent(csf);
		form.bind(csf, "currentSubscription");

		form.setItemDataSource(item);

		HorizontalLayout footer = formLayout.createDefaultFooter();
		Button cancelButton = new Button(lang.getText("Cancel"), new DiscardClickListener(form));
		cancelButton.setClickShortcut(KeyCode.ESCAPE);
		footer.addComponent(cancelButton);

		setContent(formLayout);

		minimum.addWindow(this);
	}

	private class CreateSubscriptionButtons extends CustomField<Subscription> implements ClickListener {
		private final VerticalLayout layout = new VerticalLayout();
		private final Customer customer;

		public CreateSubscriptionButtons(Lang lang, Settings settings, Customer customer) {
			this.customer = customer;
			final List<TimeSubscriptions> tSubs = settings.getTimeSubscriptions();
			final List<PrepaidSubscriptions> pSubs = settings.getPrepaidSubscriptions();
			setBuffered(false);
			setImmediate(true);
			layout.setSpacing(true);
			if (settings.is(Flag.USE_MASTER_SUBSCRIPTION)) {
				layout.addComponent(new SelectButton(null, lang.getText("MasterSubscription"), this));
			}
			if (!tSubs.isEmpty()) {
				HorizontalLayout hl = new HorizontalLayout();
				for (TimeSubscriptions ts : tSubs) {
					hl.addComponent(new SelectButton(ts, ts.getName() + " (" + ts.getPrice(settings, customer) + ")", this));
				}
				layout.addComponent(hl);
				hl.setSpacing(true);
			}
			if (!pSubs.isEmpty()) {
				HorizontalLayout hl = new HorizontalLayout();
				for (PrepaidSubscriptions ps : pSubs) {
					hl.addComponent(new SelectButton(ps, ps.getName() + " (" + ps.getPrice(settings, customer) + ")", this));
				}
				layout.addComponent(hl);
				hl.setSpacing(true);
			}
		}

		private class SelectButton extends Button {
			@Getter
			Subscriptions subs;

			public SelectButton(Subscriptions subs, String caption, ClickListener listener) {
				super(caption, listener);
				this.subs = subs;
				setPrimaryStyleName(Props.MINIMUMBUTTON);
				setHeight(100, Unit.PIXELS);
			}
		}

		@Override
		public void buttonClick(ClickEvent event) {
			SelectButton sb = (SelectButton) event.getButton();
			Subscriptions subs = sb.getSubs();
			if (subs != null)
				setInternalValue(subs.createSubscription(customer));
			else
				setInternalValue(new MasterSubscription(customer));
			try {
				form.commit();
			} catch (CommitException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Component initContent() {
			return layout;
		}

		@Override
		public Class<? extends Subscription> getType() {
			return Subscription.class;
		}
	}
}
