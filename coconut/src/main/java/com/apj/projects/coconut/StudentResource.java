package com.apj.projects.coconut;

import com.apj.projects.coconut.http.enums.HTTPContentTypes;
import com.apj.projects.coconut.resource.annotations.QueryParams;
import com.apj.projects.coconut.resource.annotations.RequestBody;
import com.apj.projects.coconut.resource.handlers.annotations.Path;
import com.apj.projects.coconut.resource.rest.RESTResource;
import com.apj.projects.coconut.resource.rest.annotations.Consumes;
import com.apj.projects.coconut.resource.rest.annotations.GET;
import com.apj.projects.coconut.resource.rest.annotations.POST;
import com.apj.projects.coconut.resource.rest.annotations.Produces;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;

@RESTResourceMapping("student")
public class StudentResource extends RESTResource<Student> {

	@Path("/")
	@GET
	@Produces(HTTPContentTypes.APPLICATION_JSON)
	public Student getStudentById(@QueryParams("id") long id) {
		return new Student(id, "Ameen");
	}

	@Path("/")
	@GET
	@Produces(HTTPContentTypes.APPLICATION_JSON)
	public Student[] getAllStudents() {
		return new Student[] { new Student(1, "John"), new Student(2, "Mary") };
	}

	@Path("/")
	@POST
	@Consumes(HTTPContentTypes.APPLICATION_JSON)
	@Produces(HTTPContentTypes.APPLICATION_JSON)
	public Student createStudent(@RequestBody Student student) {
		return student;
	}

}
