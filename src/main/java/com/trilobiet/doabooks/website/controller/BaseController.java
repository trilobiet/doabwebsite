package com.trilobiet.doabooks.website.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.LocaleResolver;

import com.trilobiet.doabooks.website.helpers.Translator;
import com.trilobiet.doabooks.website.repositoryclient.RepositoryService;
import com.trilobiet.doabooks.website.rss.RssService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.article.ArticleImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.file.FileImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.section.SectionImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.ArticleService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.FileService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.SectionService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.SnippetService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.TopicService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.snippet.SnippetImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.topic.TopicImp;

@Controller
public class BaseController {

	protected final Logger log = LogManager.getLogger(this.getClass());
	
	@Autowired
	protected SectionService<SectionImp> sectionService;

	@Autowired
	protected TopicService<TopicImp> topicService;

	@Autowired
	protected ArticleService<ArticleImp> articleService;
	
	@Autowired
	protected SnippetService<SnippetImp> snippetService;

	@Autowired
	protected FileService<FileImp> fileService;
	
	@Autowired
	protected RepositoryService repositoryService;

	@Autowired
	protected RssService rssBlogService;	

	@Autowired
	protected RssService rssMailchimpService;	

	@Autowired
	protected Environment environment;	

	@Autowired
	protected Translator<SectionImp,TopicImp,ArticleImp> translator;	
	
	@Autowired
	protected LocaleResolver localeResolver;
	
	protected final String DEFAULT_LANGUAGE = "en";
	protected List<String> allowedLanguages;
	protected String language = DEFAULT_LANGUAGE;
	
	@PostConstruct
	public void initialize() {
		// System.out.println("post construct");
		allowedLanguages = Arrays.asList(environment.getProperty("supportedLanguages").split(","));
	}	
	
	
	/**
	 * Use to prepend section to prevnext links
	 * 
	 * @param sectionslug
	 * @return
	 */
	protected String sectionPrefix(String sectionslug) {
		
		StringBuilder sb = new StringBuilder();
		
		if (sectionslug != null) {
			sb.append("/").append(sectionslug);
		}
		
		return sb.append("/article/").toString();
	}
	
	
    // Currently active language, or a default when unspecified
    @ModelAttribute(name="language")
    public void addLanguage(
    	Model model,
    	HttpServletRequest request, HttpServletResponse response,  
    	@PathVariable( name="language", required=false ) Optional<String> lang
    ) {
    	language = getLanguage(lang);
    	model.addAttribute("language",language);
    	localeResolver.setLocale(request, response, new Locale(language));
    }
	
    /*
     * Return only allowed languages, or a default language
     * otherwise the cache may be populated with non-existing language requests 
     */
    private String getLanguage(Optional<String> langChoice) {
    	
    	if (langChoice.isEmpty()) return DEFAULT_LANGUAGE;
    	else {
    		if (allowedLanguages.contains(langChoice.get())) return langChoice.get();
    		else return DEFAULT_LANGUAGE;		
    	}
    }	
	
    
}
