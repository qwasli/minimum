package ch.meemin.minimum.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ch.meemin.minimum.entities.subscriptions.Subscription;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entity implementation class for Entity: Customer
 * 
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Visit extends AbstractEntity implements Serializable,
		Comparable<Visit> {
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
}
