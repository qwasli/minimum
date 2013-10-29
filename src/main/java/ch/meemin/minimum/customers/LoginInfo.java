package ch.meemin.minimum.customers;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.utils.Props;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

public class LoginInfo extends CustomComponent implements ClickListener {
	HorizontalLayout hl = new HorizontalLayout();
	private final Minimum minimum;
	private final Lang lang;

	private EntityItem<Subscription> item;
	private boolean ignoreWarn;

	public LoginInfo(Lang lang, Minimum minimum) {
		this.lang = lang;
		this.minimum = minimum;
		hl.setSizeFull();
		setCompositionRoot(hl);
	}

	public void show(Image icon, String info, boolean clear) {
		if (clear)
			hl.removeAllComponents();
		hl.addComponent(icon);
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
		b.setPrimaryStyleName(Props.MINIMUMBUTTON);
		b.setWidth(200, Unit.PIXELS);
		b.setHeight(70, Unit.PIXELS);
		if (sub.valid())
			b.setCaption(lang.getText("LogIn"));
		else
			b.setCaption(lang.getText("HasPayd", minimum.getSettings().getNormalPrize()));

		hl.addComponent(b);

	}

	@Override
	public void buttonClick(ClickEvent event) {
		item.refresh();
		minimum.login(item, ignoreWarn);
	}
}
