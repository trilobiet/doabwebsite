package com.trilobiet.doabooks.website.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.trilobiet.graphqlweb.dao.DaoException;


@ResponseStatus(
	value=HttpStatus.NOT_FOUND,
	reason="Resource Not Found"
)
public class ResourceNotFoundException extends DaoException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(Exception root) {
		super(root);
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException() {
		super();
	}
}
