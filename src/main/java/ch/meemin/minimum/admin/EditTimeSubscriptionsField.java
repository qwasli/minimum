package ch.meemin.minimum.admin;

import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.entities.settings.TimeSubscriptions;
import ch.meemin.minimum.lang.Lang;

import com.vaadin.data.util.BeanItemContainer;

public class EditTimeSubscriptionsField extends EditSubscriptionsField<TimeSubscriptions> {
	public EditTimeSubscriptionsField(Lang lang, Settings settings) {
		super(lang, TimeSubscriptions.class, settings);
		BeanItemContainer<TimeSubscriptions> dataSource = new BeanItemContainer<>(TimeSubscriptions.class);
		dataSource.addNestedContainerBean("duration");
		prepareTable(lang, TimeSubscriptions.class, dataSource);
		setVisibleColumns("duration.amount", "duration.timeSpan");
	}
}
