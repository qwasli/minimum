package ch.meemin.minimum.admin;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import ch.meemin.minimum.lang.Lang;
import ch.meemin.minimum.report.ReportWindow;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

@UIScoped
public class AdminBar extends CustomComponent {

	private HorizontalLayout layout = new HorizontalLayout();
	@Inject
	private Lang lang;
	@Inject
	private Instance<EditSettingsWin> esw;
	// @Inject
	// private ValidatePasswordWindow pwWin;
	@Inject
	private Instance<ReportWindow> reports;

	@PostConstruct
	public void init() {
		setCompositionRoot(layout);
		setWidth(100, Unit.PERCENTAGE);
		layout.setWidth(100, Unit.PERCENTAGE);

		Button reportsButton = new Button(lang.getText("Reports"), new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				reports.get().show();
			}
		});

		Button settingsButton = new Button(lang.getText("Settings"), new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				esw.get().show();
				// pwWin.show(esw.get());
			}
		});
		Label spacer = new Label();
		layout.addComponent(spacer);
		layout.setExpandRatio(spacer, 1f);
		layout.addComponent(reportsButton);
		layout.addComponent(settingsButton);

	}
}
