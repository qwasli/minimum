package ch.meemin.minimum.entities.subscriptions;

import ch.meemin.minimum.entities.AbstractEntity_;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.Visit;
import ch.meemin.minimum.entities.settings.SettingImage;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-04-05T13:45:27.341+0200")
@StaticMetamodel(Subscription.class)
public class Subscription_ extends AbstractEntity_ {
	public static volatile SingularAttribute<Subscription, String> typeName;
	public static volatile SingularAttribute<Subscription, SettingImage> background;
	public static volatile SingularAttribute<Subscription, Customer> customer;
	public static volatile ListAttribute<Subscription, Visit> visits;
	public static volatile SingularAttribute<Subscription, Subscription> replacedBy;
	public static volatile SingularAttribute<Subscription, Subscription> replacing;
	public static volatile SingularAttribute<Subscription, Date> expiry;
	public static volatile SingularAttribute<Subscription, Integer> credit;
}
