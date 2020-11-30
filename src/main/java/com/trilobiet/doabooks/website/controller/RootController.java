package com.trilobiet.doabooks.website.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.trilobiet.doabooks.website.helpers.SnippetMessageSource;

@Controller
public class RootController {
	
	protected final Logger log = LogManager.getLogger(this.getClass());
	
	@Autowired
	SnippetMessageSource messageSource;

	/** 
	 * Make it look like favicon is in the root
	 * 
	 * @return
	 */
	@RequestMapping("/favicon.ico")
	public ModelAndView showFavicon() {
		return new ModelAndView("forward:/static-assets/images/favicon.ico");
	}
	
	@CacheEvict(value={
		"sectionsCache","topicsCache","articlesCache",
		"filesCache","snippetsCache","translationsCache",
		"rssCache"}, allEntries=true)
	@RequestMapping("/clearCache") 
	public ModelAndView clearCache(Model model) {
		log.info("Cache cleared");
		messageSource.init();
		return new ModelAndView("redirect:/home");
	}
	
}
