package com.img.mysure11.GetSet;

import java.util.ArrayList;

public class liveLeaderboardGetSet {

    ArrayList<liveTeamsGetSet> jointeams;
    String team_number_get,userrank,pdfname,status;
    boolean success;

    public ArrayList<liveTeamsGetSet> getJointeams() {
        return jointeams;
    }

    public void setJointeams(ArrayList<liveTeamsGetSet> jointeams) {
        this.jointeams = jointeams;
    }

    public String getTeam_number_get() {
        return team_number_get;
    }

    public void setTeam_number_get(String team_number_get) {
        this.team_number_get = team_number_get;
    }

    public String getUserrank() {
        return userrank;
    }

    public void setUserrank(String userrank) {
        this.userrank = userrank;
    }

    public String getPdfname() {
        return pdfname;
    }

    public void setPdfname(String pdfname) {
        this.pdfname = pdfname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
