package com.trilobiet.doabooks.website.repositoryclient;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bitstream implements Serializable {
	
	private static final long serialVersionUID = 593346853366131724L;

	private String name, bundleName;
	private List<Metadata> metadata;
	private String sequenceId;
	
	static final String THUMBNAIL_BUNDLE = "THUMBNAIL";

	public String getName() {
		return name;
	}
	
	public String getBundleName() {
		return bundleName;
	}

	public boolean isThumbnail() {
		return bundleName.equals(THUMBNAIL_BUNDLE);
	}
	
	/*
	 * Sequence is used to make equally named files unique
	 */
	public String getSequenceId() {
		return sequenceId;
	}

	public List<Metadata> getMetadata() {
		return metadata;
	}

	@Override
	public String toString() {
		return "Bitstream [name=" + name + "]";
	}
	
}
