package com.phase3.jsonbind.model;

import java.util.*;

public class Customer {
	String name;
	int age;
	Date dateOfBirth;
	boolean isActive;
	ArrayList<Phone> phoneList = null;

	public ArrayList<Phone> getPhoneList() {
		return phoneList;
	}

	public void setPhoneList(ArrayList<Phone> phoneList) {
		this.phoneList = phoneList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}
}
