package com.trilobiet.doabooks.website.controller;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.trilobiet.graphqlweb.datamodel.ArticleOutline;
import com.trilobiet.graphqlweb.helpers.CmsUtils;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.article.ArticleImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.section.SectionImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.topic.TopicImp;

@Controller
public class ArticleController extends BaseController {

	@RequestMapping({
		"/{language}/{sectionslug}/{topicslug}/article/{slug}",
		"/{language}/{sectionslug}/article/{slug}",
		"/{language}/article/{slug}"
	})
	public ModelAndView showArticle(
			@PathVariable( "slug" ) String slug,
			@PathVariable(required=false, name="topicslug" ) String topicslug,
			@PathVariable(required=false, name="sectionslug" ) String sectionslug
		) throws Exception {
		
		ModelAndView mv = new ModelAndView("article");
		
		ArticleImp article = articleService.getArticleBySlug(slug)
				.orElseThrow( () -> new ResourceNotFoundException() );
		mv.addObject("article", article);
		mv.addObject( "bodyClass", CmsUtils.getCssClass(article) );
		
		// Filter only for current language
		Set<ArticleOutline> linked = translator.forLanguage(articleService.getLinked(article),language);
		mv.addObject("linked", linked);
		
		if(topicslug != null) {
			Optional<TopicImp> otopic = topicService.getTopicBySlug(topicslug);
			mv.addObject("topic",otopic.orElse(new TopicImp()));
		}

		if(sectionslug != null) {
			Optional<SectionImp> osection = sectionService.getSectionBySlug(sectionslug);
			mv.addObject("section",osection.orElse(new SectionImp()));
		}
		
		mv.addObject("sectionprefix", sectionPrefix( sectionslug ));
		
		return mv;
	}
	
}
