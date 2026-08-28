package com.smartride.exception;


public class FareConfigNotFoundException extends RuntimeException {
    public FareConfigNotFoundException() {
        super("No active fare configuration found. Please set up fare config.");
    }
}