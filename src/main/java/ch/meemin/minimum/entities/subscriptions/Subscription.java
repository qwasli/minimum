package ch.meemin.minimum.entities.subscriptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ch.meemin.minimum.entities.AbstractEntity;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.Visit;
import ch.meemin.minimum.entities.settings.SettingImage;
import ch.meemin.minimum.entities.settings.Subscriptions;
import ch.meemin.minimum.lang.Lang;

/**
 * Entity implementation class for Entity: Customer
 * 
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Subscription extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public Subscription(Customer customer, Subscriptions type) {
		this.customer = customer;
		if (type != null) {
			this.typeName = type.getName();
			this.background = type.getBackground();
		}
	}

	private String typeName;

	@Getter
	@Setter
	@ManyToOne
	private SettingImage background;

	@ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE })
	@NotNull
	@Getter
	Customer customer;

	@OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
	@OrderBy("createdAt")
	List<Visit> visits = new ArrayList<Visit>();

	@OneToOne(cascade = CascadeType.ALL)
	@Getter
	Subscription replacedBy = null;

	@OneToOne(mappedBy = "replacedBy")
	@Getter
	Subscription replacing = null;

	// FIXME Find a way to get it back to TimeSubscription and still work with JPAContainerItem
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	protected Date expiry;

	// FIXME Find a way to get it back to PrepaidSubscription and still work with JPAContainerItem
	@Getter
	@Setter
	protected int credit;

	public abstract boolean valid();

	public abstract void replace();

	public boolean isSuspended() {
		return false;
	}

	public void suspend() {}

	public void reactivate() {}

	public boolean isReplaced() {
		return replacedBy != null;
	}

	protected void doReplace(Subscription sub) {
		sub.customer = customer;
		sub.background = background;
		sub.typeName = typeName;
		this.replacedBy = sub;
		sub.replacing = this;
		if (this.equals(customer.getCurrentSubscription()))
			customer.setCurrentSubscription(sub);
	}

	public Visit checkIn() {
		Visit visit = new Visit(this);
		this.customer.setLastVisit(visit);
		visits.add(visit);
		return visit;
	}

	public Visit getLastVisit() {
		if (visits.isEmpty())
			return null;
		return visits.get(visits.size() - 1);
	}

	public String getTypeName(Lang lang) {
		if (typeName != null)
			return typeName;
		else
			return lang.getText(this.getClass().getSimpleName());
	}
}
