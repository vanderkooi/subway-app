package ca.ubc.cs.cpsc210.mindthegap;

import android.test.ActivityInstrumentationTestCase2;
import ca.ubc.cs.cpsc210.mindthegap.model.*;
import ca.ubc.cs.cpsc210.mindthegap.model.exception.ArrivalException;
import ca.ubc.cs.cpsc210.mindthegap.ui.ArrivalsListFragment;
import ca.ubc.cs.cpsc210.mindthegap.util.LatLon;

import java.util.List;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 */
public class ArrivalsActivityTest extends ActivityInstrumentationTestCase2<ArrivalsActivity> {
    private ArrivalsListFragment arrivalListFragment;
    private Station stn;
    private Line victoria;

    public ArrivalsActivityTest() {
        super("ca.ubc.cs.cpsc210.mindthegap", ArrivalsActivity.class);
    }

    public void setUp() throws Exception {
        stn = new Station("stnID", "TestStation", new LatLon(51.5, -0.1));
        victoria = new Line(LineResourceData.VICTORIA, "victoria", "Victoria");
        victoria.addStation(stn);
        StationManager stnManager = StationManager.getInstance();
        stnManager.clearSelectedStation();
        stnManager.clearStations();
        stnManager.addStationsOnLine(victoria);
        stnManager.setSelected(stn);
        arrivalListFragment = (ArrivalsListFragment) getActivity().getFragmentManager().findFragmentByTag("ArrivalsListFragment");
    }

    public void testArrivalBoardsForSelectedStationNoArrivals() throws Exception {
        List<Arrival> arrivals = arrivalListFragment.getArrivalsForSelectedStationOnLineInDirection("victoria", "Northbound");
        assertTrue("Arrivals size", 0 == arrivals.size());
    }

    public void testArrivalBoardsForSelectedStationSingleLine() throws Exception {
        addArrivalsSingleLine();
        List<Arrival> arrivalsNorth = arrivalListFragment.getArrivalsForSelectedStationOnLineInDirection("victoria", "Northbound");
        assertTrue("Arrivals size", 1 == arrivalsNorth.size());
        List<Arrival> arrivalsSouth = arrivalListFragment.getArrivalsForSelectedStationOnLineInDirection("victoria", "Southbound");
        assertTrue("Arrivals size", 2 == arrivalsSouth.size());
    }

    public void testArrivalBoardsForSelectedStationMultipleLines() throws Exception {
        addArrivalsMultiLine();
        List<Arrival> arrivalsNorth = arrivalListFragment.getArrivalsForSelectedStationOnLineInDirection("victoria", "Northbound");
        assertTrue("Arrivals Victoria Northbound size", 2 == arrivalsNorth.size());
        List<Arrival> arrivalsSouth = arrivalListFragment.getArrivalsForSelectedStationOnLineInDirection("victoria", "Southbound");
        assertTrue("Arrivals Victoria Southbound size", 3 == arrivalsSouth.size());
        List<Arrival> arrivalsCentralNorth = arrivalListFragment.getArrivalsForSelectedStationOnLineInDirection("central", "Northbound");
        assertTrue("Arrivals Central Northbound size", 1 == arrivalsCentralNorth.size());
        List<Arrival> arrivalsCentralSouth = arrivalListFragment.getArrivalsForSelectedStationOnLineInDirection("central", "Southbound");
        assertTrue("Arrivals Central Southbound size", 2 == arrivalsCentralSouth.size());

    }

    private void addArrivalsSingleLine() throws ArrivalException {
        stn.addArrival(victoria, new Arrival(0, "D", "Northbound - Platform 2"));
        stn.addArrival(victoria, new Arrival(1, "E", "Southbound - Platform 3"));
        stn.addArrival(victoria, new Arrival(2, "E", "Southbound - Platform 3"));
    }

    private void addArrivalsMultiLine() throws ArrivalException {
        addArrivalsSingleLine();
        Line central = new Line(LineResourceData.CENTRAL, "central", "Central");
        stn.addLine(central);
        stn.addArrival(central, new Arrival(0, "D", "Northbound - Platform 2"));
        stn.addArrival(central, new Arrival(1, "E", "Southbound - Platform 3"));
        stn.addArrival(central, new Arrival(2, "E", "Southbound - Platform 3"));
        // add a few more to Victoria line (just to make sure that ordering doesn't matter)
        stn.addArrival(victoria, new Arrival(4, "D", "Northbound - Platform 2"));
        stn.addArrival(victoria, new Arrival(0, "E", "Southbound - Platform 3"));
    }
}
