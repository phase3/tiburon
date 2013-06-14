package com.phase3.jsonbind.model;

public class PhoneType {
	int id;
	String name;

	public PhoneType() {
	}

	public PhoneType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
