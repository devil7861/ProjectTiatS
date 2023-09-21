package com.jica.newpts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jica.newpts.MainFragment.CommunityFragment;
import com.jica.newpts.MainFragment.MainFragment;
import com.jica.newpts.MainFragment.PlantManagementFragment;
import com.jica.newpts.MainFragment.ProfileFragment;
import com.jica.newpts.MainFragment.SearchFragment;

public class TabLayoutActivity extends AppCompatActivity {

    // 퀵메뉴 자연스렙게 하기 위한 부분
    boolean isPageOpen = false;
    MainFragment mainFragment;
    PlantManagementFragment plantManagementFragment;
    SearchFragment searchFragment;
    CommunityFragment communityFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);

        mainFragment = new MainFragment();
        plantManagementFragment = new PlantManagementFragment();
        searchFragment = new SearchFragment();
        communityFragment = new CommunityFragment();
        profileFragment = new ProfileFragment();

        Intent intent = getIntent();
        String receivedValue = intent.getStringExtra("sendData");

        if (receivedValue != null) {
            if (receivedValue.equals("CommunityBoardModifyFragment") || receivedValue.equals("CommunityBoardReadFragement") || receivedValue.equals("CommunitySearchActivity")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, communityFragment).commit();
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.tab4);
            } else if (receivedValue.equals("ChattingActivity")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.tab5);
            }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();
        }


        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int curId = item.getItemId();
                if (curId == R.id.tab1) {

                    isPageOpen = false;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, mainFragment).commit();
                    return true;

                } else if (curId == R.id.tab2) {

                    isPageOpen = false;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, plantManagementFragment).commit();

                    return true;
                } else if (curId == R.id.tab3) {

                    isPageOpen = false;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, searchFragment).commit();
                    return true;
                } else if (curId == R.id.tab4) {

                    isPageOpen = false;

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, communityFragment).commit();
                    return true;
                } else if (curId == R.id.tab5) {

                    isPageOpen = false;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, profileFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
