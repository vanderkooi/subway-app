package ca.ubc.cs.cpsc210.mindthegap.parsers;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

import ca.ubc.cs.cpsc210.mindthegap.model.Arrival;
import ca.ubc.cs.cpsc210.mindthegap.model.Line;
import ca.ubc.cs.cpsc210.mindthegap.model.Station;
import ca.ubc.cs.cpsc210.mindthegap.model.exception.ArrivalException;
import ca.ubc.cs.cpsc210.mindthegap.parsers.exception.TfLArrivalsDataMissingException;

import javax.json.*;
import java.io.StringReader;
import java.util.Set;

/**
 * A parser for the data returned by the TfL station arrivals query
 */
public class TfLArrivalsParser extends TfLAbstractParser {

    /**
     * Parse arrivals from JSON response produced by TfL query.  Parsed arrivals are
     * added to given station assuming that arrival is on a line that passes
     * through the station and that corresponding JSON object has all of:
     * timeToStation, platformName, lineID and one of destinationName or towards.  If
     * any of the aforementioned elements is missing, or if arrival is on a line
     * that does not pass through the station, the arrival is not added to the station.
     *
     * @param stn             station to which parsed arrivals are to be added
     * @param jsonResponse    the JSON response produced by TfL
     * @throws JsonException  when JSON response does not have expected format
     * @throws TfLArrivalsDataMissingException  when all arrivals are missing at least one of the following:
     *      <ul>
     *          <li>timeToStation</li>
     *          <li>platformName</li>
     *          <li>lineId</li>
     *          <li>destinationName and towards</li>
     *      </ul>
     * or if all arrivals are on a line that does not run through given station.
     */
    public static void parseArrivals(Station stn, String jsonResponse)
            throws JsonException, TfLArrivalsDataMissingException {

        JsonReader reader = Json.createReader(new StringReader(jsonResponse));

        JsonArray arrivals = reader.readArray();
        int countMissing = 0;

        for(int index = 0; index < arrivals.size(); index++) {
            JsonObject jsonArrival = arrivals.getJsonObject(index);
            try {
                addArrivalToStn(stn, jsonArrival);
            } catch(TfLArrivalsDataMissingException | ArrivalException e) {
                countMissing++;
            }
        }

        if (countMissing == arrivals.size()) {
            throw new TfLArrivalsDataMissingException("All arrivals missing expected data component");
        }

    }

    /**
     * Add arrival to station
     *
     * @param stn              station to which arrival is to be added
     * @param jsonArrival      JSON object representing arrival
     * @throws JsonException   when JSON object does not have expected format
     * @throws TfLArrivalsDataMissingException  when expected data component is missing from JSON data
     * @throws ArrivalException  when arrival is on line that does not pass through station
     */
    private static void addArrivalToStn(Station stn, JsonObject jsonArrival)
            throws JsonException, TfLArrivalsDataMissingException, ArrivalException {
        String destinationName = null;
        String towards = null;
        String platform;
        String lineId;
        int timeToStation;

        try {
            timeToStation = jsonArrival.getInt("timeToStation");
            platform = jsonArrival.getString("platformName");
            lineId = jsonArrival.getString("lineId");
        } catch( NullPointerException e ) {
            throw new TfLArrivalsDataMissingException("Required data missing from JSON response");
        }

        try {
            destinationName = jsonArrival.getString("destinationName");
        } catch( NullPointerException e) {
            try {
                towards = jsonArrival.getString("towards");
            } catch( NullPointerException ex) {
                throw new TfLArrivalsDataMissingException("Required data missing from JSON response");
            }
        }

        String destination;
        if (destinationName != null) {
            destination = parseName(destinationName);
        }
        else {
            destination = towards;
        }

        Line line = getLineFromId(stn, lineId);

        stn.addArrival(line, new Arrival(timeToStation, destination, platform));
    }

    /**
     * Look up line operating through given station with given id; produce null if no such line is found.
     *
     * @param stn       the station
     * @param lineId    the line id
     * @return          line operating through station with given line id or null if no such line is found
     */
    private static Line getLineFromId(Station stn, String lineId) {
        Set<Line> lines = stn.getLines();

        for(Line next : lines) {
            if(next.getId().equals(lineId))
                return next;
        }

        return null;
    }
}
