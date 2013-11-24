package ch.meemin.minimum.entities.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import ch.meemin.minimum.entities.AbstractEntity;

@Entity
public class Settings extends AbstractEntity {

	public enum Flag {
		USE_BASIC_SUBSCRIPTION(false),
		USE_MASTER_SUBSCRIPTION(false),
		USE_TIME_SUBSCRIPTION(true),
		USE_PREPAID_SUBSCRIPTION(true),
		USE_STUDENT(false),
		USE_BIRTHDAY(true),
		USE_NEWSLETTER(true),
		DIRECTLOGIN(true),
		SUBSCRIPTIONIDONCARD(true),
		PHOTOONCARD(true),
		REQUIREEMAIL(true);

		private Boolean defaultValue;

		Flag(Boolean defaultValue) {
			this.defaultValue = defaultValue;
		}
	}

	@Getter
	@Setter
	private String adminPassword = null;

	@Getter
	@Setter
	private int minutesForWarning = 240;

	@Getter
	private String normalPrize = null;

	public void setNormalPrize(String normalPrize) {
		this.normalPrize = StringUtils.trimToNull(normalPrize);
	}

	@Getter
	private String studentPrize = null;

	public void setStudentPrize(String studentPrize) {
		this.studentPrize = StringUtils.trimToNull(studentPrize);
	}

	@Getter
	private String underAgePrize = null;

	public void setUnderAgePrize(String underAgePrize) {
		this.underAgePrize = StringUtils.trimToNull(underAgePrize);
	}

	@Getter
	private String childrenPrize = null;

	public void setChildrenPrize(String childrenPrize) {
		this.childrenPrize = StringUtils.trimToNull(childrenPrize);
	}

	@Getter
	private String seniorPrize = null;

	public void setSeniorPrize(String seniorPrize) {
		this.seniorPrize = StringUtils.trimToNull(seniorPrize);
	}

	@Getter
	@Setter
	private Integer childAgeLimit = 12;

	@Getter
	@Setter
	private Integer underAgeLimit = 24;

	@Getter
	@Setter
	private Integer seniorAgeLimit = null;

	@Getter
	@Setter
	private Integer studentAgeLimit = 27;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyEnumerated(EnumType.STRING)
	@CollectionTable(name = "flags")
	@Getter
	@Setter
	private Map<Flag, Boolean> flags = new HashMap<>();

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "timeSubscriptions")
	@OrderColumn
	@Getter
	@Setter
	private List<TimeSubscriptions> timeSubscriptions = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "prepaidSubscriptions")
	@OrderColumn
	@Getter
	@Setter
	private List<PrepaidSubscriptions> prepaidSubscriptions = new ArrayList<>();

	public boolean is(Flag flag) {
		if (flags.containsKey(flag))
			return flags.get(flag);
		else
			return flag.defaultValue;
	}

	public void set(Flag flag, boolean value) {
		flags.remove(flag);
		if (!flag.defaultValue.equals(value))
			flags.put(flag, value);
	}

	@OneToMany(mappedBy = "settings")
	@OrderColumn
	@Getter
	@Setter
	private List<SettingImage> images = new ArrayList<>();

	public SettingImage imageByType(SettingImage.Type type) {
		for (SettingImage i : images)
			if (i.getType().equals(type))
				return i;
		return null;
	}
}
