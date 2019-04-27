package com.gmail.randzjx.itunessearch.network;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ItunesAlbums {
    private static ItunesAlbums mInstance;
    private static final String BASE_URL = "https://itunes.apple.com";

    private Retrofit mRetrofit;

    public static ItunesAlbums getInstance() {
        if (mInstance == null) {
            mInstance = new ItunesAlbums();
        }
        return mInstance;
    }

    private ItunesAlbums() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public AlbumLoader getJSON() {
        return mRetrofit.create(AlbumLoader.class);
    }

    public interface AlbumLoader {
        @GET("search?entity=album&attribute=albumTerm&limit=200&explicit=yes")
        Call<Response> loadAlbums(@Query("term") String albumName);
    }
}
