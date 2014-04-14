package ch.meemin.minimum.report;

import ch.meemin.minimum.Minimum;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;

public class ReportWindow extends Window {
	private TabSheet tabs = new TabSheet();

	public ReportWindow() {
		super();
		Minimum min = Minimum.getCurrent();

		setSizeUndefined();
		setContent(tabs);
		setCaption(min.getLang().getText("Reports"));
		center();

		tabs.setSizeUndefined();

		tabs.addTab(new SubscriptionReport());
		tabs.addTab(new SubscriptionSaleReport());
		tabs.addTab(new VisitReport());

		min.addWindow(this);

	}

}
