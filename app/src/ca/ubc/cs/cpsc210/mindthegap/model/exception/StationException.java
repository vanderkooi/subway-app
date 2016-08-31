package ca.ubc.cs.cpsc210.mindthegap.model.exception;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

/**
 * Represents exception raised when errors occur with Stations
 */
public class StationException extends Exception {
    public StationException() {
        super();
    }

    public StationException(String msg) {
        super(msg);
    }
}

