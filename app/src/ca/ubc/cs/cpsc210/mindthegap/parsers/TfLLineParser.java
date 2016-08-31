package ca.ubc.cs.cpsc210.mindthegap.parsers;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

import ca.ubc.cs.cpsc210.mindthegap.model.*;
import ca.ubc.cs.cpsc210.mindthegap.parsers.exception.TfLLineDataMissingException;
import ca.ubc.cs.cpsc210.mindthegap.util.LatLon;

import javax.json.*;
import java.io.StringReader;

/**
 * A parser for the data returned by TFL line route query
 */
public class TfLLineParser extends TfLAbstractParser {

    /**
     * Parse line from JSON response produced by TfL.
     *
     * @param lmd              line meta-data
     * @return                 line parsed from TfL data
     * @throws JsonException   when JSON data does not have expected format
     * @throws TfLLineDataMissingException when
     * <ul>
     *  <li> JSON data is missing lineName, lineId or stopPointSequences elements </li>
     *  <li> or, for a given sequence: </li>
     *    <ul>
     *      <li> the stopPoint array is missing, or </li>
     *      <li> all station elements are missing one of name, lat, lon or stationId elements </li>
     *    </ul>
     * </ul>
     */
    public static Line parseLine(LineResourceData lmd, String jsonResponse)
            throws JsonException, TfLLineDataMissingException {
        JsonReader reader = Json.createReader(new StringReader(jsonResponse));

        JsonObject rootJSON = reader.readObject();
        String lineName;
        String lineId;

        try {
            lineName = rootJSON.getString("lineName");
            lineId = rootJSON.getString("lineId");
        } catch (NullPointerException e) {
            throw new TfLLineDataMissingException("JSON data missing required data elements");
        }

        Line tubeLine = new Line(lmd, lineId, lineName);

        addBranches(rootJSON, tubeLine);
        addStations(rootJSON, tubeLine);

        return tubeLine;
    }

    /**
     * Add stations parsed from JSON response to a given line
     *
     * @param rootJSON        the JSON response produced by TfL query
     * @param tubeLine        the line to which stations are to be added
     * @throws JsonException  when JSON data does not have expected format
     * @throws TfLLineDataMissingException  when JSON data is missing expected element (for stopPointSequences
     * data, exception thrown if ANY ONE of the sequences is completely missing data)
     */
    private static void addStations(JsonObject rootJSON, Line tubeLine)
            throws JsonException, TfLLineDataMissingException {
        JsonArray sequences = rootJSON.getJsonArray("stopPointSequences");

        if (sequences == null) {
            throw new TfLLineDataMissingException("stopPointSequences missing from JSON response");
        }

        for (int index = 0; index < sequences.size(); index++) {
            JsonObject sequence = sequences.getJsonObject(index);
            addSequenceToLine(sequence, tubeLine);
        }
    }

    /**
     * Add sequence of stop points to line
     *
     * @param sequence        JSON object containing sequence of stop points
     * @param tubeLine        line to which sequence of stop points is to be added
     * @throws JsonException  when JSON data does not have expected format
     * @throws TfLLineDataMissingException  when JSON data is missing expected element (for stopPoint
     * data, exception thrown only if ALL stopPoints have missing data)
     */
    private static void addSequenceToLine(JsonObject sequence, Line tubeLine)
            throws JsonException, TfLLineDataMissingException {
        int countMissing = 0;
        JsonArray stopPoints = sequence.getJsonArray("stopPoint");

        if (stopPoints == null) {
            throw new TfLLineDataMissingException("stopPoint array missing from stopPointSequences");
        }

        for(int index = 0; index < stopPoints.size(); index++) {
            JsonObject stopPoint = stopPoints.getJsonObject(index);
            try {
                addStationToLine(tubeLine, stopPoint);
            }
            catch(TfLLineDataMissingException e) {
                countMissing++;
            }
        }

        if (countMissing == stopPoints.size()) {
            throw new TfLLineDataMissingException("All stations missing required data");
        }
    }

    /**
     * Add stations to line
     *
     * @param tubeLine        line to which stations are to be added
     * @param jsonStation     JSOn object representing station (stop point)
     * @throws JsonException  when JSON data does not have expected format
     * @throws TfLLineDataMissingException  when JSON data is missing expected element
     */
    private static void addStationToLine(Line tubeLine, JsonObject jsonStation)
            throws JsonException, TfLLineDataMissingException {
        String fullName;
        String id;
        JsonNumber latNumber = jsonStation.getJsonNumber("lat");
        JsonNumber lonNumber = jsonStation.getJsonNumber("lon");

        try {
            fullName = jsonStation.getString("name");
            id = jsonStation.getString("stationId");
        } catch (NullPointerException e) {
            throw new TfLLineDataMissingException("Required data missing from stopPoint");
        }

        if (latNumber == null || lonNumber == null) {
            throw new TfLLineDataMissingException("Required data missing from stopPoint");
        }

        String shortName = parseName(fullName);
        double lat = latNumber.doubleValue();
        double lon = lonNumber.doubleValue();
        LatLon locn = new LatLon(lat, lon);

        Station lookup = StationManager.getInstance().getStationWithId(id);
        if(lookup != null) {
            tubeLine.addStation(lookup);
        }
        else {
            tubeLine.addStation(new Station(id, shortName, locn));
        }
    }

    /**
     * Add branches to tube line
     *
     * @param rootJSON        the JSON object that contains the branch data
     * @param tubeLine        the line to which branches are to be added
     * @throws JsonException  when JSON data does not have expected format
     * @throws TfLLineDataMissingException  when JSON data is missing expected element
     */
    private static void addBranches(JsonObject rootJSON, Line tubeLine)
            throws JsonException, TfLLineDataMissingException {
        JsonArray lineStrings;

        lineStrings = rootJSON.getJsonArray("lineStrings");
        if (lineStrings == null) {
            throw new TfLLineDataMissingException("Required data missing from JSON response");
        }

        for(int index = 0; index < lineStrings.size(); index++) {
            String branchString = lineStrings.getString(index);
            tubeLine.addBranch(new Branch(branchString));
        }
    }

}
