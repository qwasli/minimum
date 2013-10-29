package ch.meemin.minimum.admin;

import ch.meemin.minimum.entities.settings.PrepaidSubscriptions;
import ch.meemin.minimum.lang.Lang;

public class EditPrepaidSubscriptionsField extends EditSubscriptionsField<PrepaidSubscriptions> {
	public EditPrepaidSubscriptionsField(Lang lang) {
		super(lang, PrepaidSubscriptions.class);
		table
				.setVisibleColumns(new String[] { "name", "credit", "normalPrize", "studentPrize", "underAgePrize", "remove" });
		table.setColumnHeaders(new String[] { lang.getText("name"), lang.getText("credit"), lang.getText("normalPrize"),
				lang.getText("studentPrize"), lang.getText("underAgePrize"), lang.getText("Remove") });
	}
}
