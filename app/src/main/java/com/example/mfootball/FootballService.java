package com.example.mfootball;

import com.example.mfootball.data.MatchesResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FootballService {
    @GET("/video-api/v{version}/feed/")
    Call<MatchesResult> getVideos(
            @Path("version") int version,
            @Query("token") String token
    );
}