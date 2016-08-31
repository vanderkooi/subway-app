package ca.ubc.cs.cpsc210.mindthegap.ui;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

import ca.ubc.cs.cpsc210.mindthegap.model.Station;

/**
 * Handles changes in user location
 */
public interface LocationListener {

    /**
     * Called when the user's location has changed
     *
     * @param nearest  station that is nearest to user (null if no station within StationManager.RADIUS metres)
     */
    void onLocationChanged(Station nearest);
}
