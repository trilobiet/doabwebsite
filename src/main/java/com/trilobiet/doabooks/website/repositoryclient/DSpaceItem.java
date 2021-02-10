package com.trilobiet.doabooks.website.repositoryclient;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DSpaceItem implements RepositoryItem, Serializable {
	
	private static final long serialVersionUID = 1;

	@JsonProperty("name") 
	private String name;
	private String handle;
	private String description;
	
	private List<Metadata> metadata = Collections.emptyList();
	private List<Bitstream> bitstreams = Collections.emptyList();
	
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
	@Override
	public List<String> getEditors() {
		
		// Lazy init
		if (!editors.isPresent()) initEditors(metadata);
		return editors.get();
	}

	private void initEditors(List<Metadata> metadata) {
		
		editors = Optional.of(metadata.stream()
			.filter(m -> m.getKey().equals("dc.contributor.editor"))
			.map(m -> m.getValue() )
			.collect(Collectors.toList())
		);	
	}
	
	
	@JsonIgnore
	@Override
	public List<String> getAuthors() {
		
		// Lazy init
		if (!authors.isPresent()) initAuthors(metadata);
		return authors.get();
	}
	
	private void initAuthors(List<Metadata> metadata) {
		
		authors = Optional.of(metadata.stream()
			.filter(m -> m.getKey().equals("dc.contributor.author"))
			.map(m -> m.getValue() )
			.collect(Collectors.toList())
		);	
	}

	@JsonIgnore
	public String getFirstAuthor() {
		
		List<String> authors =  getAuthors();
		int num = authors.size();
		
		if (num == 0) return "";
		else if (num == 1) return authors.get(0);
		else return authors.get(0) + " et al.";
	}

	@JsonIgnore
	@Override
	public String getUrl() {
		
		// Lazy init
		if(!url.isPresent()) initUrl(metadata);
		return url.get();
	}
	
	private void initUrl(List<Metadata> metadata) {
		
		url = metadata.stream()
			.filter(m -> m.getKey().equals("dc.identifier.uri"))
			.map(m -> m.getValue() )
			.findFirst()
			.or(()-> Optional.of(""));
	}
	
	
	@Override
	public String getThumbnailPath() {
		
		// Lazy init
		if(!thumbnailPath.isPresent()) initThumbnail(bitstreams);
		return thumbnailPath.get();
	}

	private void initThumbnail(List<Bitstream> bitstreams) {
		
		Optional<Bitstream> obs = bitstreams.stream()
			.filter( b -> b.isThumbnail() )
			.findFirst();

		String tp = "";
			
		if (obs.isPresent()) {
			
			tp = "/bitstream/handle/" 
				+ getHandle() + "/"
				+ obs.get().getName() + "?sequence="
				+ obs.get().getSequenceId();
		}
		else tp = "";
			
		thumbnailPath = Optional.of(tp);
	}
	
	
	@Override
	public String toString() {
		return "Item [name=" + getTitle() + ", url=" + getUrl() + "]";
	}

}
