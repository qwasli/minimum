package ch.meemin.minimum.admin;

import ch.meemin.minimum.entities.settings.TimeSubscriptions;
import ch.meemin.minimum.lang.Lang;

import com.vaadin.data.util.BeanItemContainer;

public class EditTimeSubscriptionsField extends EditSubscriptionsField<TimeSubscriptions> {
	public EditTimeSubscriptionsField(Lang lang) {
		super(lang, TimeSubscriptions.class);
		BeanItemContainer<TimeSubscriptions> dataSource = new BeanItemContainer<>(TimeSubscriptions.class);
		dataSource.addNestedContainerBean("duration");
		prepareTable(lang, TimeSubscriptions.class, dataSource);
		table.setVisibleColumns(new String[] { "name", "duration.amount", "duration.timeSpan", "normalPrize",
				"studentPrize", "underAgePrize", "remove" });
		table.setColumnHeaders(new String[] { lang.getText("name"), lang.getText("duration.amount"),
				lang.getText("duration.timeSpan"), lang.getText("normalPrize"), lang.getText("studentPrize"),
				lang.getText("underAgePrize"), lang.getText("Remove") });

	}
}
