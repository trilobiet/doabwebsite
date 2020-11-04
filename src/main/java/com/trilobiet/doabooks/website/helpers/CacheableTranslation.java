package com.trilobiet.doabooks.website.helpers;

import java.io.Serializable;

import com.trilobiet.graphqlweb.datamodel.Translation;

public final class CacheableTranslation implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String languageCode, name, description;
	
	public CacheableTranslation(String languageCode, String name, String description) {
		this.languageCode = languageCode;
		this.name = name;
		this.description = description;
	}

	public CacheableTranslation(Translation t) {
		this.languageCode = t.getLanguageCode();
		this.name = t.getName();
		this.description = t.getDescription();
	}
	
	public String getDescription() {
		return description;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public String getName() {
		return name;
	}

}
