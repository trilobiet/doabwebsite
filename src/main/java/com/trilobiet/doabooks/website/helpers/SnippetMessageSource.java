package com.trilobiet.doabooks.website.helpers;

import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.AbstractMessageSource;

import com.trilobiet.graphqlweb.dao.DaoException;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.service.SnippetService;
import com.trilobiet.graphqlweb.implementations.aexpgraphql2.snippet.SnippetImp;

/**
 * Alternative graphqlweb CMS message supplier for i18n.
 * 
 * This MessageSource takes care of message files stored as snippets 
 * named messages_fr.properties, messages_en.properties etc. 
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
public class SnippetMessageSource extends AbstractMessageSource {

	// Use Snippets to store i18n language message files like messages_fr.properties
	
	private SnippetService<SnippetImp> snippetService;
	private Map<String,Properties> translations = new HashMap<>(); 

	protected final Logger log = LogManager.getLogger(this.getClass());
	private final String languages; 
		
	public SnippetMessageSource(SnippetService<SnippetImp> service, String languages) {
		this.languages = languages;
		this.snippetService = service;
		init();
	}

	@Override
	protected MessageFormat resolveCode(String key, Locale locale) {
		
		Properties languageProps = translations.getOrDefault(locale.getLanguage(),new Properties());
		String message = languageProps.getProperty(key, "key not found...");
		return new MessageFormat(message, locale);
	}
	
	public void init() {
	
		Arrays.asList(languages.split(","))
			.stream()
			.map(l -> l.trim())
			.forEach(l -> {
				loadLanguageTable(l);
			});
	}
	
	private void loadLanguageTable(String language) {
		
		final String snippet = "messages_" + language + ".properties";
		
		String properties = "";
		
		try {
			Optional<SnippetImp> s = snippetService.getSnippet(snippet);
			if (s.isPresent()) properties = s.get().getCode(); 
		} catch (DaoException e) {
			log.error(e.getMessage());
		}
		
		translations.put(language, parsePropertiesString(properties));
		// System.out.println(translations);
	}

	private Properties parsePropertiesString(String s) {
		
	    final Properties p = new Properties();
	    try {
			p.load(new StringReader(s));
		} catch (IOException e) { 
			log.error(e.getMessage());
		}
	    return p;
	}
	
}
