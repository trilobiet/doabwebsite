package com.trilobiet.doabooks.website.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.trilobiet.doabooks.website.repositoryclient.RepositoryItem;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.snippet.SnippetImp;


@Controller
public class DSpaceController extends BaseController {
	
	@RequestMapping("/{language}/featuredtitles")
	public ModelAndView showFeatured() {
		
		ModelAndView mv = new ModelAndView("include/featuredtitles");  

		// Featured items: 1 title for each top subject
		try {
			Optional<SnippetImp> osnippet = snippetService.getSnippet("top-subjects");
			SnippetImp snippet = osnippet.orElseGet(()->new SnippetImp());
			List<String> subjects = Arrays.asList( snippet.getCode().split("\\r?\\n") );
			List<RepositoryItem> featuredItems = repositoryService.getFeaturedItems(subjects);
			mv.addObject("topSubjects", subjects);
			mv.addObject("featuredItems", featuredItems);
		} catch (Exception e) {
			log.error(e);
		}
		
		return mv;
	}

}
