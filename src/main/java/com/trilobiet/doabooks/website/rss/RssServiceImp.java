package com.trilobiet.doabooks.website.rss;

import java.io.IOException;
import java.net.URL;
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
			URL url = new URL(feedUrl); 
			SyndFeedInput input = new SyndFeedInput();
			// input.setAllowDoctypes(true);
			// input.setXmlHealerOn(true);
			feed = input.build(new XmlReader(url));
			
		} catch (IllegalArgumentException | FeedException | IOException e) {
			
			throw new RssException( e );
		}
		
		return Optional.ofNullable(feed);
	}
	
	// Test
	public static void main(String[] args) {

		String url = "https://dariahopen.hypotheses.org/feed";
		
		RssServiceImp service = new RssServiceImp(url);
		
		try {
			List<RssItem> items = service.getItems(12);
			items.forEach(System.out::println);
			
		} catch (RssException e) {
			System.out.println(e);
		}
	}

}
