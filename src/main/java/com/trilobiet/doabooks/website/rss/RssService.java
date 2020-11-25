package com.trilobiet.oapen.oapenwebsite.rss;

import java.util.List;
import java.util.Optional;

public interface RssService {
	
	/**
	 * Get [count] items from Rss feed
	 * @param count
	 * @return
	 * @throws RssException
	 */
	List<RssItem> getItems(int count) throws RssException;
	
	/**
	 * Get item from Rss feed by link value
	 * @param items
	 * @param link
	 * @return
	 */
	Optional<RssItem> getItemByLink(List<RssItem> items, String link);

}
