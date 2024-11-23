package com.apj.projects.coconut;

import com.apj.projects.coconut.http.enums.HTTPContentTypes;
import com.apj.projects.coconut.resource.annotations.QueryParams;
import com.apj.projects.coconut.resource.annotations.RequestBody;
import com.apj.projects.coconut.resource.handlers.annotations.Path;
import com.apj.projects.coconut.resource.rest.annotations.Consumes;
import com.apj.projects.coconut.resource.rest.annotations.GET;
import com.apj.projects.coconut.resource.rest.annotations.POST;
import com.apj.projects.coconut.resource.rest.annotations.Produces;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;

@RESTResourceMapping("student")
public class StudentResource {

	@Path("/")
	@GET
	@Produces(HTTPContentTypes.APPLICATION_JSON)
	public Student getStudentById(@QueryParams("id") long id) {
		return new Student(id, "Ameen");
	}

	@Path("/")
	@POST
	@Consumes(HTTPContentTypes.APPLICATION_JSON)
	@Produces(HTTPContentTypes.APPLICATION_JSON)
	public Student[] createStudent(@RequestBody Student[] students) {
		return students;
	}

}
