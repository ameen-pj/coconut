package com.apj.projects.coconut.resource.rest;

public abstract class RESTResource<T extends Entity> {

	public abstract void create();

	public abstract T get(long id);

	public abstract void update(T obj);

	public abstract void delete(long id);
}