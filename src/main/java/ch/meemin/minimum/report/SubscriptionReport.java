package ch.meemin.minimum.report;

import java.util.Map;

import org.vaadin.maddon.label.Header;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.SubscriptionProvider;

import com.vaadin.data.Item;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class SubscriptionReport extends CustomComponent {

	VerticalLayout layout = new VerticalLayout();
	SubscriptionProvider subsProvider;

	private Lang lang;

	public SubscriptionReport() {
		Minimum min = Minimum.getCurrent();
		this.lang = min.getLang();

		subsProvider = min.getSubscriptionProvider();

		setSizeUndefined();
		setCaption(lang.get("Subscriptions"));
		layout.addComponent(new Header(lang.get("ValidSubscriptions")).setHeaderLevel(3));
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
		Map<String, Long> counts = subsProvider.countValidPrepaidSubscriptions();
		for (Map.Entry<String, Long> ent : counts.entrySet()) {
			Item i = countTable.addItem(ent.getKey());
			i.getItemProperty("name").setValue(ent.getKey());
			i.getItemProperty("count").setValue(ent.getValue());
		}
		layout.addComponent(countTable);
	}
}
