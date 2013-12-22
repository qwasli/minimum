package ch.meemin.minimum.customers;

import java.util.EnumMap;
import java.util.EnumSet;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.subscriptions.BasicSubscription;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.utils.Props;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

public class LoginInfo extends CustomComponent implements ClickListener {
	HorizontalLayout hl = new HorizontalLayout();

	public enum Status {
		OK(new ThemeResource("icons/128/ok2.png")),
		WARN(new ThemeResource("icons/128/warn2.png")),
		NOTOK(new ThemeResource("icons/128/notOK2.png"));
		ThemeResource ir;

		Status(ThemeResource ir) {
			this.ir = ir;
		}
	}

	private final Minimum minimum;
	private final Lang lang;
	private EnumMap<Status, Image> images = new EnumMap<LoginInfo.Status, Image>(Status.class);

	private EntityItem<Subscription> item;
	private boolean ignoreWarn;

	public LoginInfo(Lang lang, Minimum minimum) {
		this.lang = lang;
		this.minimum = minimum;
		hl.setSizeFull();
		setCompositionRoot(hl);
		for (Status st : EnumSet.allOf(Status.class))
			images.put(st, new Image("", st.ir));
	}

	public void show(Status status, String info, boolean clear) {
		if (clear)
			hl.removeAllComponents();
		hl.addComponent(images.get(status));
		Label l = new Label(info);
		l.setStyleName("smileyinfo");
		hl.addComponent(l);
		hl.setExpandRatio(l, 1.0f);
	}

	public void clear() {
		hl.removeAllComponents();
	}

	public void showLoginAfterWarnButton(EntityItem<Subscription> item) {
		this.item = item;
		hl.removeAllComponents();
		this.ignoreWarn = true;
		Button b = new Button(lang.getText("LogInAnyway"), this);
		b.setPrimaryStyleName(Props.MINIMUMBUTTON);
		b.setWidth(100, Unit.PIXELS);
		b.setHeight(70, Unit.PIXELS);
		hl.addComponent(b);
	}

	public void showLoginButton(EntityItem<Subscription> item) {
		this.ignoreWarn = false;
		hl.removeAllComponents();
		this.item = item;
		Subscription sub = item.getEntity();
		Button b = new Button(null, this);
		b.setVisible(true);
		b.setPrimaryStyleName(Props.MINIMUMBUTTON);
		b.setWidth(200, Unit.PIXELS);
		b.setHeight(70, Unit.PIXELS);
		if (sub.valid())
			b.setCaption(lang.getText("LogIn"));
		else if (sub instanceof BasicSubscription && minimum.getSettings().is(Flag.USE_BASIC_SUBSCRIPTION))
			b.setCaption(lang.getText("HasPayd", minimum.getSettings().getNormalPrize()));
		else
			b.setVisible(false);
		hl.addComponent(b);

	}

	@Override
	public void buttonClick(ClickEvent event) {
		item.refresh();
		minimum.login(item, ignoreWarn);
	}
}
