package com.trilobiet.doabooks.website.rss;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

public class RssServiceImp implements RssService {
	
	private final String feedUrl;

	/**
	 * Constructor 
	 * @param feedUrl
	 */
	public RssServiceImp(String feedUrl) {
		this.feedUrl = feedUrl;
	}
	
	@Override
	public List<RssItem> getItems(int count) throws RssException {
		
		Optional<SyndFeed> feed = getFeed();
		List<RssItem> items = new ArrayList<>();

		feed.ifPresent( f -> {
			f.getEntries().stream().limit(count).forEach( e -> items.add( rssItem(e) ) );
		});

		return items;
	}
	
	@Override
	public List<RssItem> getItems(int count, List<String> categories) throws RssException {
		
		Optional<SyndFeed> feed = getFeed();
		List<RssItem> items = new ArrayList<>();
		
		feed.ifPresent( f -> {
			f.getEntries().stream()
			 .filter(e ->
			 	// show all, or only from listed categories (case sensitive!)
			  	categories.isEmpty() || e.getCategories().stream().anyMatch(c -> categories.contains(c.getName()))
			 )
			 .limit(count).forEach(e -> items.add(rssItem(e)));
		});

		return items;
	}
	
	@Override
	public Optional<RssItem> getItemByLink(List<RssItem> items, String link) {
		
		Optional<RssItem> oItem = items.parallelStream()
			.filter( itm -> itm.getLink().equals(link) )
			.findAny();
		
		return oItem;
	}

	/**
	 * Adaptor method returning an RssItem from a SyndEntry
	 * 
	 * @param entry
	 * @return
	 */
	private RssItem rssItem(SyndEntry entry) {
		
		RssItemImp.Builder builder = new RssItemImp.Builder( entry.getLink() )
			.setTitle( entry.getTitle())
			.setDescription( entry.getDescription().getValue())
			.setAuthor(entry.getAuthor())
			.setDate(entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		
		List<SyndContent> lst = entry.getContents();
		lst.stream()
			.filter(content -> content.getType().equalsIgnoreCase("html"))
			.findFirst()
			.ifPresent( content -> builder.setContents(content.getValue()) );
		
		return builder.build();
	}

	/**
	 * Fetch the feed
	 * 
	 * @return
	 * @throws RssException
	 */
	@Cacheable(value="rssCache", key="#root.target.feedUrl")
	private Optional<SyndFeed> getFeed() throws RssException {
		
		SyndFeed feed = null;
		
		try {
			URLConnection uc = new URL(feedUrl).openConnection();
			uc.setConnectTimeout(2000);
			uc.setReadTimeout(2000);
			SyndFeedInput input = new SyndFeedInput();
			feed = input.build(new XmlReader(uc));
			
		} catch (IllegalArgumentException | FeedException | IOException e) {
			
			throw new RssException( e );
		}
		
		return Optional.ofNullable(feed);
	}
	
	// Test
	public static void main(String[] args) {

		String url = "https://oapen.hypotheses.org/feed";
		
		RssServiceImp service = new RssServiceImp(url);
		List<String> cats = List.of("Uncategorized");
		
		try {
			List<RssItem> items = service.getItems(10,cats);
			items.forEach(System.out::println);
			
		} catch (RssException e) {
			System.out.println(e);
		}
	}

}
