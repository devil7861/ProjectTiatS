package com.jica.newpts.PlantManagementFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jica.newpts.R;

public class FragmentPlantManagement extends Fragment implements View.OnClickListener {

    ImageView ivCFPMWrite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.child_fragment_plant_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity ownerActivity = requireActivity();

        ivCFPMWrite = view.findViewById(R.id.ivCFPMWrite);

        ivCFPMWrite.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        int clickedViewId = view.getId();
        if (clickedViewId == R.id.ivCFPMWrite) {
            navigateToFragment(new FragmentPlantManagementWrite());
        }
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.child_container, fragment);

        Toast.makeText(getActivity(), "선택됨", Toast.LENGTH_SHORT).show();
        transaction.commit();
    }
}