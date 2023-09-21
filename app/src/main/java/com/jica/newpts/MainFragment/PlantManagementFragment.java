package com.jica.newpts.MainFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.jica.newpts.PlantManagementFragment.FragmentPlantManagement;
import com.jica.newpts.PlantManagementFragment.FragmentPlantGrowthWrite;
import com.jica.newpts.R;
import com.jica.newpts.TabLayoutActivity;

public class PlantManagementFragment extends Fragment {
    FragmentPlantManagement fragmentPlantManagement;
    FragmentPlantGrowthWrite fragmentPlantGrowthWrite;
    Toolbar toolbar; // Toolbar 선언

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plant_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        fragmentPlantManagement = new FragmentPlantManagement();
        fragmentPlantGrowthWrite = new FragmentPlantGrowthWrite();

        getChildFragmentManager().beginTransaction().replace(R.id.child_container, fragmentPlantManagement).commit();

        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("식물관리"));
        tabs.addTab(tabs.newTab().setText("성장기록"));
        setDialog();
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selectedFragment = null;
                if (position == 0) {
                    selectedFragment = fragmentPlantManagement;
                } else if (position == 1) {
                    /*  selectedFragment = fragmentPlantGrowthWrite;*/
                    selectedFragment = fragmentPlantManagement;
                }
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.child_container, selectedFragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("주의사항")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage("해당기능은 차후에 지원할 예정입니다.")
                .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     /*   Intent intent = new Intent(requireActivity(), TabLayoutActivity.class);
                        startActivity(intent);*/
                    }
                })
                .setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
