package com.apj.projects.coconut.resource.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.apj.projects.coconut.http.enums.HTTPContentTypes;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Consumes {

	HTTPContentTypes value() default HTTPContentTypes.TEXT_PLAIN;

}
