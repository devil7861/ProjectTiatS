package com.jica.newpts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FindPasswordActivity extends AppCompatActivity {

    Button btnFPFindPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        btnFPFindPassword = findViewById(R.id.btnFPFindPassword);

        btnFPFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TotalLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}