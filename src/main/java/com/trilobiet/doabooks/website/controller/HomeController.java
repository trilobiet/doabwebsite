package com.trilobiet.doabooks.website.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.trilobiet.doabooks.website.rss.RssItem;
import com.trilobiet.graphqlweb.datamodel.Snippet;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.article.ArticleImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.section.SectionImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.snippet.SnippetImp;

@Controller
public class HomeController extends BaseController {

	@RequestMapping({
		"/home","/{language}","/home/{language}"
	})
	public ModelAndView showHome(HttpServletRequest request) {
		
		ModelAndView mv = new ModelAndView("home"); 

		mv.addObject("ip",request.getRemoteAddr());
		
		// Catch all errors here: home page must always be rendered, erroneous sections can be empty
		
		try {
			Optional<SnippetImp> introText = snippetService.getSnippet("home-intro-"+language);
			if (introText.isPresent() ) mv.addObject("home_intro",introText.get().getCode());
		} catch (Exception e) {
			log.error("home-intro: " + e.getMessage());
		}
		
		// Totals books & publishers
		try {
			Integer countTitles = repositoryService.getCommunitiesCount(
				Arrays.asList(environment.getProperty("communities_with_titles").split(","))	
			);
			mv.addObject("count_titles",countTitles);
			Integer countPublishers = repositoryService.getCommunitiesCount(
				Arrays.asList(environment.getProperty("communities_with_publishers").split(","))	
			);
			mv.addObject("count_publishers",countPublishers);			
		} catch (Exception e) {	
			log.error("communities: " + e.getMessage());
		}
		
		try {
			// Filter only for current language
			List<ArticleImp> showcases = 
				translator.forLanguage(articleService.getByFieldContainsValue("params", "showcase=true"),language);
			mv.addObject("showcases",showcases);
		} catch (Exception e) {
			log.error("showcases: " + e.getMessage());
		}
		
		try {
			// Filter only for current language
			List<ArticleImp> spotlights = 
				translator.forLanguage(articleService.getByFieldValue("spotlight", "true"),language);
			mv.addObject("spotlights",spotlights);
		} catch (Exception e) {
			log.error("spotlights: " + e.getMessage());
		}
		
		try {
			// Get latest hypotheses blog post(s)
			List<RssItem> rssItems = rssBlogService.getItems(2);
			mv.addObject("rssHypothesesItems",rssItems);
		} catch (Exception e) {
			log.error("hypotheses: " + e.getMessage());
		}
		
		try {
			Optional<SnippetImp> otwitter = snippetService.getSnippet("twitter-timeline");
			Snippet ttl = otwitter.orElseGet(()->new SnippetImp());
			mv.addObject("twittertimeline",ttl);
		} catch (Exception e) {
			log.error("twitter: " + e.getMessage());
		}
		
		return mv;
	}
	
	@RequestMapping("/sitemap") 
	public ModelAndView showSitemap() throws Exception {
		
		ModelAndView mv = new ModelAndView("sitemap"); 
		List<SectionImp> sections = sectionService.getSections();
		mv.addObject("sections", sections);

		return mv;
	}
	
}
