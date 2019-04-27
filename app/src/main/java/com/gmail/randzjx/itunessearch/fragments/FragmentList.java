package com.gmail.randzjx.itunessearch.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.randzjx.itunessearch.R;
import com.gmail.randzjx.itunessearch.activity.ActivityDetail;
import com.gmail.randzjx.itunessearch.network.ItunesAlbums;
import com.gmail.randzjx.itunessearch.network.Response;
import com.squareup.picasso.Picasso;

import java.net.HttpURLConnection;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;

public class FragmentList extends Fragment {

    private static final String POSITION = "position";
    private static final String SEARCH = "search";

    private boolean mTwoPane = false;

    private ListAdapter mAdapter;
    private EditText albumTitle;
    private ImageButton bSearch;
    private LinearLayoutManager layout;
    private int lastPosition = -1;    //scrolling position

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        mTwoPane = getActivity() != null && getActivity().findViewById(R.id.container_detail) != null;

        albumTitle = v.findViewById(R.id.et_album_title);
        bSearch = v.findViewById(R.id.btn_search);

        RecyclerView recyclerView = v.findViewById(R.id.list);

        layout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layout);
        mAdapter = new ListAdapter();
        recyclerView.setAdapter(mAdapter);

        initLis();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        lastPosition = layout.findFirstVisibleItemPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAlbums();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, lastPosition);
        outState.putString(SEARCH, albumTitle.getText().toString());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            lastPosition = savedInstanceState.getInt(POSITION, -1);
            albumTitle.setText(savedInstanceState.getString(SEARCH, ""));

        }
        super.onViewStateRestored(savedInstanceState);
    }

    private void initLis() {
        albumTitle.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                loadAlbums();
                return true;
            }
            return false;
        });

        bSearch.setOnClickListener(v -> loadAlbums());
    }

    private void loadAlbums() {
        lastPosition = -1;                    //reset position on load
        String name = albumTitle.getText().toString();
        name = name.trim().replaceAll("\\s+", "+");
        ItunesAlbums.getInstance()
                .getJSON()
                .loadAlbums(name)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        if (response.code() == HttpURLConnection.HTTP_OK && response.body().getResultCount() > 0) {
                            mAdapter.setResponse(response.body());
                            mAdapter.notifyDataSetChanged();
                            if (lastPosition > 0) layout.scrollToPosition(lastPosition);
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        Toast.makeText(getContext(), R.string.error_no_connection, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showDetail(int albumID) {
        if (getFragmentManager() != null) {
            if (mTwoPane) {                                     //widescreen check
                Fragment fragment = FragmentDetail.getInstance(albumID);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_detail, fragment)
                            .commit();
            } else {
                Intent intent = new Intent(getActivity(), ActivityDetail.class);
                intent.putExtras(FragmentDetail.getBundle(albumID));
                startActivity(intent);
            }
        }

    }

    private class ViewHolderAlbum extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView image;

        public ViewHolderAlbum(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater.inflate(R.layout.item_list, viewGroup, false));
            title = itemView.findViewById(R.id.tv_album_title_list);
            image = itemView.findViewById(R.id.album_artwork60);
        }

        public void setTitle(String collectionName) {
            title.setText(collectionName);
        }

        public void loadImage(String url) {
            Picasso.get().load(url).into(image);
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ViewHolderAlbum> {

        Response mResponse;

        void setResponse(Response response) {
            mResponse = response;
            if (mResponse != null && mResponse.getResultCount() > 0) {
                Collections.sort(mResponse.getResults(),                // Sorting response alphabetically
                        (o1, o2) -> o1.getCollectionName().compareTo(o2.getCollectionName()));
            }
        }

        @NonNull
        @Override
        public ViewHolderAlbum onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final ViewHolderAlbum holder = new ViewHolderAlbum(inflater, parent);
            holder.itemView.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    showDetail(mResponse.getResults().get(position).getCollectionId());
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderAlbum holder, int position) {
            holder.setTitle(mResponse.getResults().get(position).getCollectionName());
            holder.loadImage(mResponse.getResults().get(position).getArtworkUrl60());
        }

        @Override
        public int getItemCount() {
            if (mResponse != null) return mResponse.getResultCount();
            return 0;
        }
    }
}
