package ch.meemin.minimum.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ch.meemin.minimum.entities.subscriptions.Subscription;

/**
 * Entity implementation class for Entity: Customer
 * 
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({ @NamedQuery(name = Visit.Q.CountBySubsType, query = Visit.Q.CountBySubsTypeQ) })
public class Visit extends AbstractEntity implements Serializable, Comparable<Visit> {
	private static final long serialVersionUID = 1L;

	public Visit(Subscription subscription) {
		this.subscription = subscription;
	}

	@Override
	public int compareTo(Visit o) {
		return getCreatedAt().compareTo(o.getCreatedAt());
	}

	@ManyToOne
	@NotNull
	@Getter
	private Subscription subscription;

	public class Q {
		public static final String CountBySubsType = "Visit.CountBySubsType";
		protected static final String CountBySubsTypeQ = "SELECT s.typeName, COUNT(v) FROM Visit v LEFT JOIN v.subscription s WHERE s.typeName IS NOT NULL AND v.createdAt >= :from AND v.createdAt <= :to GROUP BY s.typeName";

	}

}
