package com.img.mysure11.GetSet;

import java.util.ArrayList;

public class MyTeamsGetSet {
    int teamnumber,teamid,status,batsmancount,bowlercount,allroundercount,team1count,team2count;
    String captain,vicecaptain,player_type,captainimage,vicecaptainimage;
    boolean isSelected,picked = false;
    ArrayList<SelectedPlayersGetSet>player;

    public int getTeamnumber() {
        return teamnumber;
    }

    public void setTeamnumber(int teamnumber) {
        this.teamnumber = teamnumber;
    }

    public int getTeamid() {
        return teamid;
    }

    public void setTeamid(int teamid) {
        this.teamid = teamid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBatsmancount() {
        return batsmancount;
    }

    public void setBatsmancount(int batsmancount) {
        this.batsmancount = batsmancount;
    }

    public int getBowlercount() {
        return bowlercount;
    }

    public void setBowlercount(int bowlercount) {
        this.bowlercount = bowlercount;
    }

    public int getAllroundercount() {
        return allroundercount;
    }

    public void setAllroundercount(int allroundercount) {
        this.allroundercount = allroundercount;
    }

    public int getTeam1count() {
        return team1count;
    }

    public void setTeam1count(int team1count) {
        this.team1count = team1count;
    }

    public int getTeam2count() {
        return team2count;
    }

    public void setTeam2count(int team2count) {
        this.team2count = team2count;
    }

    public String getCaptain() {
        return captain;
    }

    public void setCaptain(String captain) {
        this.captain = captain;
    }

    public String getVicecaptain() {
        return vicecaptain;
    }

    public void setVicecaptain(String vicecaptain) {
        this.vicecaptain = vicecaptain;
    }

    public String getPlayer_type() {
        return player_type;
    }

    public void setPlayer_type(String player_type) {
        this.player_type = player_type;
    }

    public String getCaptainimage() {
        return captainimage;
    }

    public void setCaptainimage(String captainimage) {
        this.captainimage = captainimage;
    }

    public String getVicecaptainimage() {
        return vicecaptainimage;
    }

    public void setVicecaptainimage(String vicecaptainimage) {
        this.vicecaptainimage = vicecaptainimage;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public ArrayList<SelectedPlayersGetSet> getPlayer() {
        return player;
    }

    public void setPlayer(ArrayList<SelectedPlayersGetSet> player) {
        this.player = player;
    }
}
