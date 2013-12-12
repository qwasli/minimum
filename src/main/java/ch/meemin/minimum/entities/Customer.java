package ch.meemin.minimum.entities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import ch.meemin.minimum.entities.subscriptions.BasicSubscription;
import ch.meemin.minimum.entities.subscriptions.Subscription;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Image;

/**
 * Entity implementation class for Entity: Customer
 * 
 */
@Entity
public class Customer extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@NotNull
	private String name;
	@Getter
	@Setter
	private String email;
	@Getter
	@Setter
	private String phone;
	@Getter
	@Setter
	private String address;
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	private Date birthDate;
	@Getter
	@Setter
	private boolean student;
	@Getter
	@Setter
	private boolean newsletter;

	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Visit lastVisit;

	@Getter
	@Setter
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Photo photo;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotNull
	@Getter
	@Setter
	private Subscription currentSubscription = new BasicSubscription(this);

	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Getter
	private List<Subscription> subscriptions = new ArrayList<>();

	@PreUpdate
	@PrePersist
	public void checkSubscriptions() {
		if (!subscriptions.contains(currentSubscription))
			subscriptions.add(currentSubscription);
	}

	public void checkIn(boolean ignoreBasicSub) {
		this.lastVisit = currentSubscription.checkIn();
		if (!(currentSubscription instanceof BasicSubscription) && !currentSubscription.valid() && ignoreBasicSub)
			currentSubscription = new BasicSubscription(this);
	}

	public Image getImage() {
		if (photo == null)
			return null;
		Image image = new Image(null, new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				return new ByteArrayInputStream(photo.getContent());
			}
		}, photo.getName()));
		return image;
	}

	public int age() {
		if (birthDate != null) {
			Years y = Years.yearsBetween(new LocalDate(birthDate.getTime()), new LocalDate());
			return y.getYears();
		} else
			return 0;
	}
}
