package ch.meemin.minimum.admin;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.AllArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import ch.meemin.minimum.CurrentSettings;
import ch.meemin.minimum.lang.Lang;

import com.vaadin.cdi.UIScoped;
import com.vaadin.data.Validator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@UIScoped
public class ValidatePasswordWindow extends Window {
	@Inject
	private Lang lang;
	@Inject
	private CurrentSettings currSet;
	private PasswordField pwField;

	@PostConstruct
	public void init() {
		setModal(true);
		setClosable(false);
		setResizable(false);
		pwField = new PasswordField(lang.getText("Password"));
	}

	public void show(final PasswordValidationListener pvl) {

		if (StringUtils.isBlank(currSet.getSettings().getAdminPassword())) {
			pvl.passwordValidated();
			return;
		}
		pwField.setValue("");
		pwField.setRequired(true);
		pwField.setImmediate(false);
		pwField.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (!ObjectUtils.equals(currSet.getSettings().getAdminPassword(), value))
					throw new InvalidValueException(lang.getText("WrongPassword"));
			}
		});
		VerticalLayout vl = new VerticalLayout();
		HorizontalLayout hl = new HorizontalLayout();
		Button okButton = new Button(lang.getText("OK"), new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (pwField.isValid()) {
					pvl.passwordValidated();
					close();
				}
			}
		});
		okButton.setClickShortcut(KeyCode.ENTER);
		Button cancelButton = new Button(lang.getText("Cancel"), new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				pvl.passwordNotValidated();
				close();
			}
		});
		cancelButton.setClickShortcut(KeyCode.ESCAPE);
		hl.addComponents(okButton, cancelButton);
		vl.addComponents(pwField, hl);
		setContent(vl);
		pwField.focus();
		UI.getCurrent().addWindow(this);
		center();
	}

	public void show(Window windowToOpen) {
		show(new WindowOpen(windowToOpen));

	}

	public interface PasswordValidationListener {
		public void passwordValidated();

		public void passwordNotValidated();
	}

	@AllArgsConstructor
	private static class WindowOpen implements PasswordValidationListener {
		private Window windowToOpen;

		@Override
		public void passwordNotValidated() {}

		@Override
		public void passwordValidated() {
			UI.getCurrent().addWindow(windowToOpen);
			windowToOpen.focus();
		}
	}
}
