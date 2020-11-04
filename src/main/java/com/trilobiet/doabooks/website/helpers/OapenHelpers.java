package com.trilobiet.doabooks.website.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.WordUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.trilobiet.graphqlweb.helpers.ViewHelpers;

@Component
public class OapenHelpers extends ViewHelpers {

	@Bean(name = "viewhelpers")
	public OapenHelpers thymeleafHelpers() {
	    return new OapenHelpers();
	}	

	public static String abbreviate(String input, int length) {
		
		return WordUtils.abbreviate(input, length, -1, "...");
	}
	
	/**
	 * https://stackoverflow.com/questions/31868404/how-to-abbreviate-string-at-the-middle-without-cutting-words
	 * 
	 * @param input
	 * @param middle
	 * @param length
	 * @return Abbreviated String
	 */
	public static String abbreviateMiddle(String input, String middle, int length) {
		
	    if (input != null && input.length() > length) {
	        int half = (length - middle.length()) / 2;

	        Pattern pattern = Pattern.compile(
	                "^(.{" + half + ",}?)" + "\\b.*\\b" + "(.{" + half + ",}?)$");
	        Matcher matcher = pattern.matcher(input);

	        if (matcher.matches()) {
	            return matcher.group(1) + middle + matcher.group(2);
	        }
	    }

	    return input;
	}	
	
}
