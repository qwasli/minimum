package ch.meemin.minimum;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.Getter;
import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.provider.SettingsProvider;

import com.vaadin.cdi.UIScoped;

@UIScoped
public class CurrentSettings {

	@Inject
	private SettingsProvider settingsProvider;

	@Getter
	private Settings settings;

	@PostConstruct
	public void loadSettings() {
		settings = settingsProvider.getSettings();
	}

}
