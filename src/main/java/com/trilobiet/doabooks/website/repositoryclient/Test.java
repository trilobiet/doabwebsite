package com.trilobiet.doabooks.website.repositoryclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		
		//String url = "http://doab-test.huma-num.fr/rest/collections/c9fb2822-4db6-4a5b-aa92-ee6c2a756025/items?expand=bitstreams,metadata&limit=10";
		String url = "http://doab-test.huma-num.fr/rest/search?query=oapen.collection:%22European%20Research%20Council%22&limit=10&expand=bitstreams,metadata";
		//String url = "http://doab-test.huma-num.fr/rest/search?expand=bitstreams,metadata&limit=20&query=oapen.collection:%22European+Research+Council%22";
		//String url = "http://doab-test.huma-num.fr/rest/search?query=oapen.collection:%22Harvested%20from%20FWF%22&limit=10&expand=bitstreams,metadata";

		ObjectMapper mapper = new ObjectMapper();
		
		DSpaceItem[] items = mapper.readValue(new URL(url), DSpaceItem[].class);
		
		System.out.println("Found: " + items.length + "\n=================\n");
		
		Arrays.asList(items).forEach(i -> {
			System.out.println(i);
			System.out.println("editors: " + i.getEditors());
			System.out.println("authors: " + i.getAuthors());
			System.out.println("thumbnail: " + i.getThumbnailPath());
			System.out.println("url: " + i.getUrl());
			System.out.println();
		});
		
	}

}
