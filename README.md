# Coconut 🥥

_Lightweight REST Resource Server written in Java, leveraging the latest Java 21 Virtual Threads._ 

## Key Features ✨
- Support for multiple threading models (Virtual Threads, Fixed Thread pool and Cached Thread pool)
- RESTful architecture
- JSON Support
- Minimal configuration for service and resource classes.
- Utilization of  Java annotations to eliminate tedious XML configurations.

## **Installation** ⬇️

1. Clone this project
2. Make sure that you have maven installed and run the following command
```
>>> cd coconut/
>>> mvn clean install
```
3. Create a new maven project and add the project to your pom.xml file
```
<dependency>
	<groupId>com.apj.projects</groupId>
	<artifactId>coconut</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

## **Manual 📗**

1. Create *"coconut.properties"* in your classpath
```
port=8080
rest_service_package_name= #NAME_OF_SERVICE_PACKAGE#
thread_type=VIRTUAL_THREAD or CACHED_THREAD_POOL or FIXED_THREAD_POOL
n_threads=n # only for FIXED_THREAD_POOL

```

2. Create an entry point for your application *"SchoolServer.java"*
```
package school;

import com.apj.projects.coconut.Coconut;

public class SchoolServer {
	public static void main(String[] args) {
		Coconut.start();
	}
}
```
3. Create your Resource class with appropriate getter and setter methods *"Student.java"*
```
package school;

public class Student {

	private int id;
	private String name;
	private int grade;
	private char division;

	public Student() {

	}

	public Student(int id, String name, int grade, char division) {

		this.setId(id);
		this.setName(name);
		this.setGrade(grade);
		this.setDivision(division);

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

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public char getDivision() {
		return division;
	}

	public void setDivision(char division) {
		this.division = division;
	}

	public String toString() {
		return id + ": " + name + " | " + grade + "-" + division;
	}

}
```
4. Create your service class *"StudentResource.java"*
```
package school;

import java.util.Arrays;

import com.apj.projects.coconut.http.enums.HTTPContentTypes;
import com.apj.projects.coconut.resource.annotations.QueryParams;
import com.apj.projects.coconut.resource.annotations.RequestBody;
import com.apj.projects.coconut.resource.rest.annotations.DELETE;
import com.apj.projects.coconut.resource.rest.annotations.GET;
import com.apj.projects.coconut.resource.rest.annotations.POST;
import com.apj.projects.coconut.resource.rest.annotations.Produces;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;

@RESTResourceMapping("student")
public class StudentResource {

	@GET
	@Produces(HTTPContentTypes.APPLICATION_JSON)
	public Student[] getAllStudents() {
		return new Student[] { new Student(1, "Mary Jane", 10, 'A'), new Student(2, "John Doe", 8, 'E'),
				new Student(3, "Ameen PJ", 12, 'A')

		};
	}

	@GET
	@Produces(HTTPContentTypes.APPLICATION_JSON)
	public Student getStudentById(@QueryParams("id") int id) {
		return new Student(id, "Ameen PJ", 12, 'A');
	}

	@POST
	@Produces(HTTPContentTypes.TEXT_PLAIN)
	public String addStudent(@RequestBody Student student) {
		return student.toString() + " created";
	}

	@POST
	@Produces(HTTPContentTypes.TEXT_PLAIN)
	public String addStudents(@RequestBody Student[] students) {
		return Arrays.toString(students) + " created";
	}

	@DELETE
	@Produces(HTTPContentTypes.TEXT_PLAIN)
	public String deleteStudent(@QueryParams("id") int id) {
		return "deleted student with id: " + id;
	}

}
```
5. Run *"SchoolServer.java"*
### Console
![image](https://github.com/user-attachments/assets/61dd125f-800e-449e-9b03-8f4e9088fefc)
### Get All Students
![image](https://github.com/user-attachments/assets/e0cf4f1e-4214-43a7-b25c-fb7a4ea1bc2a)
### Get Student by ID
![image](https://github.com/user-attachments/assets/16c2da1f-1c1c-425b-a732-e632b503950c)
### Add Student
![image](https://github.com/user-attachments/assets/f7794f04-8775-48c0-a0cd-d72b07d66cd1)
### Add Students
![image](https://github.com/user-attachments/assets/617c552e-a1c3-42dd-9a6f-d07f30e023d9)
### Delete Student by ID
![image](https://github.com/user-attachments/assets/eac9413c-72f4-431f-a938-af90e87489d9)









