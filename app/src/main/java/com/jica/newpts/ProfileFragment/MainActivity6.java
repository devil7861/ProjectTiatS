package com.jica.newpts.ProfileFragment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jica.newpts.R;

import java.util.Arrays;
import java.util.List;

// MainActivity.java

public class MainActivity6 extends AppCompatActivity {
    // ...

    private List<String> lunchMenus; // 메뉴 항목들

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        // ...

        lunchMenus = Arrays.asList("잡탕밥", "유산슬밥", "팔보채", "유린기", "쟁반짜장");

        Button showDialogButton = findViewById(R.id.showDialogButton);
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   showSearchDialog();*/
                SearchDialog dialog = new SearchDialog(MainActivity6.this, lunchMenus);
                dialog.show();
            }
        });
    }

    private void showSearchDialog() {
        SearchDialog dialog = new SearchDialog(this, lunchMenus);
        dialog.show();
    }
}
