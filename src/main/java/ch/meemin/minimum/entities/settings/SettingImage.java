package ch.meemin.minimum.entities.settings;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;
import ch.meemin.minimum.entities.AbstractEntity;

@Entity
public class SettingImage extends AbstractEntity {
	public enum Type {
		PDF_BACKROUND,
		LOGO;
	}

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private Type type;

	@Getter
	@Setter
	private String mimeType;

	@Getter
	@Setter
	private byte[] content;

}
