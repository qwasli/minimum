package ch.meemin.minimum.customers;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.AbstractEntity;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.pdf.PdfButton;
import ch.meemin.minimum.pdf.PdfButton.Style;
import ch.meemin.minimum.pdf.PdfCreator;
import ch.meemin.minimum.utils.PrettyTimeConverter;
import ch.meemin.minimum.utils.Props;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class ShowCustomer extends CustomComponent implements ValueChangeListener {
	private static final Logger LOG = LoggerFactory.getLogger(ShowCustomer.class);

	private final Lang lang;
	final Minimum minimum;
	protected Label titel = new Label();
	protected GridLayout infoGrid;
	protected VerticalLayout infoLayout;
	protected PhotoComponent photoComponent;
	protected HorizontalLayout buttons = new HorizontalLayout();

	protected HorizontalLayout infoAndPhoto;

	EntityItem<Customer> customerItem;

	public ShowCustomer(Lang lang, Minimum minimum, SubscriptionInfo subscriptionInfo, LoginInfo loginInfo) {
		this.lang = lang;
		this.minimum = minimum;
		setSizeFull();

		titel.setStyleName(Reindeer.LABEL_H1);

		infoGrid = new GridLayout();
		infoGrid.setSizeUndefined();
		// infoGrid.setWidth(100, Unit.PERCENTAGE);
		infoGrid.setColumns(2);
		infoGrid.setColumnExpandRatio(0, 1f);
		infoGrid.setColumnExpandRatio(1, 999f);
		infoGrid.setSpacing(true);

		Label spacer = new Label();
		buttons.setSpacing(true);
		infoLayout = new VerticalLayout(titel, buttons, subscriptionInfo, spacer, loginInfo);
		// infoLayout.setSpacing(true);
		infoLayout.setExpandRatio(titel, 0);
		infoLayout.setExpandRatio(buttons, 0);
		infoLayout.setExpandRatio(subscriptionInfo, 0);
		infoLayout.setExpandRatio(spacer, 1);
		infoLayout.setExpandRatio(loginInfo, 0);
		infoLayout.setSizeUndefined();
		infoLayout.setHeight(100, Unit.PERCENTAGE);

		photoComponent = new PhotoComponent(lang);

		infoAndPhoto = new HorizontalLayout(infoLayout, photoComponent);
		infoAndPhoto.setExpandRatio(photoComponent, 1f);
		infoAndPhoto.setSizeFull();

		setCompositionRoot(infoAndPhoto);

	}

	protected void resetAndAddTitle(String title, Image image) {
		this.titel.setValue(title);
		infoGrid.removeAllComponents();
	}

	protected void addDate(AbstractEntity entity) {
		Label label;
		label = new Label(" @ " + lang.formatDate(entity.getCreatedAt()));
		label.setStyleName(Reindeer.LABEL_SMALL);
		label.setSizeUndefined();
		int r = infoGrid.getRows();
		infoGrid.setRows(r + 1);
		infoGrid.addComponent(label, 0, r, 1, r);
	}

	protected void addInfo(String infoTitle, Component info) {
		if (!StringUtils.isBlank(infoTitle)) {
			Label label;
			label = new Label(infoTitle);
			label.setStyleName(Reindeer.LABEL_H2);
			label.setSizeUndefined();
			infoGrid.addComponent(label);
		} else
			infoGrid.space();
		infoGrid.addComponent(info);
	}

	public void setCustomer(EntityItem<Customer> customerItem) {
		if (this.customerItem != null)
			customerItem.removeValueChangeListener(this);
		this.customerItem = customerItem;
		this.photoComponent.setCustomer(customerItem);
		customerItem.addValueChangeListener(this);
		refresh();

	}

	private void refresh() {
		Customer customer = customerItem.getEntity();

		resetAndAddTitle(customer.getName(), customer.getImage());

		if (minimum.getSettings().is(Flag.USE_BIRTHDAY) && customer.getBirthDate() != null) {
			Years age = Years.yearsBetween(new DateTime(customer.getBirthDate()), new DateTime());
			addInfo(lang.getText("Age"), new Label(lang.getText("NumYears", new Integer(age.getYears()))));
		}

		if (minimum.getSettings().is(Flag.USE_STUDENT)) {
			addInfo(lang.getText("student"), new Label(lang.getText(customer.isStudent() ? "Yes" : "No")));
		}

		addInfo(lang.getText("LastVisit"), createLastUsedLabel(customerItem.getItemProperty("lastVisit.createdAt")));

		buttons.removeAllComponents();
		buttons.addComponent(infoGrid);
		Button button = new PdfButton(PdfCreator.getCustomerInfoDownloader(minimum, customer.getId()), lang.getText("PDF"),
				Style.NORMAL);

		button.setPrimaryStyleName(Props.MINIMUMBUTTON);
		button.setHeight(100, Unit.PERCENTAGE);
		button.setHeight(35, Unit.PIXELS);

		buttons.addComponent(button);

		button = new Button(lang.getText("Edit"), new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				new EditCustomerWin(getUI(), customerItem);
			}
		});
		button.setPrimaryStyleName(Props.MINIMUMBUTTON);
		button.setHeight(100, Unit.PERCENTAGE);
		button.setHeight(35, Unit.PIXELS);
		buttons.addComponent(button);

	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		refresh();
	}

	private Label createLastUsedLabel(Property<Date> prop) {
		Label l = new Label();
		l.setLocale(getUI().getLocale());
		l.setConverter(new PrettyTimeConverter(lang.getText("NotYetVisited")));
		l.setPropertyDataSource(prop);
		return l;
	}

}
