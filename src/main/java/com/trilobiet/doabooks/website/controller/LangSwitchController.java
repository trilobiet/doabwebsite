package com.trilobiet.doabooks.website.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LangSwitchController extends BaseController {
	
	@RequestMapping(value="/setlanguage",params={"lang"})
	public ModelAndView switchLanguage(
			@RequestHeader(value="referer", required=false) Optional<String> referer,
			@RequestParam(value="lang", required=false ) Optional<String> lang) {
		
		String newlanguage = lang.orElse(DEFAULT_LANGUAGE);
		
		return new ModelAndView("redirect:/home/"+newlanguage).addObject("language", newlanguage);
	}
	
}
