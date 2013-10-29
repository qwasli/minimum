package ch.meemin.minimum.entities;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Photo extends AbstractEntity{
	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private String mimeType;
	@Getter
	@Setter
	private byte[] content;

}
