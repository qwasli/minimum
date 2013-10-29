package ch.meemin.minimum.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class CommitClickListener implements ClickListener {
	private static final long serialVersionUID = 109646835L;
	private static final Logger LOG = LoggerFactory
			.getLogger(CommitClickListener.class);

	private FieldGroup fieldGroup;

	public CommitClickListener(FieldGroup fieldGroup) {
		this.fieldGroup = fieldGroup;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		try {
			fieldGroup.commit();
		} catch (CommitException e) {
			LOG.warn("", e);
		}
	}

}
