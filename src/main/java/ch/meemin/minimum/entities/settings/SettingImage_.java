package ch.meemin.minimum.entities.settings;

import ch.meemin.minimum.entities.AbstractEntity_;
import ch.meemin.minimum.entities.settings.SettingImage.Type;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-04-05T13:45:27.336+0200")
@StaticMetamodel(SettingImage.class)
public class SettingImage_ extends AbstractEntity_ {
	public static volatile SingularAttribute<SettingImage, String> name;
	public static volatile SingularAttribute<SettingImage, Type> type;
	public static volatile SingularAttribute<SettingImage, String> mimeType;
	public static volatile SingularAttribute<SettingImage, byte[]> content;
}
