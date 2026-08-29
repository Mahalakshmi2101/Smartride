package com.smartride.exception;


public class FareConfigNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public FareConfigNotFoundException() {
        super("No active fare configuration found. Please set up fare config.");
        
    }
}