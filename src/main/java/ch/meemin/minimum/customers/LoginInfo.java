package ch.meemin.minimum.customers;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import ch.meemin.minimum.CurrentSettings;
import ch.meemin.minimum.Minimum.LoggedInEvent;
import ch.meemin.minimum.Minimum.LoginSelectedEvent;
import ch.meemin.minimum.Minimum.SelectEvent;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.subscriptions.BasicSubscription;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.SubscriptionProvider;
import ch.meemin.minimum.utils.Props;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

@UIScoped
public class LoginInfo extends CssLayout {
	public enum Status {
		OK("ok"),
		WARN("warn"),
		NOTOK("notok");
		String style;

		Status(String style) {
			this.style = style;
		}

		CustomComponent get() {
			CustomComponent cc = new CustomComponent();
			cc.setStyleName(style);
			cc.addStyleName("statusicon");
			return cc;
		}
	}

	@Inject
	private Lang lang;
	@Inject
	private CurrentSettings currentSettings;
	@Inject
	private SubscriptionProvider subsProvider;

	@Inject
	private javax.enterprise.event.Event<LoginSelectedEvent> loginEvent;

	private Button logginB, logInAnywayB;

	private Label timeWarnLabel;

	private Label invalidLabel;

	@PostConstruct
	public void init() {
		setSizeFull();
		// setHeight(50, Unit.PIXELS);
		// setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
		logInAnywayB = new Button(lang.getText("LogInAnyway"), new ForceLogin());
		logInAnywayB.addStyleName(Props.MINIMUMBUTTON);
		logInAnywayB.setWidth(100, Unit.PERCENTAGE);
		logginB = new Button(lang.getText("LogIn"), new Login());
		logginB.addStyleName(Props.MINIMUMBUTTON);
		logginB.setWidth(100, Unit.PERCENTAGE);

		timeWarnLabel = new Label(lang.getText("TimeWarn"));
		invalidLabel = new Label(lang.getText("InvalidSubscription"));
		addStyleName("logininfo");

	}

	public void selected(@Observes SelectEvent event) {
		removeAllComponents();
		if (event.isClear())
			return;
		Subscription sub = subsProvider.getBySetting(event.getId(),
				currentSettings.getSettings().is(Flag.SUBSCRIPTIONIDONCARD));

		if (sub == null)
			return;

		addComponent(logginB);
		if (sub.valid())
			logginB.setCaption(lang.getText("LogIn"));
		else if (sub instanceof BasicSubscription && currentSettings.getSettings().is(Flag.USE_BASIC_SUBSCRIPTION))
			logginB.setCaption(lang.getText("HasPayd", currentSettings.getSettings().getNormalPrize()));
		else {
			removeComponent(logginB);
			if (sub.isReplaced()) {
				Notification.show(lang.getText("ReplacedSubscription"), "", Type.WARNING_MESSAGE);
				addComponent(Status.NOTOK.get());
			}
		}
	}

	public void selected(@Observes LoggedInEvent event) {
		removeAllComponents();
		addComponent(event.getStatus().get());
		switch (event.getStatus()) {
		case NOTOK:
			addComponent(invalidLabel);
			break;
		case WARN:
			addComponent(timeWarnLabel);
			addComponent(logInAnywayB);
			break;
		}

	}

	private class Login implements ClickListener {
		@Override
		public void buttonClick(ClickEvent event) {
			loginEvent.fire(new LoginSelectedEvent(false));
		}
	}

	private class ForceLogin implements ClickListener {
		@Override
		public void buttonClick(ClickEvent event) {
			loginEvent.fire(new LoginSelectedEvent(true));
		}
	}
}
