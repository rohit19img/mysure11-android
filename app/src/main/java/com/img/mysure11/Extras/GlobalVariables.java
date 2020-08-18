package com.img.mysure11.Extras;

import android.app.Application;

import com.img.mysure11.GetSet.MyTeamsGetSet;
import com.img.mysure11.GetSet.captainListGetSet;
import com.img.mysure11.GetSet.priceCardGetSet;

import java.util.ArrayList;

public class GlobalVariables extends Application{

    public String  matchKey = new String(), paytype="",multi_entry= new String(), Series= new String(),matchTime= new String(),promoCode= new String(),team1= new String(),team2= new String(),team1Image= new String(),team2image= new String(),status= new String();
    String sportType = "",seriesName = "",playing11_status;
    int challengeId=0;
    public static int teamCount;
    int max_teams = 11;
    boolean isprivate = false;

    ArrayList<captainListGetSet> captainList;
    ArrayList<MyTeamsGetSet> selectedteamList;
    public ArrayList<priceCardGetSet> priceCard = new ArrayList<>();

    public String getMatchKey() {
        return matchKey;
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public String getMulti_entry() {
        return multi_entry;
    }

    public void setMulti_entry(String multi_entry) {
        this.multi_entry = multi_entry;
    }

    public String getSeries() {
        return Series;
    }

    public void setSeries(String series) {
        Series = series;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getTeam1Image() {
        return team1Image;
    }

    public void setTeam1Image(String team1Image) {
        this.team1Image = team1Image;
    }

    public String getTeam2image() {
        return team2image;
    }

    public void setTeam2image(String team2image) {
        this.team2image = team2image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    public static int getTeamCount() {
        return teamCount;
    }

    public static void setTeamCount(int teamCount) {
        GlobalVariables.teamCount = teamCount;
    }

    public int getMax_teams() {
        return max_teams;
    }

    public void setMax_teams(int max_teams) {
        this.max_teams = max_teams;
    }

    public boolean isIsprivate() {
        return isprivate;
    }

    public void setIsprivate(boolean isprivate) {
        this.isprivate = isprivate;
    }

    public ArrayList<captainListGetSet> getCaptainList() {
        return captainList;
    }

    public void setCaptainList(ArrayList<captainListGetSet> captainList) {
        this.captainList = captainList;
    }

    public ArrayList<MyTeamsGetSet> getSelectedteamList() {
        return selectedteamList;
    }

    public void setSelectedteamList(ArrayList<MyTeamsGetSet> selectedteamList) {
        this.selectedteamList = selectedteamList;
    }

    public ArrayList<priceCardGetSet> getPriceCard() {
        return priceCard;
    }

    public void setPriceCard(ArrayList<priceCardGetSet> priceCard) {
        this.priceCard = priceCard;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getPlaying11_status() {
        return playing11_status;
    }

    public void setPlaying11_status(String playing11_status) {
        this.playing11_status = playing11_status;
    }
}
