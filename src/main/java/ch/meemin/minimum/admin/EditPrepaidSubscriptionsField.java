package ch.meemin.minimum.admin;

import ch.meemin.minimum.entities.settings.PrepaidSubscriptions;
import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.lang.Lang;

public class EditPrepaidSubscriptionsField extends EditSubscriptionsField<PrepaidSubscriptions> {
	public EditPrepaidSubscriptionsField(Lang lang, Settings settings) {
		super(lang, PrepaidSubscriptions.class, settings);
		setVisibleColumns("credit");
	}
}
