package com.apj.projects.coconut;

import com.apj.projects.coconut.resource.handlers.annotations.Path;
import com.apj.projects.coconut.resource.rest.RESTResource;
import com.apj.projects.coconut.resource.rest.annotations.DELETE;
import com.apj.projects.coconut.resource.rest.annotations.GET;
import com.apj.projects.coconut.resource.rest.annotations.POST;
import com.apj.projects.coconut.resource.rest.annotations.PUT;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;

@RESTResourceMapping("student")
public class StudentResource extends RESTResource<Student> {

	@Path("/")
	@GET
	public void create() {
		System.out.println("in create");

	}

	@Path("/")
	@POST
	public Student get(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/")
	@PUT
	public void update(Student obj) {
		// TODO Auto-generated method stub

	}

	@Path("/")
	@DELETE
	public void delete(long id) {
		// TODO Auto-generated method stub

	}

}
