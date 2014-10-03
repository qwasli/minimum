package ch.meemin.minimum.customers;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Years;

import ch.meemin.minimum.CurrentSettings;
import ch.meemin.minimum.Minimum.LoggedInEvent;
import ch.meemin.minimum.Minimum.SelectEvent;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.pdf.CustomerInfoPDF;
import ch.meemin.minimum.provider.CustomerProvider;
import ch.meemin.minimum.provider.SubscriptionProvider;
import ch.meemin.minimum.utils.Props;

import com.ocpsoft.pretty.time.PrettyTime;
import com.vaadin.cdi.UIScoped;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@UIScoped
public class ShowCustomer extends HorizontalLayout {
	@Inject
	private Lang lang;
	@Inject
	private CurrentSettings currentSettings;
	@Inject
	private SubscriptionProvider subsProvider;
	@Inject
	private CustomerProvider custProvider;
	@Inject
	private LoginInfo loginInfo;
	@Inject
	private SubscriptionInfo subscriptionInfo;
	@Inject
	protected PhotoComponent photoComponent;
	@Inject
	private EditCustomerWin editCustomerWin;
	@Inject
	private CustomerInfoPDF pdf;

	protected Label titel = new Label();
	protected FormLayout infoGrid = new FormLayout();
	protected VerticalLayout infoLayout;
	protected HorizontalLayout titelNbuttons = new HorizontalLayout();

	protected Button pdfButton, editButton;
	PrettyTime prettyTime;
	private Long id;

	@PostConstruct
	public void init() {
		setSizeFull();

		prettyTime = new PrettyTime(lang.getLocale());

		titel.setStyleName(ValoTheme.LABEL_H2);

		pdfButton = new Button(lang.getText("PDF"), FontAwesome.FILE_PDF_O);
		pdfButton.addStyleName(Props.MINIMUMBUTTON);
		pdfButton.setHeight(50, Unit.PIXELS);
		pdf.getCustomerInfoDownloader().extend(pdfButton);

		editButton = new Button(lang.getText("Edit"), new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				editCustomerWin.show(id);
			}
		});

		editButton.addStyleName(Props.MINIMUMBUTTON);
		editButton.setHeight(50, Unit.PIXELS);

		titelNbuttons.setSpacing(true);

		infoGrid.setSizeUndefined();
		infoGrid.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

		infoLayout = new VerticalLayout(titelNbuttons, infoGrid, subscriptionInfo, loginInfo);
		infoLayout.setSpacing(true);
		infoLayout.setExpandRatio(infoGrid, 0);
		infoLayout.setExpandRatio(titelNbuttons, 0);
		infoLayout.setExpandRatio(subscriptionInfo, 0);
		infoLayout.setExpandRatio(loginInfo, 1);
		infoLayout.setSizeUndefined();
		infoLayout.setHeight(100, Unit.PERCENTAGE);

		addComponents(infoLayout, photoComponent);
		setExpandRatio(photoComponent, 1f);
		setSizeFull();

	}

	protected void addInfo(String infoTitle, Component info) {
		if (StringUtils.isNotBlank(infoTitle))
			info.setCaption(infoTitle);
		infoGrid.addComponent(info);
	}

	public void clear() {
		this.id = null;
		infoGrid.removeAllComponents();
		titelNbuttons.removeAllComponents();
		titel.setValue("");
		photoComponent.clear();
	}

	public void setCustomer(@Observes SelectEvent event) {
		if (event.isClear()) {
			clear();
			return;
		}
		id = event.getId();
		if (currentSettings.getSettings().is(Flag.SUBSCRIPTIONIDONCARD)) {
			Subscription sub = subsProvider.getSubscription(id);
			if (sub == null) {
				clear();
				return;
			} else
				id = sub.getCustomer().getId();
		}
		refresh();

	}

	public void hasLoggedIn(@Observes LoggedInEvent event) {
		refresh();
	}

	private void refresh() {
		if (id == null) {
			clear();
			return;
		}
		Customer customer = custProvider.getCustomer(id);
		titel.setValue(customer.getName());

		if (titelNbuttons.getComponentCount() == 0)
			titelNbuttons.addComponents(titel, pdfButton, editButton);

		infoGrid.removeAllComponents();

		if (currentSettings.getSettings().is(Flag.USE_BIRTHDAY) && customer.getBirthDate() != null) {
			Years age = Years.yearsBetween(new DateTime(customer.getBirthDate()), new DateTime());
			addInfo(lang.getText("Age"), new Label(lang.getText("NumYears", new Integer(age.getYears()))));
		}

		if (currentSettings.getSettings().is(Flag.USE_STUDENT)) {
			addInfo(lang.getText("student"), new Label(lang.getText(customer.isStudent() ? "Yes" : "No")));
		}
		if (customer.getLastVisit() != null)
			addInfo(lang.getText("LastVisit"), new Label(prettyTime.format(customer.getLastVisit().getCreatedAt())));
	}

}
