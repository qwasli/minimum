package ch.meemin.minimum.customers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.Getter;

import org.vaadin.dialogs.ConfirmDialog;

import ch.meemin.minimum.CurrentSettings;
import ch.meemin.minimum.Minimum.SelectEvent;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.PrepaidSubscriptions;
import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.settings.Subscriptions;
import ch.meemin.minimum.entities.settings.TimeSubscriptions;
import ch.meemin.minimum.entities.subscriptions.MasterSubscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.CustomerProvider;
import ch.meemin.minimum.utils.FormLayout;
import ch.meemin.minimum.utils.Props;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.cdi.UIScoped;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@UIScoped
public class SellSubscriptionWin extends Window implements ClickListener {
	private static final long serialVersionUID = 1L;

	@Inject
	private Lang lang;

	@Inject
	private CurrentSettings currSet;
	@Inject
	private Containers containers;
	@Inject
	private CustomerProvider customerProvider;

	private VerticalLayout buttonsLayout = new VerticalLayout();

	private ArrayList<SelectButton> buttons = new ArrayList<>();

	private Customer customer;

	@Inject
	private javax.enterprise.event.Event<SelectEvent> selectEvent;

	public void commit() {
		customer = customerProvider.updateEntity(customer);
		if (currSet.getSettings().is(Flag.SUBSCRIPTIONIDONCARD))
			selectEvent.fire(new SelectEvent(customer.getCurrentSubscription().getId()));
		else
			selectEvent.fire(new SelectEvent(customer.getId()));
		customer = null;
		close();
	}

	public void discard() {
		customer = null;
		close();
	};

	@PostConstruct
	public void init() {
		FormLayout formLayout = new FormLayout();
		formLayout.setSizeUndefined();
		formLayout.setDescription(lang.getText("SellSubscriptionFormDescription"));
		prepareSubsciptionButtons(lang, currSet.getSettings());

		formLayout.addComponent(buttonsLayout);

		HorizontalLayout footer = formLayout.createDefaultFooter();
		Button cancelButton = new Button(lang.getText("Cancel"), new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				discard();
			}
		});
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
		this.customer = item.getEntity();
		if (this.customer.getCurrentSubscription().valid()) {
			Notification.show(lang.get("HasValidSubscription"));
			selectEvent.fire(new SelectEvent(this.customer.getCurrentSubscription().getId()));
			return;
		}
		updatePrices();
		UI.getCurrent().addWindow(this);
	}

	public void updatePrices() {
		Settings settings = currSet.getSettings();
		for (SelectButton sb : buttons) {
			Subscriptions subs = sb.getSubs();
			sb.setCaption(subs.getName() + " (" + subs.getPrice(settings, customer) + ")");
		}
	}

	public void prepareSubsciptionButtons(Lang lang, Settings settings) {
		final List<TimeSubscriptions> tSubs = settings.getTimeSubscriptions();
		final List<PrepaidSubscriptions> pSubs = settings.getPrepaidSubscriptions();
		buttonsLayout.setSpacing(true);
		if (settings.is(Flag.USE_MASTER_SUBSCRIPTION)) {
			buttonsLayout.addComponent(new SelectButton(null, lang.getText("MasterSubscription"), this));
		}
		if (!tSubs.isEmpty()) {
			HorizontalLayout hl = new HorizontalLayout();
			for (TimeSubscriptions ts : tSubs) {
				SelectButton sb = new SelectButton(ts, ts.getName(), this);
				buttons.add(sb);
				hl.addComponent(sb);
			}
			buttonsLayout.addComponent(hl);
			hl.setSpacing(true);
		}
		if (!pSubs.isEmpty()) {
			HorizontalLayout hl = new HorizontalLayout();
			for (PrepaidSubscriptions ps : pSubs) {
				SelectButton sb = new SelectButton(ps, ps.getName(), this);
				buttons.add(sb);
				hl.addComponent(sb);
			}
			buttonsLayout.addComponent(hl);
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
		final Subscriptions subs = sb.getSubs();
		if (subs != null) {
			if (subs.mayKeepId(customer)) {
				ConfirmDialog.show(UI.getCurrent(), lang.get("KeepCard"), lang.get("KeepCardExpl"), lang.get("YesKeepCard"),
						lang.get("NoKeepCard"), new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								customer.setCurrentSubscription(subs.createSubscription(customer, dialog.isConfirmed()));
								commit();
							}
						});
			} else {
				customer.setCurrentSubscription(subs.createSubscription(customer, false));
				commit();
			}
		} else {
			customer.setCurrentSubscription(new MasterSubscription(customer));
			commit();
		}
	}
}
