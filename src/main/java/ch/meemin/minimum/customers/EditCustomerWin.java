package ch.meemin.minimum.customers;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.admin.ValidatePasswordWindow;
import ch.meemin.minimum.admin.ValidatePasswordWindow.PasswordValidationListener;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.utils.CommitClickListener;
import ch.meemin.minimum.utils.DiscardClickListener;
import ch.meemin.minimum.utils.EntityFieldGroup;
import ch.meemin.minimum.utils.FormLayout;
import ch.meemin.minimum.utils.Props;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * This is the window used to add new contacts to the 'address book'. It does not do proper validation - you can add
 * weird stuff.
 */
public class EditCustomerWin extends Window {
	private static final long serialVersionUID = 1L;

	private JPAContainer<Customer> container;
	private Lang lang;
	private DateField dateField;
	private Button okButton;

	private FieldGroup form = new EntityFieldGroup<Customer>(Customer.class) {
		private static final long serialVersionUID = -1026191871445717584L;

		@Override
		public void commit() throws CommitException {
			super.commit();
			EntityItem<Customer> item = (EntityItem<Customer>) getItemDataSource();
			Long id = (Long) item.getItemId();
			if (!item.isPersistent()) {
				id = (Long) container.addEntity(item.getEntity());
				item = container.getItem(id);
			}
			item.commit();
			container.commit();
			Minimum minimum = (Minimum) getUI();
			close();
			if (!minimum.getSettings().is(Flag.USE_BASIC_SUBSCRIPTION) && newCustomer)
				new SellSubscriptionWin(minimum, item);
			else
				minimum.selectCustomer(id, true);

		}

		@Override
		public void discard() throws SourceException {
			super.discard();
			close();
		};
	};

	private boolean newCustomer;

	public EditCustomerWin(UI ui, EntityItem<Customer> item) {
		super();
		Minimum minimum = (Minimum) ui;
		this.lang = minimum.getLang();
		container = minimum.getCustomerContainer();
		setModal(true);
		setSizeUndefined();

		FormLayout formLayout = new FormLayout();
		formLayout.setSizeUndefined();
		formLayout.setDescription(lang.getText("newCustomerFormDescription"));
		form.setFieldFactory(new FieldFactory());

		if (item == null) {
			this.newCustomer = true;
			item = container.createEntityItem(new Customer());

		}
		form.setItemDataSource(item);

		formLayout.addComponent(form.buildAndBind(lang.getText("name"), "name"));
		if (minimum.getSettings().is(Flag.USE_BIRTHDAY)) {
			dateField = form.buildAndBind(lang.getText("birthDate"), "birthDate", DateField.class);
			dateField.setRequired(true);
			formLayout.addComponent(dateField);

		}
		if (minimum.getSettings().is(Flag.USE_STUDENT)) {
			Field<?> studentField = form.buildAndBind(lang.getText("student"), "student");

			final Integer studentAgeLimit = minimum.getSettings().getStudentAgeLimit();
			if (studentAgeLimit != null) {
				studentField.addValueChangeListener(new ValueChangeListener() {
					@Override
					public void valueChange(ValueChangeEvent event) {
						Boolean value = (Boolean) event.getProperty().getValue();
						checkStudentAge(studentAgeLimit, value);
					}

				});
				checkStudentAge(studentAgeLimit, (Boolean) studentField.getValue());
			}
			formLayout.addComponent(studentField);
		}
		Field<?> mailField = form.buildAndBind(lang.getText("email"), "email");
		mailField.setRequired(minimum.getSettings().is(Flag.REQUIREEMAIL));

		formLayout.addComponent(mailField);
		formLayout.addComponent(form.buildAndBind(lang.getText("phone"), "phone"));
		formLayout.addComponent(form.buildAndBind(lang.getText("address"), "address", TextArea.class));
		if (minimum.getSettings().is(Flag.USE_NEWSLETTER))
			formLayout.addComponent(form.buildAndBind(lang.getText("newsletter"), "newsletter"));

		okButton = new Button(lang.getText((newCustomer) ? "Create" : "OK"), new CommitClickListener(form));
		okButton.setPrimaryStyleName(Props.MINIMUMBUTTON);
		okButton.setClickShortcut(KeyCode.ENTER);
		HorizontalLayout footer = formLayout.createDefaultFooter();
		footer.addComponent(okButton);
		Button cancelButton = new Button(lang.getText("Cancel"), new DiscardClickListener(form));
		cancelButton.setPrimaryStyleName(Props.MINIMUMBUTTON);
		cancelButton.setClickShortcut(KeyCode.ESCAPE);
		footer.addComponent(cancelButton);

		if (!newCustomer) {

			footer.setWidth(100, Unit.PERCENTAGE);
			Label spacer = new Label();
			footer.addComponent(spacer);
			footer.setExpandRatio(spacer, 1f);
			Button deleteButton = new Button(lang.getText("Delete"), new DelCustomerListener(item));
			footer.addComponent(deleteButton);
		}

		setContent(formLayout);

		ui.addWindow(this);
		form.getField("name").focus();
	}

	private void checkStudentAge(final Integer studentAgeLimit, Boolean value) {
		if (value)
			dateField.addValidator(new Validator() {

				@Override
				public void validate(Object value) throws InvalidValueException {
					if (DateUtils.addYears(new Date(), -1 * studentAgeLimit).after((Date) value))
						throw new InvalidValueException(lang.getText("studentLimitWarn"));
				}
			});
		else
			dateField.removeAllValidators();
	}

	private class FieldFactory extends DefaultFieldGroupFieldFactory {
		@Override
		public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {
			if (TextArea.class.isAssignableFrom(fieldType)) {
				TextArea ta = new TextArea();
				ta.setNullRepresentation("");
				ta.addFocusListener(new FocusListener() {
					@Override
					public void focus(FocusEvent event) {
						okButton.removeClickShortcut();
					}
				});
				ta.addBlurListener(new BlurListener() {
					@Override
					public void blur(BlurEvent event) {
						okButton.setClickShortcut(KeyCode.ENTER);
					}
				});
				return (T) ta;
			}
			if (DateField.class.isAssignableFrom(fieldType)) {
				DateField df = new DateField();
				df.setResolution(Resolution.DAY);
				return (T) df;
			}
			return super.createField(type, fieldType);
		}
	}

	private class DelCustomerListener implements Button.ClickListener {
		private EntityItem<Customer> cItem;

		public DelCustomerListener(EntityItem<Customer> cItem) {
			this.cItem = cItem;
		}

		@Override
		public void buttonClick(ClickEvent event) {
			final Minimum minimum = (Minimum) event.getButton().getUI();
			new ValidatePasswordWindow(minimum, new PasswordValidationListener() {

				@Override
				public void passwordValidated() {
					container.removeItem(cItem.getItemId());
					minimum.clear();
					EditCustomerWin.this.close();
				}

				@Override
				public void passwordNotValidated() {}
			});
		}
	}
}
