package com.trilobiet.doabooks.website.repositoryclient;

import java.util.List;

public interface RepositoryService {
	
	List<RepositoryItem> getFeaturedItems(List<String> subjects) throws RepositoryException;
	Integer getCommunitiesCount(List<String> communities) throws Exception;
	
}
