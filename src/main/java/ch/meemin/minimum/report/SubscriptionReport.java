package ch.meemin.minimum.report;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.SubscriptionProvider;

import com.vaadin.data.Item;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class SubscriptionReport extends CustomComponent {

	VerticalLayout layout = new VerticalLayout();
	@Inject
	SubscriptionProvider subsProvider;

	@Inject
	private Lang lang;

	@PostConstruct
	public void init() {
		setSizeUndefined();
		setCaption(lang.get("Subscriptions"));
		Label label = new Label(lang.get("ValidSubscriptions"));
		label.setStyleName(ValoTheme.LABEL_H2);
		layout.addComponent(label);
		HorizontalLayout hl = new HorizontalLayout();
		addTimeSubTable(hl);
		addPrepaidSubTable(hl);
		layout.addComponent(hl);
		setCompositionRoot(layout);
	}

	private void addTimeSubTable(Layout layout) {
		Table countTable = new Table(lang.get("timeSubscriptions"));
		countTable.addContainerProperty("name", String.class, null);
		countTable.addContainerProperty("count", Long.class, 0);
		countTable.setColumnHeaders(lang.get("Subscription"), lang.get("count"));
		countTable.setPageLength(8);
		Map<String, Long> counts = subsProvider.countValidTimesubscriptions();
		for (Map.Entry<String, Long> ent : counts.entrySet()) {
			Item i = countTable.addItem(ent.getKey());
			i.getItemProperty("name").setValue(ent.getKey());
			i.getItemProperty("count").setValue(ent.getValue());
		}
		layout.addComponent(countTable);
	}

	private void addPrepaidSubTable(Layout layout) {
		Table countTable = new Table(lang.get("prepaidSubscriptions"));
		countTable.addContainerProperty("name", String.class, null);
		countTable.addContainerProperty("count", Long.class, 0);
		countTable.setColumnHeaders(lang.get("Subscription"), lang.get("count"));
		countTable.setPageLength(8);
		Map<String, Long> counts = subsProvider.countValidPrepaidSubscriptions();
		for (Map.Entry<String, Long> ent : counts.entrySet()) {
			Item i = countTable.addItem(ent.getKey());
			i.getItemProperty("name").setValue(ent.getKey());
			i.getItemProperty("count").setValue(ent.getValue());
		}
		layout.addComponent(countTable);
	}
}
