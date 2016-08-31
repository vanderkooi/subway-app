package ca.ubc.cs.cpsc210.mindthegap.ui;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

import ca.ubc.cs.cpsc210.mindthegap.model.Station;

/**
 * Handles user selection of station on map
 */
public interface StationSelectionListener {

    /**
     * Called when user selects a station
     *
     * @param stn   station selected by user
     */
    void onStationSelected(Station stn);
}
