package com.img.mysure11.GetSet;

import java.util.ArrayList;

public class contestGetSet {

    String name,entryfee,contest_type,winning_percentage,matchkey,c_type,status,getjoinedpercentage,catid,catname;
    int id,challenge_id,multi_entry,confirmed_challenge,is_running,is_bonus,totalwinners,joinedusers,maximum_user,win_amount,winamount;
    String bonus_percentage,multientry_limit,pricecard_type,refercode,isselectedid;
    boolean isselected;
    ArrayList<priceCardGetSet> price_card = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntryfee() {
        return entryfee;
    }

    public void setEntryfee(String entryfee) {
        this.entryfee = entryfee;
    }

    public String getContest_type() {
        return contest_type;
    }

    public void setContest_type(String contest_type) {
        this.contest_type = contest_type;
    }

    public String getWinning_percentage() {
        return winning_percentage;
    }

    public void setWinning_percentage(String winning_percentage) {
        this.winning_percentage = winning_percentage;
    }

    public String getMatchkey() {
        return matchkey;
    }

    public void setMatchkey(String matchkey) {
        this.matchkey = matchkey;
    }

    public String getC_type() {
        return c_type;
    }

    public void setC_type(String c_type) {
        this.c_type = c_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGetjoinedpercentage() {
        return getjoinedpercentage;
    }

    public void setGetjoinedpercentage(String getjoinedpercentage) {
        this.getjoinedpercentage = getjoinedpercentage;
    }

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChallenge_id() {
        return challenge_id;
    }

    public void setChallenge_id(int challenge_id) {
        this.challenge_id = challenge_id;
    }

    public int getMulti_entry() {
        return multi_entry;
    }

    public void setMulti_entry(int multi_entry) {
        this.multi_entry = multi_entry;
    }

    public int getConfirmed_challenge() {
        return confirmed_challenge;
    }

    public void setConfirmed_challenge(int confirmed_challenge) {
        this.confirmed_challenge = confirmed_challenge;
    }

    public int getIs_running() {
        return is_running;
    }

    public void setIs_running(int is_running) {
        this.is_running = is_running;
    }

    public int getIs_bonus() {
        return is_bonus;
    }

    public void setIs_bonus(int is_bonus) {
        this.is_bonus = is_bonus;
    }

    public int getTotalwinners() {
        return totalwinners;
    }

    public void setTotalwinners(int totalwinners) {
        this.totalwinners = totalwinners;
    }

    public int getJoinedusers() {
        return joinedusers;
    }

    public void setJoinedusers(int joinedusers) {
        this.joinedusers = joinedusers;
    }

    public int getMaximum_user() {
        return maximum_user;
    }

    public void setMaximum_user(int maximum_user) {
        this.maximum_user = maximum_user;
    }

    public int getWin_amount() {
        return win_amount;
    }

    public void setWin_amount(int win_amount) {
        this.win_amount = win_amount;
    }

    public String getBonus_percentage() {
        return bonus_percentage;
    }

    public void setBonus_percentage(String bonus_percentage) {
        this.bonus_percentage = bonus_percentage;
    }

    public int getWinamount() {
        return winamount;
    }

    public void setWinamount(int winamount) {
        this.winamount = winamount;
    }

    public String getMultientry_limit() {
        return multientry_limit;
    }

    public void setMultientry_limit(String multientry_limit) {
        this.multientry_limit = multientry_limit;
    }

    public String getPricecard_type() {
        return pricecard_type;
    }

    public void setPricecard_type(String pricecard_type) {
        this.pricecard_type = pricecard_type;
    }

    public String getRefercode() {
        return refercode;
    }

    public void setRefercode(String refercode) {
        this.refercode = refercode;
    }

    public String getIsselectedid() {
        return isselectedid;
    }

    public void setIsselectedid(String isselectedid) {
        this.isselectedid = isselectedid;
    }

    public boolean isIsselected() {
        return isselected;
    }

    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }

    public ArrayList<priceCardGetSet> getPrice_card() {
        return price_card;
    }

    public void setPrice_card(ArrayList<priceCardGetSet> price_card) {
        this.price_card = price_card;
    }
}
