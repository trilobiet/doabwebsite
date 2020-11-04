package com.trilobiet.doabooks.website.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.trilobiet.graphqlweb.datamodel.Section;

public class OapenMenuParser<S extends Section> {

	final List<S> sections;

	public OapenMenuParser(List<S> sections) {
		this.sections = sections;
	}
	
	public List<S> getSectionsForHeader() {
		
		return getMenu(3);
	}

	public List<S> getSectionsForMainLeft() {
		
		return getMenu(1);
	}
	
	public List<S> getSectionsForMainRight() {
		
		return getMenu(2);
	}
	
	public List<S> getSectionsForFooter() {
		
		List<S> f = new ArrayList<>(getMenu(1));
		f.addAll(getMenu(2));
		return f;
	}
	

	private List<S> getMenu(final int group) {

		List<S> list = sections.stream()
			.filter( section -> section.getGroupNumber() == group )
			.filter( section -> section.isHasMenuItem() == true )
			.collect(Collectors.toList());
		
		// list.stream().forEach(section -> new OapenTopicGrouper( section.getTopics() )  ) ;
		
		return list;
		
	}
	
	
}
