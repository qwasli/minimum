package ch.meemin.minimum.entities;

import ch.meemin.minimum.entities.subscriptions.Subscription;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-04-05T13:45:27.331+0200")
@StaticMetamodel(Customer.class)
public class Customer_ extends AbstractEntity_ {
	public static volatile SingularAttribute<Customer, String> name;
	public static volatile SingularAttribute<Customer, String> email;
	public static volatile SingularAttribute<Customer, String> phone;
	public static volatile SingularAttribute<Customer, String> address;
	public static volatile SingularAttribute<Customer, Date> birthDate;
	public static volatile SingularAttribute<Customer, Boolean> student;
	public static volatile SingularAttribute<Customer, Boolean> newsletter;
	public static volatile SingularAttribute<Customer, Visit> lastVisit;
	public static volatile SingularAttribute<Customer, Photo> photo;
	public static volatile SingularAttribute<Customer, Subscription> currentSubscription;
	public static volatile ListAttribute<Customer, Subscription> subscriptions;
}
