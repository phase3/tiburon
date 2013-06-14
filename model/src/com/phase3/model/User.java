package com.phase3.model;

import javax.persistence.*;

/**
 * Project: tiburon
 * User:    cgh
 * Created: 6/14/13
 */
@Entity
@Table(name = "USER")
public class User extends BaseModelObject {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="UID", nullable=false)
	Long uid;

	@Column(name="FULL_NAME", nullable=true,length=100)
	String fullName;


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

	public void mergeUpdate(User u) {
		if (u.hasBeenTouched.contains("fullName")) {
			this.fullName = u.getFullName();
		}
	}
}
