package ch.meemin.minimum.report;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.provider.SubscriptionProvider;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.util.DefaultQueryModifierDelegate;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.data.util.filter.Compare.LessOrEqual;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Not;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class SubscriptionSaleReport extends CustomComponent implements ValueChangeListener {

	VerticalLayout layout = new VerticalLayout();
	@Inject
	SubscriptionProvider subsProvider;
	@Inject
	SubscriptionPopupContent popupContent;
	JPAContainer<Subscription> soldContainer;
	DateField from, to;
	Filter fromFilter, toFilter;

	Table countTable = new Table();
	private Lang lang;

	@PostConstruct
	public void init() {
		Minimum min = Minimum.getCurrent();
		this.lang = min.getLang();

		soldContainer = new JPAContainer(Subscription.class);
		soldContainer.setEntityProvider(subsProvider);

		setSizeUndefined();
		setCaption(lang.get("Sales"));

		Label label = new Label(lang.get("DateRange"));
		label.setStyleName(ValoTheme.LABEL_H2);
		layout.addComponent(label);

		from = new DateField(lang.get("From"));
		from.addValueChangeListener(this);
		from.setImmediate(true);
		to = new DateField(lang.get("To"));
		to.addValueChangeListener(this);
		to.setImmediate(true);

		HorizontalLayout hl = new HorizontalLayout(from, to);
		layout.addComponent(hl);

		hl = new HorizontalLayout();
		addSoldSubscriptions(hl);

		countTable.addContainerProperty("name", String.class, null);
		countTable.addContainerProperty("count", Long.class, 0);
		countTable.setColumnHeaders(lang.get("Subscription"), lang.get("count"));
		countTable.setPageLength(8);
		hl.addComponent(countTable);
		fillCountTable();
		label = new Label(lang.get("SoldSubscriptions"));
		label.setStyleName(ValoTheme.LABEL_H2);
		layout.addComponent(label);
		layout.addComponent(hl);
		setCompositionRoot(layout);
	}

	private void addSoldSubscriptions(Layout layout) {

		soldContainer.addContainerFilter(new Not(new IsNull("typeName")));
		soldContainer.setQueryModifierDelegate(new DefaultQueryModifierDelegate() {
			@Override
			public void filtersWillBeAdded(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query, List<Predicate> predicates) {
				query.distinct(true);
				Root<Subscription> subRoot = (Root<Subscription>) query.getRoots().iterator().next();
				Subquery<Subscription> subquery = query.subquery(Subscription.class);
				subquery.distinct(true);
				Root<Subscription> subRootEntity = subquery.from(Subscription.class);
				subquery.where(criteriaBuilder.equal(subRootEntity.get("replacedBy"), subRoot));
				predicates.add(criteriaBuilder.not(criteriaBuilder.exists(subquery)));
			}
		});
		// subsContainer.addContainerFilter(new IsNull("replacing"));

		Table table = new Table(null, soldContainer);
		table.setVisibleColumns("typeName", "createdAt");
		table.setSortContainerPropertyId("createdAt");
		table.setSortAscending(false);
		table.setPageLength(8);

		table.addGeneratedColumn("info", new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				return new PopupView(popupContent.get((Long) itemId));
			}
		});

		table.setColumnHeaders(lang.get("Subscription"), lang.get("Date"), "");
		layout.addComponent(table);
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		if (fromFilter != null)
			soldContainer.removeContainerFilter(fromFilter);
		if (toFilter != null)
			soldContainer.removeContainerFilter(toFilter);

		if (from.getValue() != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(from.getValue());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);

			fromFilter = new GreaterOrEqual("createdAt", cal.getTime());
			soldContainer.addContainerFilter(fromFilter);
		}
		if (to.getValue() != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(to.getValue());
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);

			toFilter = new LessOrEqual("createdAt", cal.getTime());
			soldContainer.addContainerFilter(toFilter);
		}
		fillCountTable();
	}

	private void fillCountTable() {
		Map<String, Long> counts = subsProvider.countSoldSubscriptionsByType(from.getValue(), to.getValue());
		countTable.removeAllItems();
		for (Map.Entry<String, Long> ent : counts.entrySet()) {
			Item i = countTable.addItem(ent.getKey());
			i.getItemProperty("name").setValue(ent.getKey());
			i.getItemProperty("count").setValue(ent.getValue());
		}
		Item i = countTable.addItem("TOTAL");
		i.getItemProperty("name").setValue(lang.get("Total"));
		i.getItemProperty("count").setValue(new Long(soldContainer.size()));

	}

}
