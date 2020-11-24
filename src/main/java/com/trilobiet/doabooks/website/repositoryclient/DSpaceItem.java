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
	
	// These fields are initialized on first request
	private Optional<List<String>> editors = Optional.empty();
	private Optional<List<String>> authors = Optional.empty();
	private Optional<String> url = Optional.empty();
	private Optional<String> thumbnailPath = Optional.empty();
	
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
		
		// Lazy init
		if (!editors.isPresent()) {	
			editors = Optional.of(metadata.stream()
				.filter(m -> m.getKey().equals("dc.contributor.editor"))
				.map(m -> m.getValue() )
				.collect(Collectors.toList())
			);
		}
		
		return editors.get();
	}

	@JsonIgnore
	public List<String> getAuthors() {
		
		// Lazy init
		if (!authors.isPresent()) {
			authors = Optional.of(metadata.stream()
				.filter(m -> m.getKey().equals("dc.contributor.author"))
				.map(m -> m.getValue() )
				.collect(Collectors.toList())
			);
		}	
			
		return authors.get();
	}

	@JsonIgnore
	public String getUrl() {
		
		// Lazy init
		if(!url.isPresent()) {
		
			url = metadata.stream()
				.filter(m -> m.getKey().equals("dc.identifier.uri"))
				.map(m -> m.getValue() )
				.findFirst()
				.or(()-> Optional.of(""));
		}		
		
		return url.get();
	}
	
	@Override
	public String getThumbnailPath() {
		
		String tp = "";
		
		// Lazy init
		if(!thumbnailPath.isPresent()) {

			Optional<Bitstream> obs = bitstreams.stream()
				.filter( b -> b.isThumbnail() )
				.findFirst();
			
			if (obs.isPresent()) {
				
				tp = "/bitstream/handle/" 
						+ getHandle() + "/"
						+ obs.get().getName() + "?sequence="
						+ obs.get().getSequenceId();
			}
			else tp = "";
			
			thumbnailPath = Optional.of(tp);
		}	
			
		return thumbnailPath.get();
	}

	@Override
	public String toString() {
		return "Item [name=" + getTitle() + ", url=" + getUrl() + "]";
	}

}
