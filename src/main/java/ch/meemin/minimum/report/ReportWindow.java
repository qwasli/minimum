package ch.meemin.minimum.report;

import javax.inject.Inject;

import ch.meemin.minimum.lang.Lang;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@UIScoped
public class ReportWindow extends Window {
	private TabSheet tabs = new TabSheet();

	@Inject
	private SubscriptionReport subReport;
	@Inject
	private SubscriptionSaleReport saleReport;
	@Inject
	private VisitReport visitReport;

	@Inject
	private Lang lang;

	public void show() {

		setSizeUndefined();
		setContent(tabs);
		setCaption(lang.get("Reports"));
		center();

		tabs.setSizeUndefined();

		tabs.addTab(subReport);
		tabs.addTab(saleReport);
		tabs.addTab(visitReport);

		UI.getCurrent().addWindow(this);

	}

}
