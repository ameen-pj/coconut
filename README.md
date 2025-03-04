# Coconut 🥥

_Lightweight REST Resource Server leveraging the latest Java 21 Virtual Threads._ 

## Key Features ✨
- REST
- JSON support
- Write your Service and Resource classes with minimum configuration.
- Supports multiple threading models (Java Virtual Threads, Cached Thread Pool , Fixed Threadpool)
- Annotation based.

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
3. Create your Resource class with appropriate getter and setter methods *"Teacher.java"*
```
package school;

public class Teacher {

	private String name;
	private String subject;

	public Teacher() {

	}

	public Teacher(String name, String subject) {
		this.name = name;
		this.subject = subject;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getName() {
		return this.name;
	}

	public String getSubject() {
		return this.subject;
	}

}

```
4. Create your service class *"TeacherService.java"*
```
package school;

import java.util.HashMap;

import com.apj.projects.coconut.http.enums.HTTPContentTypes;
import com.apj.projects.coconut.resource.annotations.QueryParams;
import com.apj.projects.coconut.resource.annotations.RequestBody;
import com.apj.projects.coconut.resource.handlers.annotations.Path;
import com.apj.projects.coconut.resource.rest.annotations.Consumes;
import com.apj.projects.coconut.resource.rest.annotations.GET;
import com.apj.projects.coconut.resource.rest.annotations.POST;
import com.apj.projects.coconut.resource.rest.annotations.Produces;
import com.apj.projects.coconut.resource.rest.annotations.RESTResourceMapping;

@RESTResourceMapping("teacher")
public class TeacherService {

	@Path("/")
	@GET
	@Produces(HTTPContentTypes.APPLICATION_JSON)
	public Teacher[] getAllTeachers() {

		return new Teacher[] { new Teacher("John Doe", "Maths"), new Teacher("Adam Morris", "English") };
	}

	@Path("/getBySubject")
	@GET
	@Produces(HTTPContentTypes.APPLICATION_JSON)
	public Teacher getTeacherById(@QueryParams("subject") String subject) {
		return new Teacher("Mr Armstrong", subject);
	}

	@Path("/addTeacher")
	@POST
	@Consumes(HTTPContentTypes.APPLICATION_JSON)
	@Produces(HTTPContentTypes.APPLICATION_JSON)
	public HashMap<String, Teacher> addTeacher(@RequestBody Teacher teacher) {
		HashMap<String, Teacher> map = new HashMap<String, Teacher>();
		map.put("Teacher", teacher);
		return map;
	}

}
```
5. Run *"SchoolServer.java"*
![image](https://github.com/user-attachments/assets/4f1a1f13-5f0c-49a1-b314-d3016c5d86a7)
![image](https://github.com/user-attachments/assets/b2c5856e-7ffc-4ab8-9db5-3974edbbe683)
![image](https://github.com/user-attachments/assets/584a033f-cafe-4c41-bb83-2deabf767435)
![image](https://github.com/user-attachments/assets/b4371431-b1d3-41db-956f-ccc8ef8ee6a1)



