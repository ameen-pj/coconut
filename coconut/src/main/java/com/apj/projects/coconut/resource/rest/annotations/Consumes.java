package com.apj.projects.coconut.resource.rest.annotations;

import com.apj.projects.coconut.http.enums.HTTPContentTypes;

public @interface Consumes {

	HTTPContentTypes value() default HTTPContentTypes.TEXT_PLAIN;

}
