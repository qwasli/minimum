package ch.meemin.minimum.customers;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.lang.Lang;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

public class AllCustomers extends CustomComponent {
	JPAContainer<Customer> container;
	private Lang lang;
	private ShowCustomer showCustomer;
	private Table table;

	public AllCustomers(Minimum minimum, JPAContainer<Customer> container) {
		this.lang = minimum.getLang();
		this.container = container;
		table = new Table("Users", container);
		table.setVisibleColumns(new String[] { "name", "email", "currentSubscription" });
		table.addGeneratedColumn("currentSubscription", new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				return lang.getText(source.getItem(itemId).getItemProperty(columnId).getValue().getClass().getSimpleName());
			}
		});
		table.setSizeFull();
		table.setImmediate(true);
		table.setSelectable(true);
		table.addValueChangeListener(new SelectUser());
		table.setPageLength(5);
		setCompositionRoot(table);
	}

	public void setFilter(String filter) {
		container.removeAllContainerFilters();
		container.addContainerFilter("name", filter, true, false);
		table.select(container.firstItemId());
	}

	public void clearFilter() {
		container.removeAllContainerFilters();
	}

	private class SelectUser implements ValueChangeListener {
		@Override
		public void valueChange(ValueChangeEvent event) {
			Long id = (Long) table.getValue();
			if (id != null) {
				Minimum minimum = (Minimum) getUI();
				minimum.selectCustomer(id, true);
			}
			table.setValue(id);

		}
	}

}
