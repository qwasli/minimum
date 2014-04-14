package ch.meemin.minimum.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-04-05T13:45:27.333+0200")
@StaticMetamodel(Photo.class)
public class Photo_ extends AbstractEntity_ {
	public static volatile SingularAttribute<Photo, String> name;
	public static volatile SingularAttribute<Photo, String> mimeType;
	public static volatile SingularAttribute<Photo, byte[]> content;
}
