package ch.meemin.minimum.report;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.vaadin.maddon.label.Header;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.Visit;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.VisitProvider;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.data.util.filter.Compare.LessOrEqual;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

public class VisitReport extends CustomComponent implements ValueChangeListener {

	VerticalLayout layout = new VerticalLayout();
	VisitProvider visitProvider;
	DateField from, to;
	Filter fromFilter, toFilter;
	JPAContainer<Visit> visitContainer;

	Table countTable = new Table();
	private Lang lang;

	public VisitReport() {
		Minimum min = Minimum.getCurrent();
		this.lang = min.getLang();

		setSizeUndefined();
		setCaption(lang.get("Visits"));

		visitContainer = new JPAContainer(Visit.class);
		visitProvider = min.getVisitProvider();
		visitContainer.setEntityProvider(visitProvider);
		visitContainer.addNestedContainerProperty("subscription.typeName");

		layout.addComponent(new Header(lang.get("DateRange")).setHeaderLevel(2));

		from = new DateField(lang.get("From"));
		from.addValueChangeListener(this);
		from.setImmediate(true);
		to = new DateField(lang.get("To"));
		to.addValueChangeListener(this);
		to.setImmediate(true);

		HorizontalLayout hl = new HorizontalLayout(from, to);
		layout.addComponent(hl);

		hl = new HorizontalLayout();
		addVisitTable(hl);

		countTable.addContainerProperty("name", String.class, null);
		countTable.addContainerProperty("count", Long.class, 0);
		countTable.setColumnHeaders(lang.get("Subscription"), lang.get("count"));
		hl.addComponent(countTable);
		fillCountTable();

		layout.addComponent(new Header(lang.get("Visits")).setHeaderLevel(2));
		layout.addComponent(hl);
		setCompositionRoot(layout);
	}

	private void addVisitTable(Layout layout) {

		Table table = new Table(null, visitContainer);
		table.setVisibleColumns("subscription.typeName", "createdAt");
		table.setSortContainerPropertyId("subscription.typeName");
		table.setSortContainerPropertyId("createdAt");
		table.setSortAscending(false);

		table.addGeneratedColumn("info", new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				PopupView pop = new PopupView(new SubscriptionPopupContent(lang, (((EntityItem<Visit>) source.getItem(itemId))
						.getEntity()).getSubscription()));
				return pop;
			}
		});

		table.setColumnHeaders(lang.get("Subscription"), lang.get("Date"), "");
		layout.addComponent(table);
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		if (fromFilter != null)
			visitContainer.removeContainerFilter(fromFilter);
		if (toFilter != null)
			visitContainer.removeContainerFilter(toFilter);

		if (from.getValue() != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(from.getValue());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);

			fromFilter = new GreaterOrEqual("createdAt", cal.getTime());
			visitContainer.addContainerFilter(fromFilter);
		}
		if (to.getValue() != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(to.getValue());
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);

			toFilter = new LessOrEqual("createdAt", cal.getTime());
			visitContainer.addContainerFilter(toFilter);
		}
		fillCountTable();
	}

	private void fillCountTable() {
		Map<String, Long> counts = visitProvider.countVisitsSubscriptionType(from.getValue(), to.getValue());
		countTable.removeAllItems();
		for (Map.Entry<String, Long> ent : counts.entrySet()) {
			Item i = countTable.addItem(ent.getKey());
			i.getItemProperty("name").setValue(ent.getKey());
			i.getItemProperty("count").setValue(ent.getValue());
		}
		Item i = countTable.addItem("TOTAL");
		i.getItemProperty("name").setValue(lang.get("Total"));
		i.getItemProperty("count").setValue(new Long(visitContainer.size()));

	}

}
