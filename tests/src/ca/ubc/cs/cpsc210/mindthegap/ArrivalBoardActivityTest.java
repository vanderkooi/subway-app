package ca.ubc.cs.cpsc210.mindthegap;

import android.test.ActivityInstrumentationTestCase2;
import ca.ubc.cs.cpsc210.mindthegap.model.*;
import ca.ubc.cs.cpsc210.mindthegap.model.exception.ArrivalException;
import ca.ubc.cs.cpsc210.mindthegap.ui.ArrivalBoardListFragment;
import ca.ubc.cs.cpsc210.mindthegap.util.LatLon;

import java.util.List;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 */
public class ArrivalBoardActivityTest extends ActivityInstrumentationTestCase2<ArrivalBoardActivity> {
    private ArrivalBoardListFragment arrivalBoardListFragment;
    private Station stn;
    private Line victoria;

    public ArrivalBoardActivityTest() {
        super("ca.ubc.cs.cpsc210.mindthegap", ArrivalBoardActivity.class);
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
        arrivalBoardListFragment = (ArrivalBoardListFragment) getActivity().getFragmentManager().findFragmentByTag("ArrivalBoardListFragment");
    }

    public void testArrivalBoardsForSelectedStationNoArrivals() throws Exception {
        List<ArrivalBoard> arrivalBoards = arrivalBoardListFragment.getArrivalBoardsForSelectedStation();
        assertTrue("AB size", 0 == arrivalBoards.size());
    }

    public void testArrivalBoardsForSelectedStationSingleLine() throws Exception {
        addArrivalsSingleLine();
        List<ArrivalBoard> arrivalBoards = arrivalBoardListFragment.getArrivalBoardsForSelectedStation();
        assertTrue("AB size", 2 == arrivalBoards.size());
        assertTrue("Board Northbound Travel Direction", arrivalBoards.get(0).getTravelDirn().equals("Northbound"));
        assertTrue("Board Southbound Travel Direction", arrivalBoards.get(1).getTravelDirn().equals("Southbound"));
        assertTrue("Board Northbound", 1 == arrivalBoards.get(0).getNumArrivals());
        assertTrue("Board Southbound", 2 == arrivalBoards.get(1).getNumArrivals());
    }

    public void testArrivalBoardsForSelectedStationMultipleLines() throws Exception {
        addArrivalsMultiLine();
        List<ArrivalBoard> arrivalBoards = arrivalBoardListFragment.getArrivalBoardsForSelectedStation();
        assertTrue("AB size", 4 == arrivalBoards.size());
        assertTrue("Victoria Board Northbound Travel Direction", arrivalBoards.get(0).getTravelDirn().equals("Northbound"));
        assertTrue("Victoria Board Southbound Travel Direction", arrivalBoards.get(1).getTravelDirn().equals("Southbound"));
        assertTrue("Central Board Northbound Travel Direction", arrivalBoards.get(2).getTravelDirn().equals("Northbound"));
        assertTrue("Central Board Southbound Travel Direction", arrivalBoards.get(3).getTravelDirn().equals("Southbound"));
        assertTrue("Victoria Board Northbound", 2 == arrivalBoards.get(0).getNumArrivals());
        assertTrue("Victoria Board Southbound", 3 == arrivalBoards.get(1).getNumArrivals());
        assertTrue("Central Board Northbound", 1 == arrivalBoards.get(2).getNumArrivals());
        assertTrue("Central Board Southbound", 2 == arrivalBoards.get(3).getNumArrivals());
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
