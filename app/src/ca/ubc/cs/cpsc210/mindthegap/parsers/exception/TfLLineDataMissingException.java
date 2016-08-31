package ca.ubc.cs.cpsc210.mindthegap.parsers.exception;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

/**
 * Represents exception raised when expected data is missing from TfL Line JSON data.
 */
public class TfLLineDataMissingException extends Exception {
    public TfLLineDataMissingException() {
        super();
    }

    public TfLLineDataMissingException(String msg) {
        super(msg);
    }
}
