package com.gmail.randzjx.itunessearch.network;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ItunesAlbumTracks {
    private static ItunesAlbumTracks mInstance;
    private static final String BASE_URL = "https://itunes.apple.com";

    private Retrofit mRetrofit;

    public static ItunesAlbumTracks getInstance() {
        if (mInstance == null) {
            mInstance = new ItunesAlbumTracks();
        }
        return mInstance;
    }

    private ItunesAlbumTracks() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public TrackLoader getJSON() {
        return mRetrofit.create(TrackLoader.class);
    }

    public interface TrackLoader {
        @GET("lookup?entity=song&limit=200")
        Call<Response> loadTracks(@Query("id") int id);
    }
}
