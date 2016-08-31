package ca.ubc.cs.cpsc210.mindthegap.model.exception;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

/*
 * Represents exception raised when errors occur with arrival estimates
 */
public class ArrivalException extends Exception {
    public ArrivalException() {
        super();
    }

    public ArrivalException(String msg) {
        super(msg);
    }
}
