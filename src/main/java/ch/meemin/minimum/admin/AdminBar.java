package ch.meemin.minimum.admin;

import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.report.ReportWindow;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Reindeer;

public class AdminBar extends CustomComponent {

	private final HorizontalLayout layout = new HorizontalLayout();

	public AdminBar(Lang lang) {
		setCompositionRoot(layout);
		setWidth(100, Unit.PERCENTAGE);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setStyleName(Reindeer.LAYOUT_BLACK);

		Button reportsButton = new Button(lang.getText("Reports"), new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				new ReportWindow();
			}
		});

		Button settingsButton = new Button(lang.getText("Settings"), new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				new EditSettingsWin(getUI());
			}
		});
		Label spacer = new Label();
		layout.addComponent(spacer);
		layout.setExpandRatio(spacer, 1f);
		layout.addComponent(reportsButton);
		layout.addComponent(settingsButton);

	}
}
