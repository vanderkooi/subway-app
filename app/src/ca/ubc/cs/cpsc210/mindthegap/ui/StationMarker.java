package ca.ubc.cs.cpsc210.mindthegap.ui;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

import android.view.MotionEvent;
import ca.ubc.cs.cpsc210.mindthegap.model.Station;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.views.MapView;

// Represents a marker used to mark a station on the map
public class StationMarker extends Marker {
    private TfLOverlayManager tfLOverlayManager;

    public StationMarker(MapView v, TfLOverlayManager tfLOverlayManager) {
        super(v);
        this.tfLOverlayManager = tfLOverlayManager;
    }

    @Override
    public boolean onLongPress(MotionEvent event, MapView mapView) {
        boolean touched = this.hitTest(event, mapView);

        if (touched) {
            Station longPressedStn = (Station) getRelatedObject();
            tfLOverlayManager.onStationMarkerLongPress(longPressedStn);
        }

        return touched;
    }
}
