package ch.meemin.minimum.entities.settings;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OrderColumn;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import ch.meemin.minimum.entities.AbstractEntity;

@Entity
public class Settings extends AbstractEntity {

	@Getter
	@Setter
	private String adminPassword = null;

	@Getter
	@Setter
	private boolean directLogin = true;

	@Getter
	@Setter
	private int minutesForWarning = 240;

	@Getter
	@Setter
	private boolean useSubscriptionID = true;

	@Getter
	@Setter
	private boolean ignoreBasicSubscription = true;

	@Getter
	@Setter
	private boolean showPhotoOnCard = false;

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
	@Setter
	private Integer underAgeLimit = 16;

	@Getter
	@Setter
	private Integer studentAgeLimit = 27;

	@Getter
	@Setter
	private boolean useStudentField = true;

	@Getter
	@Setter
	private boolean useBirthDayField = true;
	@Getter
	@Setter
	private boolean useNewsletterField = false;

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

	@ElementCollection
	@CollectionTable(name = "images")
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

	@Getter
	@Setter
	private boolean useMastersubscription = false;

}
