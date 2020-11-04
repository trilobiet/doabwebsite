package com.trilobiet.doabooks.website.repositoryclient;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DSpaceCommunity implements RepositoryCommunity, Serializable {

	private static final long serialVersionUID = -476834996141817805L;
	
	private String name;
	private String handle;
	private String id;
	private String link;
	private Integer countItems;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getHandle() {
		return handle;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Integer getCountItems() {
		return countItems;
	}

	@Override
	public String getLink() {
		return link;
	}

	@Override
	public String toString() {
		return "DSpaceCommunity [name=" + name + ", handle=" + handle + ", countItems=" + countItems + "]";
	}

}
