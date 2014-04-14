package ch.meemin.minimum.entities.settings;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-04-05T13:45:27.338+0200")
@StaticMetamodel(Subscriptions.class)
public class Subscriptions_ {
	public static volatile SingularAttribute<Subscriptions, String> name;
	public static volatile SingularAttribute<Subscriptions, SettingImage> background;
	public static volatile SingularAttribute<Subscriptions, String> normalPrize;
	public static volatile SingularAttribute<Subscriptions, String> studentPrize;
	public static volatile SingularAttribute<Subscriptions, String> underAgePrize;
	public static volatile SingularAttribute<Subscriptions, String> childrenPrize;
	public static volatile SingularAttribute<Subscriptions, String> seniorPrize;
}
