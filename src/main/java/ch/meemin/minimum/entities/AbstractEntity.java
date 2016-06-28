package ch.meemin.minimum.entities;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class AbstractEntity {

	private static AtomicLong ID = new AtomicLong(System.currentTimeMillis());

	@Id
	@Getter
	private Long id = ID.incrementAndGet();

	@Getter
	@Setter
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt = new Date();

	@Version
	@Getter
	protected Long version = 0L;

	@Getter
	@Transient
	protected boolean deleting;

	@Override
	public String toString() {
		return this.getClass().getName() + " - " + getId();
	}

	@Override
	public int hashCode() {
		if (getId() == null)
			return super.hashCode();
		return getId().intValue();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass().equals(obj.getClass()) && getId() != null) {
			final AbstractEntity other = (AbstractEntity) obj;
			if (getId().equals(other.getId())) {
				return true;
			} else {
				return false;
			}
		}
		return super.equals(obj);
	}
}
