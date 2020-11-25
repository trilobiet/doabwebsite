package com.trilobiet.doabooks.website.config;

import java.text.MessageFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;

import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.SnippetService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.snippet.SnippetImp;

/**
 * Alternative Strapi CMS message supplier for i18n
 * 
 * @author acdhirr
 *
 */
/*  
 * TODO: implement
 *  
 * Edit I18nConfiguration as well. You must replace (remove) the messageSource bean. 
 * 
 * To make this class the message source add:
 * @Component("messageSource")
 */
// @Component("messageSource")
public class SnippetMessageSource extends AbstractMessageSource {

	// Use Snippets to store i18n language message files like messages_fr.properties
	
	@Autowired
	protected SnippetService<SnippetImp> snippetService;
	
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		
		// TODO implement 
		
		// TODO Auto-generated method stub
		return null;
	}

}
