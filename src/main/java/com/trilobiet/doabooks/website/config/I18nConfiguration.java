package com.trilobiet.doabooks.website.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.trilobiet.doabooks.website.helpers.SnippetMessageSource;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.SnippetService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.snippet.SnippetImp;

@Configuration
@ComponentScan("com.trilobiet.doabooks.website")
public class I18nConfiguration //extends WebMvcConfigurerAdapter 
{

	@Autowired
	protected Environment env;	
	
	@Autowired 
	protected SnippetService<SnippetImp> snippetService;
	
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("en"));
        return localeResolver;
    }
	
    /*
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }
    */
    
    /*@Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        //messageSource.setBasename("WEB-INF/i18n/messages");
        messageSource.setBasename("classpath:/i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }*/

    @Bean//(initMethod="init")
    public MessageSource messageSource() {
        return new SnippetMessageSource(snippetService, env.getProperty("supportedLanguages"));
    }

    /*
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }*/	
	
	
}
