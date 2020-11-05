/**
 * 
 */
package com.trilobiet.doabooks.website.repositoryclient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author acdhirr
 * 
 * Delegate calls to the DSpace API to a background process. 
 * Calls to the DSpace API may comprise several combined queries 
 * and thus be slow. This proxy periodically overwrites query results 
 * with newer data that can then be picked from a lookup table (hashmap) 
 * by clients, without delay.
 * 
 * Caching must be disabled on the proxied service since it 
 * no longer serves any purpose.
 * 
 * Initialization is lazy, since the keys of the lookup table are  
 * set with the arguments from the clients' request.  
 *   
 */
public class DSpaceRepositoryProxy implements RepositoryService {
	
	private final DSpaceRepositoryService drs;
	private int refreshMinutes = 60; //
	private Map<List<String>,List<RepositoryItem>> featuredItems;
	private Map<List<String>,Integer> communitiesCount;
	private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	
	protected final Logger log = LogManager.getLogger(this.getClass());
	
	public DSpaceRepositoryProxy(DSpaceRepositoryService drs) {
		this.drs = drs;
		this.featuredItems = new HashMap<>();
		this.communitiesCount = new HashMap<>();
	}

	public DSpaceRepositoryProxy(DSpaceRepositoryService drs, int refreshMinutes) {
		this.drs = drs;
		this.refreshMinutes = refreshMinutes;
		this.featuredItems = new HashMap<>();
		this.communitiesCount = new HashMap<>();
	}
	
	/**
	 * Define a background process that periodically updates 
	 * featured items and communities count (which comprise
	 * numerous slow queries). The results are put in a Hashmap 
	 * with the request arguments as key,
	 * and available without delay. 
	 */
	public void init() {
		
		Runnable r = new Runnable() {

			@Override
			public void run() {
				
				log.info("Populating featuredItems and communitiesCount ");
				
				/* These streams will be initialized on the first request.
				 * We don't know the keys yet - they must be set by a caller.
				*/
				featuredItems.keySet().stream()
					.forEach(k -> populateFeaturedItems(k) );
				communitiesCount.keySet().stream()
					.forEach(k -> populateCommunitiesCount(k) );
			}
		};
		
		executorService.scheduleAtFixedRate(r, 0, refreshMinutes, TimeUnit.MINUTES);
	}

	@Override
	public List<RepositoryItem> getFeaturedItems(List<String> subjects) {
		
		if (!featuredItems.containsKey(subjects)) populateFeaturedItems(subjects);
		return featuredItems.get(subjects);
	}

	@Override
	public Integer getCommunitiesCount(List<String> communities) {
		
		if (!communitiesCount.containsKey(communities)) populateCommunitiesCount(communities); 
		return communitiesCount.get(communities);
	}
	
	private synchronized void populateFeaturedItems(List<String> subjects) {
		
		if (!subjects.isEmpty()) try {
			List<RepositoryItem> items = drs.getFeaturedItems(subjects);
			featuredItems.put(subjects, items);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void populateCommunitiesCount(List<String> communities) {
		
		if (!communities.isEmpty()) try {
			Integer count = drs.getCommunitiesCount(communities);
			communitiesCount.put(communities, count);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	
	public void exit() {
		
		executorService.shutdown(); // Disable new tasks from being submitted
		
		try {
			// Wait a while for existing tasks to terminate
			boolean isShutdown = executorService.awaitTermination(60, TimeUnit.SECONDS);
			
			if ( !isShutdown ) {
				executorService.shutdownNow(); // Cancel currently executing tasks
				log.info("Called shutdownNow() after waiting 60 secs - some tasks may have been aborted.");
			}
			else {
				log.info("Shutdown OK - daemon stopped - scheduled tasks cleaned up.");
			}
			
		} catch (InterruptedException ie) {
			
			executorService.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
			log.info("Called shutdownNow() - waiting was interrupted - some tasks may have been aborted.");
		}        		
	}	
	
	
}
