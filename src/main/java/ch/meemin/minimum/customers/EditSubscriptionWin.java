package ch.meemin.minimum.customers;

import javax.inject.Inject;

import ch.meemin.minimum.CurrentSettings;
import ch.meemin.minimum.Minimum.SelectEvent;
import ch.meemin.minimum.admin.ValidatePasswordWindow;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.subscriptions.PrepaidSubscription;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.entities.subscriptions.TimeSubscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.SubscriptionProvider;
import ch.meemin.minimum.utils.Props;

import com.vaadin.cdi.UIScoped;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * This is the window used to add new contacts to the 'address book'. It does not do proper validation - you can add
 * weird stuff.
 */
@UIScoped
public class EditSubscriptionWin extends Window {
	private static final long serialVersionUID = 1L;

	@Inject
	private Lang lang;
	@Inject
	private ValidatePasswordWindow pwWin;
	@Inject
	private SubscriptionProvider subProvider;
	@Inject
	private CurrentSettings currSet;
	@Inject
	private javax.enterprise.event.Event<SelectEvent> selectEvent;

	private Button markLostButton;
	private Button cancelButton;
	private Button suspendButton;
	private DateField dateField;
	private TextField integerField;

	private Long id;

	public void show(Long subId) {
		this.id = subId;
		Subscription s = subProvider.getSubscription(subId);
		setCaption(lang.getText("EditSub"));
		setModal(true);
		setSizeUndefined();
		cancelButton = new Button(lang.getText("Cancel"), new CancelClick());
		cancelButton.setClickShortcut(KeyCode.ESCAPE);
		styleButton(cancelButton);
		markLostButton = new Button(lang.getText("MarkLost"), new MarkLostClick());
		styleButton(markLostButton);
		VerticalLayout layout;
		if (s instanceof TimeSubscription) {
			integerField = null;
			TimeSubscription sub = (TimeSubscription) s;
			suspendButton = new Button(lang.getText(sub.isSuspended() ? "Reactivate" : "Suspend"), new SuspendClick());
			styleButton(suspendButton);
			layout = new VerticalLayout(suspendButton, markLostButton, cancelButton);
			if (!sub.isSuspended()) {
				VerticalLayout hl = new VerticalLayout();
				dateField = new DateField();
				dateField.setValue(sub.getExpiry());
				dateField.setStyleName(Props.MINIMUMDATEFIELD);
				dateField.setWidth(250, Unit.PIXELS);
				hl.addComponent(dateField);
				Button mEButton = new Button(lang.getText("modifyExpiry"), new ModifyExpiry());
				styleButton(mEButton);
				hl.addComponent(mEButton);
				layout.addComponent(hl, 1);
			}
		} else if (s instanceof PrepaidSubscription) {
			VerticalLayout hl = new VerticalLayout();
			integerField = new TextField(new ObjectProperty<Integer>(s.getCredit()));
			integerField.setStyleName(Props.MINIMUMTEXTFIELD);
			integerField.setWidth(250, Unit.PIXELS);
			hl.addComponent(integerField);
			Button modifyButton = new Button(lang.getText("modifyCredit"), new ModifyCredit());
			styleButton(modifyButton);
			hl.addComponent(modifyButton);
			layout = new VerticalLayout(markLostButton, hl, cancelButton);
		} else {
			layout = new VerticalLayout(markLostButton, cancelButton);
		}
		layout.setSpacing(true);
		layout.setMargin(true);
		setContent(layout);
		pwWin.show(this);
	}

	private void styleButton(Button button) {
		button.addStyleName(Props.MINIMUMBUTTON);
		button.setWidth(250, Unit.PIXELS);
		button.setHeight(50, Unit.PIXELS);
	}

	@Override
	public void focus() {
		if (dateField != null)
			dateField.focus();
		else
			cancelButton.focus();
	}

	public void fireSelect(Subscription sub) {
		if (currSet.getSettings().is(Flag.SUBSCRIPTIONIDONCARD))
			selectEvent.fire(new SelectEvent(sub.getId()));
		else
			selectEvent.fire(new SelectEvent(sub.getCustomer().getId()));
	}

	private class CancelClick implements ClickListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			EditSubscriptionWin.this.close();
		}
	}

	private class SuspendClick implements ClickListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			fireSelect(subProvider.toggleSuspended(id));
			EditSubscriptionWin.this.close();
		}
	}

	private class MarkLostClick implements ClickListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			fireSelect(subProvider.replace(id));
			EditSubscriptionWin.this.close();
			Notification.show(lang.getText("SubscriptionReplaced"));
		}

	}

	private class ModifyExpiry implements ClickListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			fireSelect(subProvider.updateExpiry(id, dateField.getValue()));
			EditSubscriptionWin.this.close();

		}
	}

	private class ModifyCredit implements ClickListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			fireSelect(subProvider.updateCredit(id, (int) integerField.getConvertedValue()));
			EditSubscriptionWin.this.close();

		}
	}
}
