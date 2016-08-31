package ca.ubc.cs.cpsc210.mindthegap.model;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

import ca.ubc.cs.cpsc210.mindthegap.parsers.BranchStringParser;
import ca.ubc.cs.cpsc210.mindthegap.util.LatLon;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A branch of a line consisting of a path of lat/lon points.
 * These points are used to draw the branch on a map.  Note that the points used to
 * represent the branch are not necessarily co-located with stations.
 */
public class Branch implements Iterable<LatLon> {
    private List<LatLon> points;

    /**
     * Constructs a Branch by parsing (using BranchStringParser) the points
     * that define the branch from the given string.
     *
     * @param lineString  string of coordinates representing points on branch
     */
    public Branch(String lineString) {
        points = BranchStringParser.parseBranch(lineString);
    }

    /**
     * Get unmodifiable view of all points on this branch
     *
     * @return  all points on branch
     */
    public List<LatLon> getPoints() {
        return Collections.unmodifiableList(points);
    }

    @Override
    public Iterator<LatLon> iterator() {
        return points.iterator();
    }

    /**
     * Two branches are equal if their lists of points are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Branch geoPoints = (Branch) o;

        return points.equals(geoPoints.points);

    }

    /**
     * Two branches are equal if their lists of points are equal
     */
    @Override
    public int hashCode() {
        return points.hashCode();
    }
}
