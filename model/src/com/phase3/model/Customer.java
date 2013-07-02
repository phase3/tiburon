package com.phase3.model;

import org.hibernate.*;

import javax.persistence.*;

/**
 * Project: tiburon
 * User:    cgh
 * Created: 6/14/13
 */
@Entity
@Table(name = "CUSTOMER")
public class Customer extends BaseModelObject {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="UID", nullable=false)
	Long uid;

	@Column(name="FULL_NAME", nullable=true,length=100)
	String fullName;

	@ManyToOne(optional = true)
	@JoinColumn(name = "CREATOR", nullable = true)
	User creator;

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
		hasBeenTouched.add("uid");
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
		hasBeenTouched.add("fullName");
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
		hasBeenTouched.add("creator");
	}

	public void mergeUpdate(Customer c, Session s) throws Exception {
		if (c.hasBeenTouched.contains("fullName")) {
			this.fullName = c.getFullName();
		}
		if (c.hasBeenTouched.contains("creator")) {
			if (c.getCreator() != null) {
				User u = (User) s.get(User.class, c.getCreator().getUid());
				if (u != null) {
					this.creator = u;
				} else {
					throw new Exception("invalid foreign key on field 'creator', bad uid was " + c.getCreator().getUid());
				}

			} else {
				this.creator = null;
			}
		}
	}
}
