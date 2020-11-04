package com.trilobiet.doabooks.website.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Generic error handler
 *  
 * @author acdhirr
 */

@ControllerAdvice
public class GlobalExceptionHandler {

	protected final Logger log = LogManager.getLogger(this.getClass());
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleAllException(Exception ex) {

		ModelAndView mv = new ModelAndView("error/othererror");
		mv.addObject("error", ex);
		log.error( ex );
		ex.printStackTrace();
		return mv;
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleSomethingNotFound(HttpServletRequest req, HttpServletResponse res, Exception ex) {
		
		log.error("Not Found: " + req.getRequestURI().toString());
		log.debug( ex );
		return "error/notfound";
	}
	
}

