package com.trilobiet.doabooks.website.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.trilobiet.doabooks.website.helpers.Translator;
import com.trilobiet.doabooks.website.repositoryclient.DSpaceRepositoryProxy;
import com.trilobiet.doabooks.website.repositoryclient.DSpaceRepositoryService;
import com.trilobiet.doabooks.website.repositoryclient.RepositoryService;
import com.trilobiet.doabooks.website.rss.RssService;
import com.trilobiet.doabooks.website.rss.RssServiceImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.article.ArticleImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.file.FileImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.section.SectionImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.html.HtmlArticleService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.html.HtmlFileService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.html.HtmlSectionService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.html.HtmlSnippetService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.html.HtmlTopicService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.snippet.SnippetImp;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.topic.TopicImp;
import com.trilobiet.graphqlweb.markdown2html.Flexmark2HtmlFunction;
import com.trilobiet.graphqlweb.markdown2html.Md2HtmlArticleConverter;
import com.trilobiet.graphqlweb.markdown2html.Md2HtmlConverter;
import com.trilobiet.graphqlweb.markdown2html.Md2HtmlSectionConverter;
import com.trilobiet.graphqlweb.markdown2html.Md2HtmlSnippetConverter;
import com.trilobiet.graphqlweb.markdown2html.Md2HtmlTopicConverter;
import com.trilobiet.graphqlweb.markdown2html.StringFunction;

@Configuration
@ComponentScan (
	basePackages = {"com.trilobiet.doabooks.website"},
	excludeFilters = {
			@Filter( type=FilterType.ANNOTATION, value=EnableWebMvc.class ) 
	}
)
public class RootConfiguration {
	
	@Autowired
	public Environment env;	
	
	@Bean
	public StringFunction markdownflavour() {
		return new Flexmark2HtmlFunction();
	}
	
	@Bean 
	public Md2HtmlConverter<SectionImp> sectionMdConverter() {
		return new Md2HtmlSectionConverter<SectionImp>( markdownflavour() );
	}

	@Bean(name="sectionService")
	public HtmlSectionService<SectionImp> sectionService() {
		return new HtmlSectionService<>( env.getProperty("url_strapi"), sectionMdConverter());
	}
	

	@Bean 
	public Md2HtmlConverter<TopicImp> topicMdConverter() {
		return new Md2HtmlTopicConverter<TopicImp>( markdownflavour() );
	}

	@Bean 
	public HtmlTopicService<TopicImp> topicService() {
		return new HtmlTopicService<>( env.getProperty("url_strapi"), topicMdConverter() );
	}
	
	@Bean 
	public Md2HtmlConverter<ArticleImp> articleMdConverter() {
		return new Md2HtmlArticleConverter<ArticleImp>( markdownflavour() );
	}
	
	@Bean 
	public HtmlArticleService<ArticleImp> articleService() {
		return new HtmlArticleService<>( env.getProperty("url_strapi"), articleMdConverter() );
	}
	

	@Bean 
	public Md2HtmlConverter<SnippetImp> snippetMdConverter() {
		return new Md2HtmlSnippetConverter<SnippetImp>( markdownflavour() );
	}

	@Bean 
	public HtmlSnippetService<SnippetImp> snippetService() {
		return new HtmlSnippetService<>( env.getProperty("url_strapi"), snippetMdConverter() );
	}
	
	@Bean 
	public HtmlFileService<FileImp> fileService() {
		return new HtmlFileService<>( env.getProperty("url_strapi") );
	}
	
	@Bean(name="translator") // may use this bean in (thymeleaf) views by its name
	public Translator<SectionImp,TopicImp,ArticleImp> translator() {
		return new Translator<>();
	}
	
	@Bean(name="rssBlogService") 
	public RssService rssBlogService() {
		return new RssServiceImp(env.getProperty("url_feed_hypotheses"));
	}

	/* Not used anymore
	@Bean(name="rssMailchimpService") 
	public RssService rssMailchimpService() {
		return new RssServiceImp(env.getProperty("url_feed_mailchimp"));
	} */
	
	@Bean(initMethod="init",destroyMethod="exit" )
	public RepositoryService repositoryService() {
		// wrap in a proxy that caches request
		return new DSpaceRepositoryProxy(
			new DSpaceRepositoryService(env.getProperty("url_dspace_api")), 
			120 // refresh interval in minutes (default 60)
		);
	}
	
	
}

