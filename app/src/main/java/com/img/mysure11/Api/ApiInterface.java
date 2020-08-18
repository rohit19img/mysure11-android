package com.img.mysure11.Api;

import com.img.mysure11.GetSet.LeaderboardUserGetSet;
import com.img.mysure11.GetSet.MyTeamsGetSet;
import com.img.mysure11.GetSet.PlayerListGetSet;
import com.img.mysure11.GetSet.PlayerStatsGetSet;
import com.img.mysure11.GetSet.SelectedPlayersGetSet;
import com.img.mysure11.GetSet.TransactionGetSet;
import com.img.mysure11.GetSet.bannersGetSet;
import com.img.mysure11.GetSet.contestCategoriesGetSet;
import com.img.mysure11.GetSet.contestGetSet;
import com.img.mysure11.GetSet.fantasyScorecardGetSet;
import com.img.mysure11.GetSet.joinedMatchesGetSet;
import com.img.mysure11.GetSet.liveLeaderboardGetSet;
import com.img.mysure11.GetSet.live_contestGetSet;
import com.img.mysure11.GetSet.mainLeaderboardGetSet;
import com.img.mysure11.GetSet.offersGetSet;
import com.img.mysure11.GetSet.seriesGetSet;
import com.img.mysure11.GetSet.upcomingMatchesGetSet;
import com.img.mysure11.GetSet.withdrwalsGetSet;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("getallseries")
    Call<ArrayList<seriesGetSet>> getallseries(@Header("Authorization") String Auth);

    @GET("getleaderboard")
    Call<ArrayList<mainLeaderboardGetSet>> getleaderboard(@Header("Authorization") String Auth, @Query("series_id") String series_id);

    @GET("getleaderboardbyuser")
    Call<ArrayList<LeaderboardUserGetSet>> getleaderboardbyuser(@Header("Authorization") String Auth, @Query("series_id") String series_id, @Query("userid") String userid);

    @GET("getmainbanner")
    Call<ArrayList<bannersGetSet>> getmainbanner(@Header("Authorization") String Auth);

    @GET("offerdepositsnew")
    Call<ArrayList<offersGetSet>> offers(@Header("Authorization") String Auth);

    @GET("getmatchlist")
    Call<ArrayList<upcomingMatchesGetSet>> getmatchlist(@Header("Authorization") String Auth);

    @GET("joinedmatches")
    Call<ArrayList<joinedMatchesGetSet>> joinedmatches(@Header("Authorization") String Auth);

    @GET("mytransactions")
    Call<ArrayList<TransactionGetSet>> mytransactions(@Header("Authorization") String Auth);

    @GET("mywithdrawlist")
    Call<ArrayList<withdrwalsGetSet>> mywithdrawlist(@Header("Authorization") String Auth);

    @GET("getContests")
    Call<ArrayList<contestCategoriesGetSet>> getContests(@Header("Authorization") String Auth, @Query("matchkey") String matchkey);

    @GET("getAllContests")
    Call<ArrayList<contestGetSet>> getAllContests(@Header("Authorization") String Auth, @Query("matchkey") String matchkey);

    @GET("myjoinedleauges")
    Call<ArrayList<contestGetSet>> myjoinedleauges(@Header("Authorization") String Auth, @Query("matchkey") String matchkey);

    @GET("myjoinedleauges")
    Call<ArrayList<live_contestGetSet>> myjoinedleauges_live(@Header("Authorization") String Auth, @Query("matchkey") String matchkey);

    @GET("getallplayers")
    Call<ArrayList<PlayerListGetSet>> PlayersList(@Header("Authorization") String Auth, @Query("matchkey") String matchkey);

    @GET("getMyTeams")
    Call<ArrayList<MyTeamsGetSet>> MyTeams(@Header("Authorization") String Auth, @Query("matchkey") String matchkey);

    @GET("getMyTeams")
    Call<ArrayList<MyTeamsGetSet>> MyTeams(@Header("Authorization") String Auth, @Query("matchkey") String matchkey,
                                           @Query("challengeid") String challengeid);

    @GET("viewteam")
    Call<ArrayList<SelectedPlayersGetSet>> viewteam(@Header("Authorization") String Auth, @Query("matchkey") String matchkey,
                                                    @Query("teamid") String teamid, @Query("teamnumber") String teamnumber);

    @GET("getPlayerInfo")
    Call<ArrayList<PlayerStatsGetSet>> PlayerStats(@Header("Authorization") String Auth, @Query("playerid") String playerid, @Query("matchkey") String matchkey);

    @GET("fantasyscorecards")
    Call<ArrayList<fantasyScorecardGetSet>> fantasyscorecards(@Header("Authorization") String Auth, @Query("matchkey") String matchkey);

    @GET("livescores")
    Call<ArrayList<liveLeaderboardGetSet>> getleaderboard_challenge(@Header("Authorization") String Auth, @Query("challengeid") int challenge_id
            , @Query("matchkey") String matchkey);


    // Guest  //without Auth
    @GET("getmainbanner")
    Call<ArrayList<bannersGetSet>> getmainbanner();

    @GET("getmatchlist")
    Call<ArrayList<upcomingMatchesGetSet>> getmatchlist();

}
