package ch.meemin.minimum.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-04-05T13:45:27.300+0200")
@StaticMetamodel(AbstractEntity.class)
public class AbstractEntity_ {
	public static volatile SingularAttribute<AbstractEntity, Long> id;
	public static volatile SingularAttribute<AbstractEntity, Date> createdAt;
	public static volatile SingularAttribute<AbstractEntity, Long> version;
}
