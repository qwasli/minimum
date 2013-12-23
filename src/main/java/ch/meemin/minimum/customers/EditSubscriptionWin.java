package ch.meemin.minimum.customers;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.admin.ValidatePasswordWindow;
import ch.meemin.minimum.entities.subscriptions.PrepaidSubscription;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.entities.subscriptions.TimeSubscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.utils.Props;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityItemProperty;
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
public class EditSubscriptionWin extends Window {
	private static final long serialVersionUID = 1L;

	private final Minimum minimum;
	private final Lang lang;
	private final Button markLostButton;
	private final Button cancelButton;
	private Button suspendButton;
	private DateField dateField;
	private TextField integerField;

	private EntityItem<Subscription> subItem;

	public EditSubscriptionWin(Minimum minimum, EntityItem<Subscription> subItem) {
		super(minimum.getLang().getText("EditSub"));
		this.minimum = minimum;
		this.lang = minimum.getLang();
		this.subItem = subItem;
		this.subItem.refresh();
		setModal(true);
		setSizeUndefined();
		cancelButton = new Button(lang.getText("Cancel"), new CancelClick());
		cancelButton.setClickShortcut(KeyCode.ESCAPE);
		styleButton(cancelButton);
		markLostButton = new Button(lang.getText("MarkLost"), new MarkLostClick());
		styleButton(markLostButton);
		VerticalLayout layout;
		if (subItem.getEntity() instanceof TimeSubscription) {
			integerField = null;
			TimeSubscription sub = (TimeSubscription) subItem.getEntity();
			suspendButton = new Button(lang.getText(sub.isSuspended() ? "Reactivate" : "Suspend"), new SuspendClick());
			styleButton(suspendButton);
			layout = new VerticalLayout(suspendButton, markLostButton, cancelButton);
			if (!sub.isSuspended()) {
				VerticalLayout hl = new VerticalLayout();
				EntityItemProperty expiry = subItem.getItemProperty("expiry");
				dateField = new DateField(expiry);
				dateField.setStyleName(Props.MINIMUMDATEFIELD);
				dateField.setWidth(250, Unit.PIXELS);
				hl.addComponent(dateField);
				Button mEButton = new Button(lang.getText("modifyExpiry"), new CommitItemClick());
				styleButton(mEButton);
				hl.addComponent(mEButton);
				layout.addComponent(hl, 1);
			}
		} else if (subItem.getEntity() instanceof PrepaidSubscription) {
			VerticalLayout hl = new VerticalLayout();
			EntityItemProperty credit = subItem.getItemProperty("credit");
			integerField = new TextField(credit);
			integerField.setStyleName(Props.MINIMUMTEXTFIELD);
			integerField.setWidth(250, Unit.PIXELS);
			hl.addComponent(integerField);
			Button modifyButton = new Button(lang.getText("modifyCredit"), new CommitItemClick());
			styleButton(modifyButton);
			hl.addComponent(modifyButton);
			layout = new VerticalLayout(markLostButton, hl, cancelButton);
		} else {
			layout = new VerticalLayout(markLostButton, cancelButton);
		}
		layout.setSpacing(true);
		layout.setMargin(true);
		setContent(layout);
		new ValidatePasswordWindow(minimum, this);
	}

	private void styleButton(Button button) {
		button.setPrimaryStyleName(Props.MINIMUMBUTTON);
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

	private class CancelClick implements ClickListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			subItem.refresh();
			EditSubscriptionWin.this.close();
		}
	}

	private class SuspendClick implements ClickListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			TimeSubscription sub = (TimeSubscription) subItem.getEntity();
			if (sub.isSuspended())
				sub.reactivate();
			else
				sub.suspend();
			sub = (TimeSubscription) minimum.getSubscriptionProvider().updateEntity(sub);
			minimum.selectSubscription(sub.getId(), true);
			EditSubscriptionWin.this.close();
		}
	}

	private class MarkLostClick implements ClickListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			Subscription sub = subItem.getEntity();
			sub.replace();
			sub = minimum.getSubscriptionProvider().updateEntity(sub);
			minimum.selectSubscription(sub.getReplacedBy().getId(), true);
			// subItem.refresh();
			// minimum.getCustomerContainer().refreshItem(sub.getCustomer().getId());
			EditSubscriptionWin.this.close();
			Notification.show(lang.getText("SubscriptionReplaced"));
		}
	}

	private class CommitItemClick implements ClickListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			subItem.commit();
			subItem.getContainer().commit();
			subItem.refresh();
			EditSubscriptionWin.this.close();

		}
	}
}
