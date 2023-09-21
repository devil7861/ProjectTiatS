package com.jica.newpts.PlantManagementFragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jica.newpts.R;

public class FragmentPlantManagementWrite extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setDialog();
        return inflater.inflate(R.layout.fragment_plant_management_write, container, false);
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