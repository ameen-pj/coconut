package com.apj.projects.coconut.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.apj.projects.coconut.util.exceptions.ObjectCreatorException;

public class ObjectCreator {

	private Constructor<?> constructor;
	private Object obj;

	public ObjectCreator(Class<?> classType, Class<?>... constructorArgTypes) throws ObjectCreatorException {

		try {
			constructor = classType.getDeclaredConstructor(constructorArgTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new ObjectCreatorException(e.getMessage());
		}
	}

	public Object createObject(Object... args) throws ObjectCreatorException {

		if (constructor != null) {
			try {
				this.obj = constructor.newInstance(args);
				return obj;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				throw new ObjectCreatorException(e.getMessage());
			}
		} else {
			throw new ObjectCreatorException("Constructor is null");
		}

	}

}
