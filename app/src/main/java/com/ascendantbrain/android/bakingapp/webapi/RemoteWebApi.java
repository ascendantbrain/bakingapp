package com.ascendantbrain.android.bakingapp.webapi;

import com.ascendantbrain.android.bakingapp.webapi.RemoteContract.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RemoteWebApi {
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> fetch();
}
