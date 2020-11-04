package com.trilobiet.doabooks.website.helpers;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;

import com.trilobiet.graphqlweb.datamodel.Article;
import com.trilobiet.graphqlweb.datamodel.ArticleOutline;
import com.trilobiet.graphqlweb.datamodel.Section;
import com.trilobiet.graphqlweb.datamodel.Topic;
import com.trilobiet.graphqlweb.datamodel.Translation;

public class Translator<S extends Section, T extends Topic, A extends Article> {
	
	@Cacheable(value="translationsCache", key="#section.id + '-' + #language + '-sctn'")
	public CacheableTranslation translate(S section, String language) {
		
		Optional<Translation> translation = getTranslation(section.getTranslations(), language);
		
		// System.out.println("Translating section " + section.getName());
		if (translation.isPresent()) return new CacheableTranslation(translation.get());
		else return new CacheableTranslation(language, section.getName(),section.getDescription());
	}
	
	@Cacheable(value="translationsCache", key="#topic.id + '-' + #language + '-tpc'")
	public CacheableTranslation translate(T topic, String language) {
		
		Optional<Translation> translation = getTranslation(topic.getTranslations(), language);

		if (translation.isPresent()) return new CacheableTranslation(translation.get());
		else return new CacheableTranslation(language, topic.getName(),topic.getDescription());
	}
	
	private Optional<Translation> getTranslation(List<Translation> translations, String language) {
		
		return translations.stream()
			.filter(t -> t.getLanguageCode().equalsIgnoreCase(language))
			.findFirst();
	}
	
	@Cacheable(value="translationsCache", key="#topic.id + '-' + #language + '-articles'")
	public List<ArticleOutline> articlesFor(T topic, String language) {
		
		return topic.getArticles().stream()
			.filter(a -> a.getLanguage().equals(language))
			.collect(Collectors.toList());
	}
	
	public List<A> forLanguage(List<A> articles, String language) {
		
		return articles.stream()
				.filter(a -> a.getLanguage().equalsIgnoreCase(language))
				.collect(Collectors.toList());
	}

	public Set<ArticleOutline> forLanguage(Set<ArticleOutline> articles, String language) {
		
		return articles.stream()
				.filter(a -> a.getLanguage().equalsIgnoreCase(language))
				.collect(Collectors.toSet());
	}
}
