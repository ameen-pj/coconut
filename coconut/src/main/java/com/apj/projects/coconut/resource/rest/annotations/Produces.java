package com.apj.projects.coconut.resource.rest.annotations;

import com.apj.projects.coconut.http.enums.HTTPContentTypes;

public @interface Produces {

	HTTPContentTypes value() default HTTPContentTypes.TEXT_PLAIN;

}
