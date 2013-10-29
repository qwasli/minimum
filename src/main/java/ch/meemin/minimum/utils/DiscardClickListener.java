package ch.meemin.minimum.utils;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class DiscardClickListener implements ClickListener {
	private static final long serialVersionUID = 5233498336109646834L;
	private FieldGroup fieldGroup;

	public DiscardClickListener(FieldGroup fieldGroup) {
		this.fieldGroup = fieldGroup;
	}

	@Override
	public void buttonClick(ClickEvent event) {
			fieldGroup.discard();
	}
}
