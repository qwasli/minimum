package ch.meemin.minimum.entities.settings;

import ch.meemin.minimum.entities.AbstractEntity_;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-04-05T13:45:27.336+0200")
@StaticMetamodel(Settings.class)
public class Settings_ extends AbstractEntity_ {
	public static volatile SingularAttribute<Settings, String> adminPassword;
	public static volatile SingularAttribute<Settings, Integer> minutesForWarning;
	public static volatile SingularAttribute<Settings, String> normalPrize;
	public static volatile SingularAttribute<Settings, String> studentPrize;
	public static volatile SingularAttribute<Settings, String> underAgePrize;
	public static volatile SingularAttribute<Settings, String> childrenPrize;
	public static volatile SingularAttribute<Settings, String> seniorPrize;
	public static volatile SingularAttribute<Settings, Integer> cardHeight;
	public static volatile SingularAttribute<Settings, Integer> cardWidth;
	public static volatile SingularAttribute<Settings, Integer> childAgeLimit;
	public static volatile SingularAttribute<Settings, Integer> underAgeLimit;
	public static volatile SingularAttribute<Settings, Integer> seniorAgeLimit;
	public static volatile SingularAttribute<Settings, Integer> studentAgeLimit;
	public static volatile MapAttribute<Settings, Flag, Boolean> flags;
	public static volatile ListAttribute<Settings, TimeSubscriptions> timeSubscriptions;
	public static volatile ListAttribute<Settings, PrepaidSubscriptions> prepaidSubscriptions;
	public static volatile ListAttribute<Settings, SettingImage> images;
}
