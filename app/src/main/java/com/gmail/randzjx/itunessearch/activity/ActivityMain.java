package com.gmail.randzjx.itunessearch.activity;

import android.os.Bundle;

import com.gmail.randzjx.itunessearch.R;
import com.gmail.randzjx.itunessearch.fragments.FragmentList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ActivityMain extends AppCompatActivity {

    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container_list);
        if (fragment == null) {                                         //check and start list fragment
            fragment = new FragmentList();
            fm.beginTransaction().add(R.id.container_list, fragment).commit();
        }
    }
}
