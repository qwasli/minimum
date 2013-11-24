package ch.meemin.minimum.admin;

import lombok.AllArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.lang.Lang;

import com.vaadin.data.Validator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ValidatePasswordWindow extends Window {

	public ValidatePasswordWindow(final Minimum minimum, final PasswordValidationListener pvl) {
		setModal(true);
		setClosable(true);

		final Lang lang = minimum.getLang();
		final String pw = minimum.getSettings().getAdminPassword();
		if (StringUtils.isBlank(minimum.getSettings().getAdminPassword())) {
			pvl.passwordValidated();
			return;
		}

		final PasswordField pwField = new PasswordField(lang.getText("Password"));
		pwField.setRequired(true);
		pwField.setImmediate(false);
		pwField.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (!ObjectUtils.equals(pw, value))
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
		minimum.addWindow(this);
	}

	public ValidatePasswordWindow(final Minimum minimum, final Window windowToOpen) {
		this(minimum, new WindowOpen(minimum, windowToOpen));

	}

	public interface PasswordValidationListener {
		public void passwordValidated();

		public void passwordNotValidated();
	}

	@AllArgsConstructor
	private static class WindowOpen implements PasswordValidationListener {
		private Minimum minimum;
		private Window windowToOpen;

		@Override
		public void passwordNotValidated() {}

		@Override
		public void passwordValidated() {
			minimum.addWindow(windowToOpen);
			windowToOpen.focus();
		}
	}
}
