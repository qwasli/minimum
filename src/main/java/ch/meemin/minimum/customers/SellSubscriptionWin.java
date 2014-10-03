package ch.meemin.minimum.customers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.Getter;
import ch.meemin.minimum.CurrentSettings;
import ch.meemin.minimum.Minimum.SelectEvent;
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
import com.vaadin.cdi.UIScoped;
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
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This is the window used to add new contacts to the 'address book'. It does not do proper validation - you can add
 * weird stuff.
 */
@UIScoped
public class SellSubscriptionWin extends Window {
	private static final long serialVersionUID = 1L;

	@Inject
	private Lang lang;

	@Inject
	private CurrentSettings currSet;
	@Inject
	private Containers containers;
	CreateSubscriptionButtons csf;

	@Inject
	private javax.enterprise.event.Event<SelectEvent> selectEvent;

	private FieldGroup form = new EntityFieldGroup<Customer>(Customer.class) {
		private static final long serialVersionUID = -1026191871445717584L;

		@Override
		public void commit() throws CommitException {
			super.commit();
			EntityItem<Customer> item = (EntityItem<Customer>) getItemDataSource();
			item.commit();
			if (currSet.getSettings().is(Flag.SUBSCRIPTIONIDONCARD))
				selectEvent.fire(new SelectEvent(item.getEntity().getCurrentSubscription().getId()));
			else
				selectEvent.fire(new SelectEvent((Long) item.getItemId()));
			close();
		}

		@Override
		public void discard() throws SourceException {
			super.discard();
			close();
		};
	};

	@PostConstruct
	public void init() {
		FormLayout formLayout = new FormLayout();
		formLayout.setSizeUndefined();
		formLayout.setDescription(lang.getText("SellSubscriptionFormDescription"));
		csf = new CreateSubscriptionButtons(lang, currSet.getSettings());

		formLayout.addComponent(csf);
		form.bind(csf, "currentSubscription");

		HorizontalLayout footer = formLayout.createDefaultFooter();
		Button cancelButton = new Button(lang.getText("Cancel"), new DiscardClickListener(form));
		cancelButton.setClickShortcut(KeyCode.ESCAPE);
		footer.addComponent(cancelButton);

		setContent(formLayout);

	}

	public void show(long customerId) {
		setModal(true);
		setSizeUndefined();
		EntityItem<Customer> item = containers.getCustContainer().getItem(customerId);

		item.refresh();
		item.setBuffered(false);
		Customer customer = item.getEntity();
		csf.setCustomer(customer);

		form.setItemDataSource(item);

		UI.getCurrent().addWindow(this);
	}

	private class CreateSubscriptionButtons extends CustomField<Subscription> implements ClickListener {
		private VerticalLayout layout = new VerticalLayout();
		private Customer customer;

		private ArrayList<SelectButton> buttons = new ArrayList<>();

		public void setCustomer(Customer customer) {
			this.customer = customer;
			Settings settings = currSet.getSettings();
			for (SelectButton sb : buttons) {
				Subscriptions subs = sb.getSubs();
				sb.setCaption(subs.getName() + " (" + subs.getPrice(settings, customer) + ")");
			}
		}

		public CreateSubscriptionButtons(Lang lang, Settings settings) {
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
					SelectButton sb = new SelectButton(ts, ts.getName(), this);
					buttons.add(sb);
					hl.addComponent(sb);
				}
				layout.addComponent(hl);
				hl.setSpacing(true);
			}
			if (!pSubs.isEmpty()) {
				HorizontalLayout hl = new HorizontalLayout();
				for (PrepaidSubscriptions ps : pSubs) {
					SelectButton sb = new SelectButton(ps, ps.getName(), this);
					buttons.add(sb);
					hl.addComponent(sb);
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
				addStyleName(Props.MINIMUMBUTTON);
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
