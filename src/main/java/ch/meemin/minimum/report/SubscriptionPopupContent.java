package ch.meemin.minimum.report;

import javax.inject.Inject;

import lombok.AllArgsConstructor;

import org.joda.time.DateTime;
import org.joda.time.Years;

import ch.meemin.minimum.CurrentSettings;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.SubscriptionProvider;

import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.themes.ValoTheme;

public class SubscriptionPopupContent {
	@Inject
	private Lang lang;
	@Inject
	private SubscriptionProvider subsProvider;
	@Inject
	private CurrentSettings currSet;

	public PopupView.Content get(Long id) {
		return new Content(id);
	}

	@AllArgsConstructor
	private class Content implements PopupView.Content {
		private Long id;

		@Override
		public String getMinimizedValueAsHTML() {
			return lang.get("info");
		}

		@Override
		public Component getPopupComponent() {
			Subscription sub = subsProvider.getSubscription(id);
			FormLayout gl = new FormLayout();
			gl.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);
			gl.setMargin(true);
			gl.setSpacing(true);

			Label l = new Label(lang.formatDate(sub.getCreatedAt(), true));
			l.setCaption(lang.get("SoldDate"));
			gl.addComponent(l);

			Customer customer = sub.getCustomer();

			l = new Label(customer.getName());
			l.setCaption(lang.get("Customer"));
			gl.addComponent(l);

			if (currSet.getSettings().is(Flag.USE_BIRTHDAY) && customer.getBirthDate() != null) {
				Years age = Years.yearsBetween(new DateTime(customer.getBirthDate()), new DateTime(sub.getCreatedAt()));
				l = new Label(lang.getText("NumYears", new Integer(age.getYears())));
				l.setCaption(lang.get("AgeAtSoldDate"));
				gl.addComponent(l);
			}

			return gl;
		}

		private Label createHeader(String text) {
			Label header = new Label(text);
			header.addStyleName(ValoTheme.LABEL_H2);
			return header;
		}
	}
}