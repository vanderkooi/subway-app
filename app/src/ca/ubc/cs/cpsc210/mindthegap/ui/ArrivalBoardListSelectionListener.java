package ca.ubc.cs.cpsc210.mindthegap.ui;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

import ca.ubc.cs.cpsc210.mindthegap.model.ArrivalBoard;

/**
 * Handles user selection of an arrivals board
 */
public interface ArrivalBoardListSelectionListener {

    /**
     * Called when the user selects an arrivals board
     *
     * @param selected  the selected arrivals board
     */
    void onArrivalBoardListItemSelected(ArrivalBoard selected);
}
