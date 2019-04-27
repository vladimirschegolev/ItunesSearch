package com.gmail.randzjx.itunessearch.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.randzjx.itunessearch.R;
import com.gmail.randzjx.itunessearch.network.ItunesAlbumTracks;
import com.gmail.randzjx.itunessearch.network.Response;
import com.gmail.randzjx.itunessearch.network.Result;
import com.squareup.picasso.Picasso;

import java.net.HttpURLConnection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;

public class FragmentDetail extends Fragment {
    public static final String ID_ALBUM = "id_album";

    private int mAlbumID = 0;

    public static FragmentDetail getInstance(int albumID) {    //get instance based on ID collection
        FragmentDetail fragment = new FragmentDetail();
        fragment.setArguments(getBundle(albumID));
        return fragment;
    }

    public static FragmentDetail getInstance(Bundle args) {
        FragmentDetail fragment = new FragmentDetail();
        fragment.setArguments(args);
        return fragment;
    }

    public static Bundle getBundle(int albumID) {       //get bundle for intent extras
        Bundle args = new Bundle();
        args.putInt(ID_ALBUM, albumID);
        return args;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getArguments() != null) mAlbumID = getArguments().getInt(ID_ALBUM);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);

        if (mAlbumID > 0) {               //check for collection ID was initialized
            ImageView image = v.findViewById(R.id.album_artwork);
            TextView title = v.findViewById(R.id.tv_album_title_detail);
            TextView artist = v.findViewById(R.id.tv_artist_name);
            TextView genre = v.findViewById(R.id.tv_genre);
            TextView release = v.findViewById(R.id.tv_release);
            TextView copyright = v.findViewById(R.id.tv_copyright);
            LinearLayout layout = v.findViewById(R.id.layout_tracks);

            ItunesAlbumTracks.getInstance().getJSON().loadTracks(mAlbumID).enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    if (response.code() == HttpURLConnection.HTTP_OK &&
                            response.body().getResultCount() > 0 &&                             //check that response not empty
                            getContext() != null) {                                             //check that fragment still attached
                            List<Result> album = response.body().getResults();                  //first in response collection ob
//                        Picasso.get().load(album.get(0).getArtWorkBig(800)).into(image);      //We can download big artwork, not documented in API
                        Picasso.get().load(album.get(0).getArtworkUrl100()).into(image);
                        title.setText(album.get(0).getCollectionName());
                        artist.setText(album.get(0).getArtistName());
                        genre.setText(String.format(getString(R.string.text_genre), album.get(0).getPrimaryGenreName(), album.get(0).getCollectionPrice(), album.get(0).getCurrency()));
                        release.setText(String.format(getString(R.string.text_release),
                                album.get(0).getReleaseDate()).replaceAll("[TZ]", " "));
                        copyright.setText(album.get(0).getCopyright());
                        loadTracks(layout, album);
                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    title.setText(getString(R.string.error_no_connection));
                }
            });
        }
        return v;
    }

    @SuppressLint("DefaultLocale")
    private void loadTracks(LinearLayout layout, List<Result> album) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for (int i = 1; i < album.size(); i++) {
            Result track = album.get(i);
            View v = inflater.inflate(R.layout.item_track, layout, false);      //creating view for track
            TextView trackNum = v.findViewById(R.id.tv_track_num);
            TextView trackName = v.findViewById(R.id.tv_track_name);
            TextView duration = v.findViewById(R.id.tv_time);
            trackNum.setText(String.valueOf(track.getTrackNumber()));
            if (track.getTrackPrice() < 0) {                                               //check for album only tracks
                trackName.setText(String.format(getString(R.string.text_track_album_only), track.getTrackName()));
            } else {
                trackName.setText(String.format(getString(R.string.text_track), track.getTrackName(), track.getTrackPrice(), track.getCurrency()));
            }
            int seconds = track.getTrackTimeMillis() / 1000;                               //calculating track duration
            duration.setText(String.format("%d:%02d", seconds / 60, seconds % 60));
            layout.addView(v);
        }
    }
}
