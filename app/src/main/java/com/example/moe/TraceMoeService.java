package com.example.moe;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TraceMoeService {
    @POST("search")
    Call<TraceMoeResponse> searchAnime(@Body RequestBody image);
}
