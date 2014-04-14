package ch.meemin.minimum.report;

import lombok.AllArgsConstructor;

import org.joda.time.DateTime;
import org.joda.time.Years;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.lang.Lang;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.themes.Reindeer;

@AllArgsConstructor
public class SubscriptionPopupContent implements PopupView.Content {
	private Lang lang;
	private Subscription sub;

	@Override
	public String getMinimizedValueAsHTML() {
		return lang.get("info");
	}

	@Override
	public Component getPopupComponent() {
		Minimum min = Minimum.getCurrent();
		GridLayout gl = new GridLayout();
		gl.setMargin(true);
		gl.setSpacing(true);
		gl.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		gl.setColumns(2);

		gl.addComponent(createHeader(lang.get("SoldDate")));
		gl.addComponent(new Label(lang.formatDate(sub.getCreatedAt(), true)));

		Customer customer = sub.getCustomer();

		gl.addComponent(createHeader(lang.get("Customer")));
		gl.addComponent(new Label(customer.getName()));

		if (min.getSettings().is(Flag.USE_BIRTHDAY) && customer.getBirthDate() != null) {
			Years age = Years.yearsBetween(new DateTime(customer.getBirthDate()), new DateTime(sub.getCreatedAt()));
			gl.addComponent(createHeader(lang.get("AgeAtSoldDate")));
			gl.addComponent(new Label(new Label(lang.getText("NumYears", new Integer(age.getYears())))));
		}

		return gl;
	}

	private Label createHeader(String text) {
		Label header = new Label(text);
		header.addStyleName(Reindeer.LABEL_H2);
		return header;
	}
}
