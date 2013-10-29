package ch.meemin.minimum.entities;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import lombok.Getter;

@MappedSuperclass
public abstract class AbstractEntity {

	private static Long ID = -1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pkGen")
	@TableGenerator(allocationSize = 1000, initialValue = 0, name = "pkGen", table = "PRIMARY_KEYS")
	private Long id = null;
	@Getter
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt = new Date();;

	public Long getId() {
		if (id == null)
			return tempID;
		return id;
	}

	@Transient
	private Long tempID = ID--;

	// @PrePersist
	// public void clearNewIDsToNull() throws NoSuchFieldException,
	// SecurityException, IllegalArgumentException,
	// IllegalAccessException {
	// if (id <= 0) {
	// id = null;
	// Class<?> c = this.getClass();
	// Field _id = c.getDeclaredField("_persistence_primaryKey");
	// _id.set(this, null);
	// }
	// }

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

	public boolean isUnpersisted() {
		return this.id == null;
	}
}
