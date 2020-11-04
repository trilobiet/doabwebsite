package com.trilobiet.doabooks.website.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.trilobiet.doabooks.website.helpers.OapenMenuParser;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.section.SectionImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.SectionService;

/**
 * 
 * @author acdhirr
 */
@ControllerAdvice
public class GlobalAdvice {
	
	@Autowired
	protected SectionService<SectionImp> sectionService;
	
	@Autowired
	protected Environment environment;	

	@ModelAttribute(name="settings")
	public Map<String, String> getSettings() {
		
		Map<String, String> settings = new HashMap<>();
		settings.put("dspace_site", environment.getProperty("url_dspace_site"));
		settings.put("dspace_api", environment.getProperty("url_dspace_api"));
		settings.put("google_analytics_id", environment.getProperty("google_analytics_id"));
		settings.put("dspace_subject_path", environment.getProperty("dspace_subject_path"));
		return settings;
	}
	
	// Add navigation to all model-views
    @ModelAttribute(name="navigation")
    public void addAttributes( Model model ) throws Exception {
    	
		List<SectionImp> sections = sectionService.getSections();
		
		OapenMenuParser<SectionImp> menuparser = new OapenMenuParser<>(sections);
		model.addAttribute("headerSections", menuparser.getSectionsForHeader());
		model.addAttribute("menuLeftSections", menuparser.getSectionsForMainLeft());
		model.addAttribute("menuRightSections", menuparser.getSectionsForMainRight());
		model.addAttribute("footerSections", menuparser.getSectionsForFooter());
    }
    
}

