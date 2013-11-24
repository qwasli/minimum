package ch.meemin.minimum.admin;

import lombok.Getter;
import ch.meemin.minimum.entities.settings.Settings;
import ch.meemin.minimum.entities.settings.Settings.Flag;

import com.vaadin.data.util.AbstractProperty;

public class FlagDataSource extends AbstractProperty<Boolean> {
	Settings settings;
	@Getter
	Flag flag;

	public FlagDataSource(Settings settings, Flag flag) {
		this.settings = settings;
		this.flag = flag;
	}

	@Override
	public Boolean getValue() {
		return settings.is(flag);
	}

	@Override
	public void setValue(Boolean newValue) throws com.vaadin.data.Property.ReadOnlyException {
		settings.set(flag, newValue);
	}

	@Override
	public Class<? extends Boolean> getType() {
		return Boolean.class;
	}
}
