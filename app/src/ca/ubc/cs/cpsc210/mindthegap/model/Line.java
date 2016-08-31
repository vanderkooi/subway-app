package ca.ubc.cs.cpsc210.mindthegap.model;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

import java.util.*;

/**
 * Represents a line on the underground with a name, id, list of stations and list of branches.
 *
 * Invariants:
 * - no duplicates in list of stations
 * - stations must be maintained in the order in which they were added to the line
 */
public class Line implements Iterable<Station> {
    private LineResourceData lmd;
    private List<Station> stations;
    private Set<Branch> branches;
    private String name;
    private String id;

    /**
     * Constructs a line with given resource data, id and name.
     * List of stations and list of branches are empty.
     *
     * @param lmd     the line meta-data
     * @param id      the line id
     * @param name    the name of the line
     */
    public Line(LineResourceData lmd, String id, String name) {
        this.lmd = lmd;
        this.id = id;
        this.name = name;
        stations = new ArrayList<Station>();
        branches = new HashSet<Branch>();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    /**
     * Get colour specified by line resource data
     *
     * @return  colour in which to plot this line
     */
    public int getColour() {
        return lmd.getColour();
    }

    /**
     * Add station to line, if it's not already there
     *
     * @param stn  the station to add to this line
     */
    public void addStation(Station stn) {
        if(!stations.contains(stn)) {
            stations.add(stn);
            stn.addLine(this);
        }
    }

    /**
     * Remove station from line
     *
     * @param stn  the station to remove from this line
     */
    public void removeStation(Station stn) {
        if(stations.contains(stn)) {
            stations.remove(stn);
            stn.removeLine(this);
        }
    }

    /**
     * Remove all stations from this line
     */
    public void clearStations() {
        for(Station next : new ArrayList<Station>(stations)) {   // iterate over a copy to avoid concurrent mod
            removeStation(next);
        }
    }

    /**
     *
     * @return unmodifiable view of all stations on this line
     */
    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }

    /**
     * Determines if this line has a given station
     *
     * @param stn  the station
     * @return  true if line has the given station
     */
    public boolean hasStation(Station stn) {
        return stations.contains(stn);
    }

    /**
     * Add a branch to this line
     *
     * @param b  the branch to add to line
     */
    public void addBranch(Branch b) {
        branches.add(b);
    }

    /**
     *
     * @return unmodifiable view of all branches on this line
     */
    public Set<Branch> getBranches() {
        return Collections.unmodifiableSet(branches);
    }

    /**
     * Two lines are equal if their ids are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line otherLine = (Line) o;

        return !(id != null ? !id.equals(otherLine.id) : otherLine.id != null);

    }

    /**
     * Two lines are equal if their ids are equal
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public Iterator<Station> iterator() {
        return stations.iterator();
    }
}
