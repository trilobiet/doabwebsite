package com.trilobiet.doabooks.website.repositoryclient;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata implements Serializable {

	private static final long serialVersionUID = 8429936168497733095L;
	
	private String key, value, qualifier;

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String getQualifier() {
		return qualifier;
	}

	@Override
	public String toString() {
		return "Metadata [key=" + key + ", value=" + value + "]";
	}
	
}
