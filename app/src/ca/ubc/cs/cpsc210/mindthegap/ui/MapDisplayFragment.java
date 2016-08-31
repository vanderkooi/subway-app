package ca.ubc.cs.cpsc210.mindthegap.ui;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import ca.ubc.cs.cpsc210.mindthegap.R;
import ca.ubc.cs.cpsc210.mindthegap.TfL.AndroidFileDataProvider;
import ca.ubc.cs.cpsc210.mindthegap.TfL.DataProvider;
import ca.ubc.cs.cpsc210.mindthegap.model.Line;
import ca.ubc.cs.cpsc210.mindthegap.model.LineResourceData;
import ca.ubc.cs.cpsc210.mindthegap.model.Station;
import ca.ubc.cs.cpsc210.mindthegap.model.StationManager;
import ca.ubc.cs.cpsc210.mindthegap.parsers.TfLLineParser;
import ca.ubc.cs.cpsc210.mindthegap.util.LatLon;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a fragment used to display the map to the user
 */
public class MapDisplayFragment extends Fragment implements IMyLocationConsumer {
    private static final String MDF_TAG = "MDF_TAG";
    /** minimum change in distance to trigger update of user location */
    private static final float MIN_UPDATE_DISTANCE = 50.0f;
    /** zoom level for map */
    protected int zoomLevel = 13;
    /** centre of map */
    private GeoPoint mapCentre = new GeoPoint(51.5012385,-0.1269373);
    /** the map view */
    private MapView mapView;
    /** location provider used to respond to changes in user location */
    private GpsMyLocationProvider locnProvider;
    /** station manager */
    private StationManager stnManager;
    /** location listener used to respond to changes in user location */
    private LocationListener locationListener;
    /** overlay manager */
    private TfLOverlayManager tflOverlayManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(MDF_TAG, "onCreate");
        stnManager = StationManager.getInstance();
        locnProvider = new GpsMyLocationProvider(getActivity());
        locnProvider.setLocationUpdateMinDistance(MIN_UPDATE_DISTANCE);
        parseLines();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        locationListener = (LocationListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final int TILE_SIZE = 256;
        Log.i(MDF_TAG, "onCreateView");

        if (savedInstanceState != null) {
            Log.i(MDF_TAG, "restoring from instance state");
            mapCentre = new GeoPoint(savedInstanceState.getDouble(getString(R.string.lat_key)),
                    savedInstanceState.getDouble(getString(R.string.lon_key)));
            zoomLevel = savedInstanceState.getInt(getString(R.string.zoom_key));
        }
        else {
            Log.i(MDF_TAG, "savedInstanceState is null");
        }

        if (mapView == null) {
            mapView = new MapView(getActivity(), TILE_SIZE);
            mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
            mapView.setClickable(true);
            mapView.setBuiltInZoomControls(true);
            mapView.setMapListener(new TubeLineListener());

            GpsMyLocationProvider mapLocationProvider = new GpsMyLocationProvider(getActivity());
            locnProvider.setLocationUpdateMinDistance(MIN_UPDATE_DISTANCE);
            tflOverlayManager = new TfLOverlayManager(this, mapView, mapLocationProvider);

            // set default view for map
            final IMapController mapController = mapView.getController();

            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    else
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    mapController.setZoom(zoomLevel);
                    mapController.setCenter(mapCentre);
                }
            });
        }

        return mapView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(MDF_TAG, "onSaveInstanceState");

        outState.putDouble(getString(R.string.lat_key), mapView.getMapCenter().getLatitude());
        outState.putDouble(getString(R.string.lon_key), mapView.getMapCenter().getLongitude());
        outState.putInt(getString(R.string.zoom_key), mapView.getZoomLevel());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(MDF_TAG, "onResume");
        locnProvider.startLocationProvider(this);
        mapView.setBuiltInZoomControls(true);

        Location lastKnownLocation = locnProvider.getLastKnownLocation();
        if (lastKnownLocation != null) {
            Log.i(MDF_TAG, "Restored from last known location");
            handleLocationChange(lastKnownLocation);
        }
        else {
            Log.i(MDF_TAG, "Location cannot be recovered");
        }

        tflOverlayManager.resumeLocnPlotting();
        tflOverlayManager.updateOverlays();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(MDF_TAG, "onPause");
        tflOverlayManager.disableLocnPlotting();
        locnProvider.stopLocationProvider();
        mapView.setBuiltInZoomControls(false);
    }


    /**
     * Parse line data from files and add all stations on lines parsed to station manager.
     * Files containing line data are specified in LineMetaData enumeration.
     */
    private void parseLines() {
        for(LineResourceData lineResourceData : LineResourceData.values()) {
            Line line = parseLine(lineResourceData);
            if(line != null) {
                stnManager.addStationsOnLine(line);
            }
        }
    }

    /**
     * Parse single line from file
     *
     * @param lineResourceData   meta data for line to be parsed
     * @return               line parsed from file
     */
    private Line parseLine(LineResourceData lineResourceData) {
        String fileName = lineResourceData.getFileName();
        String filePrefix = fileName.substring(0, fileName.lastIndexOf("."));

        DataProvider dataProvider = new AndroidFileDataProvider(getActivity(), filePrefix);
        Line line = null;

        try {
            String lineData = dataProvider.dataSourceToString();
            line = TfLLineParser.parseLine(lineResourceData, lineData);
        } catch (Exception e) {
            Log.e(MDF_TAG, e.getMessage(), e);
            Toast.makeText(getActivity(), "Unable to display " + lineResourceData + " line", Toast.LENGTH_LONG).show();
        }

        return line;
    }

    /**
     * Find nearest station to user, update nearest station text view and update markers on user location change
     *
     * @param location   the location of the user
     */
    private void handleLocationChange(Location location) {
        LatLon point = new LatLon(location.getLatitude(), location.getLongitude());
        Station closest = StationManager.getInstance().findNearestTo(point);
        locationListener.onLocationChanged(closest);
        tflOverlayManager.updateMarkerOfNearest(closest);

    }


    /**
     * Called when user's location has changed - handle location change and repaint map
     *
     * @param location               user's location
     * @param iMyLocationProvider    location provider
     */
    @Override
    public void onLocationChanged(Location location, IMyLocationProvider iMyLocationProvider) {
        Log.i(MDF_TAG, "onLocationChanged");

        handleLocationChange(location);
        mapView.invalidate();
    }

    /**
     * Custom listener for zoom events.  Changes width of line used to plot
     * tube line based on zoom level.
     */
    private class TubeLineListener implements MapListener {

        @Override
        public boolean onScroll(ScrollEvent scrollEvent) {
            return false;
        }

        @Override
        public boolean onZoom(ZoomEvent zoomEvent) {
            tflOverlayManager.replotTubeLines();
            return false;
        }
    }
}
