package com.apj.projects.coconut;

import com.apj.projects.coconut.resource.rest.RESTResource;
import com.apj.projects.coconut.resource.rest.annotations.GET;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;

@RESTResourceMapping("student")
public class StudentResource extends RESTResource<Student> {

	@Override
	@GET
	public void create() {
		System.out.println("in create");

	}

	@Override
	public Student get(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Student obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(long id) {
		// TODO Auto-generated method stub

	}

}
