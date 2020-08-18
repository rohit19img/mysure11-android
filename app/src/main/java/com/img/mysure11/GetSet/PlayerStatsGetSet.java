package com.img.mysure11.GetSet;

import java.util.ArrayList;


public class PlayerStatsGetSet {

    ArrayList<MatchStatsGetSet> matches;
    String playerpoints,playername,playerkey,playercountry,playerdob,playercredit,team,playerimage,playerrole,battingstat,bowlerstat;

    public String getBowlerstat() {
        return bowlerstat;
    }

    public void setBowlerstat(String bowlerstat) {
        this.bowlerstat = bowlerstat;
    }

    public String getBattingstat() {
        return battingstat;
    }

    public void setBattingstat(String battingstat) {
        this.battingstat = battingstat;
    }

    public ArrayList<MatchStatsGetSet> getMatches() {
        return matches;
    }

    public void setMatches(ArrayList<MatchStatsGetSet> matches) {
        this.matches = matches;
    }

    public String getPlayerpoints() {
        return playerpoints;
    }

    public void setPlayerpoints(String playerpoints) {
        this.playerpoints = playerpoints;
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }

    public String getPlayerkey() {
        return playerkey;
    }

    public void setPlayerkey(String playerkey) {
        this.playerkey = playerkey;
    }

    public String getPlayercountry() {
        return playercountry;
    }

    public void setPlayercountry(String playercountry) {
        this.playercountry = playercountry;
    }

    public String getPlayerdob() {
        return playerdob;
    }

    public void setPlayerdob(String playerdob) {
        this.playerdob = playerdob;
    }

    public String getPlayercredit() {
        return playercredit;
    }

    public void setPlayercredit(String playercredit) {
        this.playercredit = playercredit;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getPlayerimage() {
        return playerimage;
    }

    public void setPlayerimage(String playerimage) {
        this.playerimage = playerimage;
    }

    public String getPlayerrole() {
        return playerrole;
    }

    public void setPlayerrole(String playerrole) {
        this.playerrole = playerrole;
    }
}
