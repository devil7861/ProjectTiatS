package com.jica.newpts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ViewInfoPlantActivity extends AppCompatActivity {
    Button btnAVIPRegister;
    ImageView btnAVIPClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_info_plant);
        btnAVIPRegister = findViewById(R.id.btnAVIPRegister);
        btnAVIPClose = findViewById(R.id.btnAVIPClose);

        btnAVIPRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog();
            }
        });
        btnAVIPClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), TabLayoutActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void setDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewInfoPlantActivity.this);
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