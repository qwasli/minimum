package ch.meemin.minimum.entities.settings;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.Settings.Flag;
import ch.meemin.minimum.entities.subscriptions.Subscription;

@MappedSuperclass
public abstract class Subscriptions {

	@Getter
	@Setter
	@NotNull
	private String name;

	@Getter
	@Setter
	@ManyToOne
	private SettingImage background;

	@Getter
	@NotNull
	protected String normalPrize;
	@Getter
	protected String studentPrize;
	@Getter
	protected String underAgePrize;
	@Getter
	protected String childrenPrize;
	@Getter
	protected String seniorPrize;

	@Override
	public String toString() {
		return name;
	}

	public abstract Subscription createSubscription(Customer customer, boolean keepId);

	public abstract boolean mayKeepId(Customer customer);

	public String getPrice(Settings settings, Customer customer) {
		String price;
		if (settings.is(Flag.USE_STUDENT) && customer.isStudent())
			price = getStudentPrize();
		else if (!settings.is(Flag.USE_BIRTHDAY))
			price = getNormalPrize();
		else if (settings.getChildAgeLimit() != null && customer.age() <= settings.getChildAgeLimit())
			price = getChildrenPrize();
		else if (settings.getUnderAgeLimit() != null && customer.age() <= settings.getUnderAgeLimit())
			price = getUnderAgePrize();
		else if (settings.getSeniorAgeLimit() != null && customer.age() >= settings.getSeniorAgeLimit())
			price = getSeniorPrize();
		else
			price = getNormalPrize();
		return StringUtils.trimToEmpty(price);
	}

	public void setNormalPrize(String normalPrize) {
		this.normalPrize = StringUtils.trimToNull(normalPrize);
	}

	public void setStudentPrize(String studentPrize) {
		this.studentPrize = StringUtils.trimToNull(studentPrize);
	}

	public void setUnderAgePrize(String underAgePrize) {
		this.underAgePrize = StringUtils.trimToNull(underAgePrize);
	}

	public void setChildrenPrize(String childrenPrize) {
		this.childrenPrize = StringUtils.trimToNull(childrenPrize);
	}

	public void setSeniorPrize(String seniorPrize) {
		this.seniorPrize = StringUtils.trimToNull(seniorPrize);
	}
}