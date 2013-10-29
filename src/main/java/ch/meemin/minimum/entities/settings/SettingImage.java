package ch.meemin.minimum.entities.settings;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
public class SettingImage {
	public enum Type {
		PDF_BACKROUND,
		LOGO;
	}

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
