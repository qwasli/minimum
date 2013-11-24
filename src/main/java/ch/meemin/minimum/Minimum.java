package ch.meemin.minimum;

import java.util.Collection;
import java.util.Locale;

import javax.inject.Inject;

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
import ch.meemin.minimum.customers.ShowCustomer;
import ch.meemin.minimum.customers.SubscriptionInfo;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.Visit;
import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.CustomerProvider;
import ch.meemin.minimum.provider.SettingsProvider;
import ch.meemin.minimum.provider.SubscriptionProvider;
import ch.meemin.minimum.utils.Props;

import com.vaadin.addon.jpacontainer.EntityItem;
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
@CDIUI
@Theme("minimum")
@Widgetset("ch.meemin.minimum.MinimumWidgetSet")
public class Minimum extends UI implements ValueChangeListener {
	private static final Logger LOG = LoggerFactory.getLogger(Minimum.class);

	@Getter
	private Lang lang = new Lang();

	@Getter
	@Inject
	private CustomerProvider customerProvider;
	@Getter
	@Inject
	private SubscriptionProvider subscriptionProvider;
	@Getter
	@Inject
	private SettingsProvider settingsProvider;
	@Getter
	private JPAContainer<Customer> customerContainer;
	@Getter
	private JPAContainer<Subscription> subscriptionContainer;

	@Getter
	private Settings settings;

	@Getter
	private AllCustomers allCustomers;
	@Getter
	private TextField searchField;

	private Button createCustomerButton;
	@Getter
	private ShowCustomer showCustomer;
	@Getter
	private SubscriptionInfo subscriptionInfo;
	@Getter
	private LoginInfo loginInfo;

	@Getter
	private AdminBar adminBar;

	private final VerticalLayout layout = new VerticalLayout();

	@Override
	public void setLocale(Locale locale) {
		super.setLocale(locale);
		lang.setLocale(locale);
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

		loadSettings();
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
		allCustomers = new AllCustomers(this, customerContainer);
		hl.addComponent(allCustomers);
		hl.setExpandRatio(allCustomers, 1f);
		createCustomerButton = new Button(lang.getText("CreateCustomer"), new CreateCustomer());
		createCustomerButton.setHeight(120, Unit.PIXELS);
		createCustomerButton.setWidth(120, Unit.PIXELS);
		createCustomerButton.setPrimaryStyleName(Props.MINIMUMBUTTON);
		hl.addComponent(createCustomerButton);
		layout.addComponent(hl);

		subscriptionInfo = new SubscriptionInfo(lang, this);
		loginInfo = new LoginInfo(lang, this);
		showCustomer = new ShowCustomer(lang, this, subscriptionInfo, loginInfo);

		layout.addComponent(showCustomer);
		adminBar = new AdminBar(lang);
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

	public void loadSettings() {
		settings = settingsProvider.getSettings();
	}

	public void login(EntityItem<Subscription> item, boolean ignoreTimeWarn) {
		Subscription sub = item.getEntity();
		Customer customer = sub.getCustomer();
		if (sub.valid()) {
			int wM = settings.getMinutesForWarning();
			Minutes warnMin = Minutes.minutes(wM);
			Visit lastVisit = sub.getLastVisit();
			Minutes minutesBetween = lastVisit != null ? Minutes.minutesBetween(new DateTime(lastVisit.getCreatedAt()),
					new DateTime()) : null;
			if (!ignoreTimeWarn && wM > 0 && minutesBetween != null && minutesBetween.isLessThan(warnMin)) {
				loginInfo.showLoginAfterWarnButton(item);
				loginInfo.show(Props.ICON_WARN, lang.getText("TimeWarn"), false);
			} else {
				customer.checkIn(settings.is(Flag.USE_BASIC_SUBSCRIPTION));
				customerProvider.updateEntity(customer);
				loginInfo.show(Props.ICON_OK, "", true);
			}
		} else {
			loginInfo.show(Props.ICON_NOT_OK, lang.getText("InvalidSubscription"), true);
		}
	}

	public void clear() {
		showCustomer.clear();
		subscriptionInfo.clear();
	}

	public void selectCustomer(Long id, boolean doNotLogin) {
		loginInfo.clear();

		if (!customerContainer.containsId(id)) {
			Notification.show(lang.getText("CustomerNotFound"), "", Type.WARNING_MESSAGE);
			return;
		}
		EntityItem<Customer> cItem = customerContainer.getItem(id);
		showCustomer.setCustomer(cItem);
		Long subId = cItem.getEntity().getCurrentSubscription().getId();
		EntityItem<Subscription> item = subscriptionContainer.getItem(subId);
		subscriptionInfo.setSubscription(item, cItem);
		if (!doNotLogin && settings.is(Flag.DIRECTLOGIN))
			login(item, false);
		else
			loginInfo.showLoginButton(item);
	}

	public void selectSubscription(Long id, boolean doNotLogin) {
		loginInfo.clear();
		if (!subscriptionContainer.containsId(id)) {
			Notification.show(lang.getText("SubscriptionNotFound"), "", Type.WARNING_MESSAGE);
			return;
		}
		EntityItem<Subscription> sItem = subscriptionContainer.getItem(id);
		Subscription sub = sItem.getEntity();
		if (sub.isReplaced()) {
			Notification.show(lang.getText("ReplacedSubscription"), "", Type.ERROR_MESSAGE);
			return;
		}
		Customer c = sub.getCustomer();
		if (!c.getCurrentSubscription().equals(sub)) {
			Notification.show(lang.getText("NotCurrentSubscription"), "", Type.WARNING_MESSAGE);
			return;
		}
		EntityItem<Customer> cItem = customerContainer.getItem(c.getId());
		showCustomer.setCustomer(cItem);
		subscriptionInfo.setSubscription(sItem, cItem);
		if (!doNotLogin && settings.is(Flag.DIRECTLOGIN))
			login(sItem, false);
		else
			loginInfo.showLoginButton(sItem);
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		Minimum minimum = (Minimum) getUI();
		String val = searchField.getValue();
		if (StringUtils.isBlank(val)) {
			minimum.getAllCustomers().clearFilter();
			return;
		}
		try {
			Long id = new Long(val);
			if (settings.is(Flag.SUBSCRIPTIONIDONCARD))
				minimum.selectSubscription(id, false);
			else
				minimum.selectCustomer(id, false);
			searchField.setValue(null);
		} catch (NumberFormatException e) {
			minimum.getAllCustomers().setFilter(val);
		}
		searchField.selectAll();
	}

	private static class CreateCustomer implements ClickListener {
		@Override
		public void buttonClick(ClickEvent event) {
			new EditCustomerWin(event.getButton().getUI(), null);
		}
	}
}
