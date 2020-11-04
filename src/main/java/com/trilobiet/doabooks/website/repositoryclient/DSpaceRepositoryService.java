package com.trilobiet.doabooks.website.repositoryclient;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DSpaceRepositoryService implements RepositoryService {
	
	private final String baseUrl;
	
	protected final Logger log = LogManager.getLogger(this.getClass());
	
	public DSpaceRepositoryService(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	@Cacheable(value="dspaceCache",key="#root.methodName") // key="'somefancyname'" or any SpEL expression
	public List<RepositoryItem> getFeaturedItems(List<String> subjects) throws RepositoryException {
		
		List<String> usedHandles = new ArrayList<>();
		
		List<RepositoryItem> lst = subjects.stream()
			.map(s -> featuredItemForSubject(s,usedHandles))
			.collect(Collectors.toList());
		
		return lst;
	}

	
	@Override
	@Cacheable(value="dspaceCache",key="#root.methodName + '-' + #communities")
	public Integer getCommunitiesCount(List<String> communities) throws RepositoryException {

		List<String> lcCommunities = 
			communities.stream().map(c -> c.toLowerCase()).collect(Collectors.toList());	
		
		int count = getCommunities().stream()
			.filter(c -> lcCommunities.contains(c.getName().toLowerCase()))
			.mapToInt( c -> c.getCountItems())
			.sum();

		return count;
	}

	
	private List<DSpaceCommunity> getCommunities() throws RepositoryException {
		
		ObjectMapper mapper = new ObjectMapper();
		DSpaceCommunity[] communities;
		
		try {
			communities = mapper.readValue(new URL(communitiesUrl()), DSpaceCommunity[].class);
		} catch (IOException e) {
			throw new RepositoryException(e);
		}
		
		return Arrays.asList(communities);
	}


	private DSpaceItem featuredItemForSubject(String subject, List<String> usedHandles) {
		
		StringBuilder sb = new StringBuilder(baseUrl);
		
		// https://library.oapen.org/rest/search?query=dc.subject.classification:%22Politics+%26+Government%22&sort=dc.date.accessioned_dt&limit=1&expand=all

		sb.append("/rest/search?query=dc.subject.classification:%22")
		  //.append("::JP+")	
		  .append(URLEncoder.encode(subject,StandardCharsets.UTF_8))
		  .append("%22%20AND%20dc.type=book");
		
		// usedHandles contains titles already included, 
		// so that we must skip them in subsequent queries
		usedHandles.stream().forEach(h -> sb.append("%20AND%20-handle:").append(h));
		
		String url = sb.append("&sort=dc.date.accessioned_dt")
		  .append("&limit=1")
		  .append("&expand=all")
		  .toString();
		
		log.debug(url);
		
		ObjectMapper mapper = new ObjectMapper();
		DSpaceItem item = null;
		
		try {
			DSpaceItem[] items = mapper.readValue(new URL(url), DSpaceItem[].class);
			if (items.length > 0) {
				item = items[0];
				usedHandles.add(items[0].getHandle());
			}
		} catch (IOException e) {
			item = new DSpaceItem(); // just an empty item
			log.warn(e);
		}
		
		return item;
	}
	
	
	private String communitiesUrl() {
		
		StringBuilder sb = new StringBuilder(baseUrl);
		sb.append("/rest/communities/");
		
		return sb.toString();
	}
	

	static public void main(String[] args) throws Exception {
		
		DSpaceRepositoryService rs = new DSpaceRepositoryService("https://library.oapen.org");
		
		String[] p = {"publishers"};
		String[] q = {"books","books chapters"};
		
		Integer publishers = rs.getCommunitiesCount(Arrays.asList(p));
		System.out.println("Publishers: " + publishers);
		Integer titles = rs.getCommunitiesCount(Arrays.asList(q));
		System.out.println("Titles: " + titles);
		
		String s = "Politics & Government";
		List<String> usedHandles = new ArrayList<>();
		DSpaceItem item = rs.featuredItemForSubject(s,usedHandles);
		System.out.println(item);
	}
	
}
