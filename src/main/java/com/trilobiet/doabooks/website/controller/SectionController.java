package com.trilobiet.doabooks.website.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.trilobiet.graphqlweb.helpers.CmsUtils;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.section.SectionImp;

@Controller
public class SectionController extends BaseController {

	@RequestMapping({
		"/{language}/{slug}"
	}) 
	public ModelAndView showTopic(
			@PathVariable( "slug" ) String slug,
			@PathVariable( "language" ) Optional<String> language
		) throws Exception {
		
		ModelAndView mv = new ModelAndView("section");

		SectionImp section = sectionService.getSectionBySlug(slug)
				.orElseThrow(ResourceNotFoundException::new);
		
		mv.addObject("section", section);
		mv.addObject("bodyClass", CmsUtils.getCssClass(section) );
		
		return mv;
	}
	
}
