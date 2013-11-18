package ch.meemin.minimum.admin;

import java.util.EnumSet;
import java.util.List;

import ch.meemin.minimum.entities.settings.Duration.TimeSpan;
import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.settings.Subscriptions;
import ch.meemin.minimum.lang.Lang;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

public class EditSubscriptionsField<T extends Subscriptions> extends CustomField<List> {

	private final Lang lang;

	protected Table table;
	private final VerticalLayout layout;

	private final Class<T> clazz;

	EditSubscriptionsField(Lang lang, final Class<T> clazz) {
		this.clazz = clazz;
		this.lang = lang;
		layout = new VerticalLayout();
		layout.setSizeFull();
		BeanItemContainer<T> dataSource = new BeanItemContainer<>(clazz);
		prepareTable(lang, clazz, dataSource);
	}

	protected void prepareTable(Lang lang, final Class<T> clazz, BeanItemContainer<T> dataSource) {
		table = new Table("", dataSource);
		table.setWidth(100, Unit.PERCENTAGE);
		table.setColumnCollapsingAllowed(true);
		table.setPageLength(5);
		table.addGeneratedColumn("remove", new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				return new Button("X", new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						getValue().remove(itemId);
						table.removeItem(itemId);
					}
				});
			}
		});
		table.setTableFieldFactory(new SubScriptionsFieldFactory());
		table.setEditable(true);
	}

	@Override
	protected Component initContent() {
		layout.addComponent(table);
		layout.setExpandRatio(table, 1f);
		layout.addComponent(new Button(lang.getText("Add"), new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					T t = clazz.newInstance();
					getValue().add(t);
					table.addItem(t);
				} catch (UnsupportedOperationException | InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}));
		return layout;
	}

	@Override
	protected void setInternalValue(List newValue) {
		table.removeAllItems();
		for (T t : (List<T>) newValue) {
			table.addItem(t);
		}
		super.setInternalValue(newValue);
	}

	@Override
	public List<T> getValue() {
		return super.getValue();
	}

	@Override
	public java.lang.Class<? extends List> getType() {
		return List.class;
	};

	private class SubScriptionsFieldFactory extends DefaultFieldFactory {
		@Override
		public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
			if (propertyId.equals("duration.timeSpan")) {
				NativeSelect timeSpanSelect = new NativeSelect();
				for (TimeSpan ts : EnumSet.allOf(TimeSpan.class)) {
					timeSpanSelect.addItem(ts);
					timeSpanSelect.setItemCaption(ts, lang.getText(ts.name()));
				}
				timeSpanSelect.setNullSelectionAllowed(false);
				return timeSpanSelect;
			}
			AbstractTextField field = (AbstractTextField) super.createField(container, itemId, propertyId, uiContext);
			field.setNullRepresentation("");
			field.setWidth(80, Unit.PIXELS);
			return field;
		}
	}

	protected void setVisibleColumns(String... cols) {
		Object[] vCols = new Object[cols.length + 6];
		String[] cHeads = new String[cols.length + 6];
		vCols[0] = "name";
		cHeads[0] = lang.getText("name");
		int i = 1;
		while (i <= cols.length) {
			vCols[i] = cols[i - 1];
			cHeads[i] = lang.getText(cols[i - 1]);
			i++;
		}
		vCols[i] = "normalPrize";
		cHeads[i++] = lang.getText("normalPrize");
		vCols[i] = "studentPrize";
		cHeads[i++] = lang.getText("studentPrize");
		vCols[i] = "childrenPrize";
		cHeads[i++] = lang.getText("childrenPrize");
		vCols[i] = "underAgePrize";
		cHeads[i++] = lang.getText("underAgePrize");
		vCols[i] = "seniorPrize";
		cHeads[i++] = lang.getText("seniorPrize");
		table.setVisibleColumns(vCols);
		table.setColumnHeaders(cHeads);
	}

	public void collapsColumns(Settings settings) {
		if (!settings.is(Flag.USE_STUDENT)) {
			table.setColumnCollapsed("studentPrize", true);
		}
		if (!settings.is(Flag.USE_BIRTHDAY)) {
			table.setColumnCollapsed("childrenPrize", true);
			table.setColumnCollapsed("underAgePrize", true);
			table.setColumnCollapsed("seniorPrize", true);
		} else {

			table.setColumnCollapsed("childrenPrize", settings.getChildAgeLimit() == null);
			table.setColumnCollapsed("underAgePrize", settings.getUnderAgeLimit() == null);
			table.setColumnCollapsed("seniorPrize", settings.getSeniorAgeLimit() == null);
		}
	}
}
