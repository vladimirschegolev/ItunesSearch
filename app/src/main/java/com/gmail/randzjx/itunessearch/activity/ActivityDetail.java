package com.gmail.randzjx.itunessearch.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.gmail.randzjx.itunessearch.R;
import com.gmail.randzjx.itunessearch.fragments.FragmentDetail;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class ActivityDetail extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle args = getIntent().getExtras();

        if (args != null && args.containsKey(FragmentDetail.ID_ALBUM)) {  //check arguments
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.container_sep_datail);
            if (fragment == null) {
                fragment = FragmentDetail.getInstance(args);
                fm.beginTransaction().add(R.id.container_sep_datail, fragment).commit();
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
