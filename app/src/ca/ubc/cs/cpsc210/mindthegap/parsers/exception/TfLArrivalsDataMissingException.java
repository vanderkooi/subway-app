package ca.ubc.cs.cpsc210.mindthegap.parsers.exception;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

/**
 * Represents exception raised when expected data is missing from TfL Arrivals JSON response.
 */
public class TfLArrivalsDataMissingException extends Exception {
    public TfLArrivalsDataMissingException() {
        super();
    }

    public TfLArrivalsDataMissingException(String msg) {
        super(msg);
    }
}
