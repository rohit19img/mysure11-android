package com.img.mysure11.GetSet;


public class MatchStatsGetSet {

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getMatchname() {
        return matchname;
    }

    public void setMatchname(String matchname) {
        this.matchname = matchname;
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }

    public String getSelectper() {
        return selectper;
    }

    public void setSelectper(String selectper) {
        this.selectper = selectper;
    }

    public String getMatchdate() {
        return matchdate;
    }

    public void setMatchdate(String matchdate) {
        this.matchdate = matchdate;
    }

    public double getStartingpoints() {
        return startingpoints;
    }

    public void setStartingpoints(double startingpoints) {
        this.startingpoints = startingpoints;
    }

    public double getBatting_points() {
        return batting_points;
    }

    public void setBatting_points(double batting_points) {
        this.batting_points = batting_points;
    }

    public double getBowling_points() {
        return bowling_points;
    }

    public void setBowling_points(double bowling_points) {
        this.bowling_points = bowling_points;
    }

    public double getFielding_points() {
        return fielding_points;
    }

    public void setFielding_points(double fielding_points) {
        this.fielding_points = fielding_points;
    }

    public double getExtra_points() {
        return extra_points;
    }

    public void setExtra_points(double extra_points) {
        this.extra_points = extra_points;
    }

    public double getNegative_points() {
        return negative_points;
    }

    public void setNegative_points(double negative_points) {
        this.negative_points = negative_points;
    }

    String playername,selectper,matchdate,matchname,shortname;
    double startingpoints,batting_points,bowling_points,fielding_points,extra_points,negative_points,total_points;

    public double getTotal_points() {
        return total_points;
    }

    public void setTotal_points(double total_points) {
        this.total_points = total_points;
    }
}
