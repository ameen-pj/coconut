package com.apj.projects.coconut;

import com.apj.projects.coconut.resource.rest.Entity;

public class Student extends Entity {

	private long id;
	private String name;

	public Student() {

	}

	public Student(long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public void setId(long id) {
		this.id = id;

	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
