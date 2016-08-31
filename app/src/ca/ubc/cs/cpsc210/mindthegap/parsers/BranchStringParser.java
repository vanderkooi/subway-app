package ca.ubc.cs.cpsc210.mindthegap.parsers;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */


import ca.ubc.cs.cpsc210.mindthegap.util.LatLon;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parser for branch strings in TfL line data
 */
public class BranchStringParser {

    /**
     * Parse a branch string obtained from TFL
     *
     * @param branch  branch string
     * @return       list of lat/lon points parsed from branch string
     */
    public static List<LatLon> parseBranch(String branch) {
        List<LatLon> points = new ArrayList<LatLon>();

        Pattern p = Pattern.compile("\\[{3}|\\],\\[|,|\\]{3}");
        String[] coords = p.split(branch, 0);

        for (int i = 1; i < coords.length; i+=2) {
            points.add(new LatLon(Double.parseDouble(coords[i+1]), Double.parseDouble(coords[i])));
        }

        return points;
    }
}
