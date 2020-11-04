package com.trilobiet.doabooks.website.repositoryclient;

public class RepositoryException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public RepositoryException(Exception root) {
		super(root);
	}

	public RepositoryException(String message) {
		super(message);
	}
	
	public RepositoryException() {
		super();
	}	

}
