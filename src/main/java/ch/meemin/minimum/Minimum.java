package ch.meemin.minimum;

import java.util.Collection;
import java.util.Locale;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.meemin.minimum.admin.AdminBar;
import ch.meemin.minimum.customers.AllCustomers;
import ch.meemin.minimum.customers.EditCustomerWin;
import ch.meemin.minimum.customers.LoginInfo;
import ch.meemin.minimum.customers.LoginInfo.Status;
import ch.meemin.minimum.customers.ShowCustomer;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.Visit;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.CustomerProvider;
import ch.meemin.minimum.provider.SubscriptionProvider;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.cdi.CDIUI;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@CDIUI("")
@Theme("minimum")
@Widgetset("ch.meemin.minimum.MinimumWidgetSet")
public class Minimum extends UI implements ValueChangeListener {
	private static final Logger LOG = LoggerFactory.getLogger(Minimum.class);

	@Getter
	private Lang lang = new Lang();

	@Inject
	private CustomerProvider customerProvider;
	@Inject
	private SubscriptionProvider subscriptionProvider;

	@Getter
	private JPAContainer<Customer> customerContainer;
	@Getter
	private JPAContainer<Subscription> subscriptionContainer;

	@Inject
	private AllCustomers allCustomers;

	private TextField searchField;
	private Button createCustomerButton;

	@Inject
	private ShowCustomer showCustomer;
	@Inject
	private AdminBar adminBar;

	@Inject
	private EditCustomerWin editCustomerWin;

	@Inject
	private CurrentSettings currSet;

	@Inject
	private javax.enterprise.event.Event<SelectEvent> selectEvent;
	@Inject
	private javax.enterprise.event.Event<LoginSelectedEvent> loginEvent;
	@Inject
	private javax.enterprise.event.Event<LoggedInEvent> loggedInEvent;

	private final VerticalLayout layout = new VerticalLayout();

	private long LastScanTime = 0;
	private Long lastScanID = 0L;

	@Override
	public void setLocale(Locale locale) {
		super.setLocale(locale);
		lang.setLocale(locale);
	}

	public static Minimum getCurrent() {
		return (Minimum) UI.getCurrent();
	}

	@Override
	protected void init(VaadinRequest request) {
		LOG.info("Initializing Minimum");
		setLocale(Lang.DE_CH);
		customerContainer = new JPAContainer<Customer>(Customer.class);
		customerContainer.setEntityProvider(customerProvider);
		customerContainer.addNestedContainerProperty("lastVisit.createdAt");
		subscriptionContainer = new JPAContainer<Subscription>(Subscription.class);
		subscriptionContainer.setEntityProvider(subscriptionProvider);

		MyErrorHandler errorHandler = new MyErrorHandler();
		setErrorHandler(errorHandler);
		getSession().setErrorHandler(errorHandler);
		setSizeFull();
		layout.setSizeFull();

		searchField = new TextField();
		searchField.setNullRepresentation("");
		searchField.setWidth(100, Unit.PERCENTAGE);
		searchField.setImmediate(true);
		searchField.addValueChangeListener(this);
		searchField.addBlurListener(new BlurListener() {

			@Override
			public void blur(BlurEvent event) {
				focus();
			}
		});
		layout.addComponent(searchField);

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth(100, Unit.PERCENTAGE);
		hl.addComponent(allCustomers);
		hl.setExpandRatio(allCustomers, 1f);
		createCustomerButton = new Button(lang.getText("CreateCustomer"), new CreateCustomer());
		createCustomerButton.setHeight(120, Unit.PIXELS);
		hl.addComponent(createCustomerButton);
		layout.addComponent(hl);

		layout.addComponent(showCustomer);
		layout.setExpandRatio(showCustomer, 1.0f);
		layout.addComponent(adminBar);
		searchField.focus();
		setContent(layout);
	}

	@Override
	public void focus() {
		Collection<Window> windows = getWindows();
		if (windows.size() == 0) {
			searchField.focus();
			// searchField.setValue(null);
			searchField.selectAll();
		} else {
			for (Window w : windows) {
				w.addCloseListener(new CloseListener() {
					@Override
					public void windowClose(CloseEvent e) {
						focus();
					}
				});
			}
		}
	}

	public void login(@Observes LoginSelectedEvent event) {

		Subscription sub = null;
		Customer customer = null;
		if (lastScanID == null)
			return;
		if (currSet.getSettings().is(Flag.SUBSCRIPTIONIDONCARD)) {
			sub = subscriptionProvider.getSubscription(lastScanID);
			customer = sub.getCustomer();
		} else {
			customer = customerProvider.getCustomer(lastScanID);
			sub = customer.getCurrentSubscription();
		}

		if (sub.valid()) {
			int wM = currSet.getSettings().getMinutesForWarning();
			Minutes warnMin = Minutes.minutes(wM);
			Visit lastVisit = sub.getLastVisit();
			Minutes minutesBetween = lastVisit != null ? Minutes.minutesBetween(new DateTime(lastVisit.getCreatedAt()),
					new DateTime()) : null;
			if (!event.isIgnoreTimeWarn() && wM > 0 && minutesBetween != null && minutesBetween.isLessThan(warnMin)) {
				loggedInEvent.fire(new LoggedInEvent(Status.WARN));
			} else {
				if (currSet.getSettings().is(Flag.SUBSCRIPTIONIDONCARD)) {
					sub.checkIn();
					subscriptionProvider.updateEntity(sub);
				} else {
					customer.checkIn(currSet.getSettings().is(Flag.USE_BASIC_SUBSCRIPTION));
					customerProvider.updateEntity(customer);
				}
				loggedInEvent.fire(new LoggedInEvent(Status.OK));
			}
		} else {
			loggedInEvent.fire(new LoggedInEvent(Status.NOTOK));
		}
	}

	public void select(@Observes SelectEvent event) {
		if (event.isClear()) {
			lastScanID = null;
			return;
		}
		Long id = event.getId();
		if (currSet.getSettings().is(Flag.SUBSCRIPTIONIDONCARD)) {
			if (!subscriptionContainer.containsId(id)) {
				LOG.warn("ID not found: " + id);
				lastScanID = null;
				Notification.show(lang.getText("SubscriptionNotFound"), "", Type.WARNING_MESSAGE);
				return;
			}
		} else {
			if (!customerContainer.containsId(id)) {
				LOG.warn("ID not found: " + id);
				lastScanID = null;
				Notification.show(lang.getText("CustomerNotFound"), "", Type.WARNING_MESSAGE);
				return;
			}
		}
		lastScanID = id;
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		String val = searchField.getValue();
		if (StringUtils.isBlank(val)) {
			allCustomers.clearFilter();
			return;
		}
		if (val.matches("[0-9]+"))
			try {
				if (val.length() >= 14) {
					LOG.info("Double scan " + val);
					val = val.substring(0, 14);
				}
				Long id = new Long(val);
				if (id.equals(lastScanID) && (LastScanTime + 2000) >= System.currentTimeMillis()) {
					LOG.info("Too fast rescan " + val);
					searchField.setValue(null);
					return;
				}
				selectEvent.fire(new SelectEvent(id));
				if (currSet.getSettings().is(Flag.DIRECTLOGIN))
					loginEvent.fire(new LoginSelectedEvent(false));
				LastScanTime = System.currentTimeMillis();
				searchField.setValue(null);
			} catch (NumberFormatException e) {
				allCustomers.setFilter(val);
			}
		else
			allCustomers.setFilter(val);
		searchField.selectAll();
	}

	private class CreateCustomer implements ClickListener {
		@Override
		public void buttonClick(ClickEvent event) {
			editCustomerWin.show(null);
		}
	}

	@AllArgsConstructor
	public static class SelectEvent {
		@Getter
		Long id;

		public boolean isClear() {
			return id == null;
		}
	}

	@AllArgsConstructor
	public static class LoginSelectedEvent {
		@Getter
		boolean ignoreTimeWarn;
	}

	@AllArgsConstructor
	public static class LoggedInEvent {
		@Getter
		LoginInfo.Status status;
	}

}
