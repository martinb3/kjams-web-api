package org.mbs3.kjams.model;

public class NameValuePair {
	public NameValuePair(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}
	private final String name;
	private final Object value;
	public String getName() {
		return name;
	}
	public Object getValue() {
		return value;
	}
}
