package ch.meemin.minimum.entities.settings;

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
	@NotNull
	protected String normalPrize;
	@Getter
	protected String studentPrize;
	@Getter
	protected String underAgePrize;

	@Override
	public String toString() {
		return name;
	}

	public abstract Subscription createSubscription(Customer customer);

	public String getPrice(Settings settings, Customer customer) {
		String price;
		if (settings.is(Flag.USE_STUDENT) && customer.isStudent())
			price = getStudentPrize();
		else if (settings.is(Flag.USE_BIRTHDAY) && customer.age() <= settings.getUnderAgeLimit())
			price = getUnderAgePrize();
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

}