package com.img.mysure11.GetSet;

import java.util.ArrayList;

public class contestCategoriesGetSet {

    String catname,catid,sub_title,image;
    ArrayList<contestGetSet> contest;

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<contestGetSet> getContest() {
        return contest;
    }

    public void setContest(ArrayList<contestGetSet> contest) {
        this.contest = contest;
    }
}
