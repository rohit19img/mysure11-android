package com.img.mysure11.GetSet;


public class PlayerListGetSet {

    boolean isSelected,enabled = true;
    String id,name,role,credit,playingstatus,playerkey,image,totalpoints,team,teamname,player_selection_percentage,captain_selection_percentage,vice_captain_selection_percentage;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getPlayingstatus() {
        return playingstatus;
    }

    public void setPlayingstatus(String playingstatus) {
        this.playingstatus = playingstatus;
    }

    public String getPlayerkey() {
        return playerkey;
    }

    public void setPlayerkey(String playerkey) {
        this.playerkey = playerkey;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTotalpoints() {
        return totalpoints;
    }

    public void setTotalpoints(String totalpoints) {
        this.totalpoints = totalpoints;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeamname() {
        return teamname;
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }

    public String getPlayer_selection_percentage() {
        return player_selection_percentage;
    }

    public void setPlayer_selection_percentage(String player_selection_percentage) {
        this.player_selection_percentage = player_selection_percentage;
    }

    public String getCaptain_selection_percentage() {
        return captain_selection_percentage;
    }

    public void setCaptain_selection_percentage(String captain_selection_percentage) {
        this.captain_selection_percentage = captain_selection_percentage;
    }

    public String getVice_captain_selection_percentage() {
        return vice_captain_selection_percentage;
    }

    public void setVice_captain_selection_percentage(String vice_captain_selection_percentage) {
        this.vice_captain_selection_percentage = vice_captain_selection_percentage;
    }
}
