package com.trilobiet.doabooks.website.repositoryclient;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DSpaceItem implements RepositoryItem, Serializable {
	
	private static final long serialVersionUID = 5161385077323821050L;

	@JsonProperty("name") 
	private String name;
	private String handle;
	private String description;
	
	private List<Metadata> metadata;
	private List<Bitstream> bitstreams;
	
	@Override
	public String getTitle() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public String getHandle() {
		return handle;
	}

	public List<Metadata> getMetadata() {
		return metadata;
	}
	
	public List<Bitstream> getBitstreams() {
		return bitstreams;
	}
	
	@JsonIgnore
	public List<String> getEditors() {
		
		List<String> editors = metadata.stream()
			.filter(m -> m.getKey().equals("dc.contributor.editor"))
			.map(m -> m.getValue() )
			.collect(Collectors.toList());
		
		return editors;
	}

	@JsonIgnore
	public List<String> getAuthors() {
		
		List<String> authors = metadata.stream()
				.filter(m -> m.getKey().equals("dc.contributor.author"))
				.map(m -> m.getValue() )
				.collect(Collectors.toList());
			
		return authors;
	}

	@JsonIgnore
	public String getUrl() {
		
		Optional<String> url = metadata.stream()
				.filter(m -> m.getKey().equals("dc.identifier.uri"))
				.map(m -> m.getValue() )
				.findFirst();
		
		return url.orElse("");
	}
	
	@Override
	public String getThumbnailPath() {

		Optional<Bitstream> obs = bitstreams.stream()
			.filter( b -> b.isThumbnail() )
			.findFirst();
		
		if (obs.isPresent()) {
			
			return "/bitstream/handle/" 
					+ getHandle() + "/"
					+ obs.get().getName() + "?sequence="
					+ obs.get().getSequenceId();
		}
		else return "";
	}

	@Override
	public String toString() {
		return "Item [name=" + getTitle() + ", url=" + getUrl() + "]";
	}

}
