package com.jica.newpts;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    Button btnNext;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNext = findViewById(R.id.btnNext);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isLoggedIn = checkLoginStatus(); // 이 부분은 실제 로그인 상태 확인 로직을 구현해야 합니다.

                if (!isLoggedIn) {
                    Intent intent = new Intent(getApplicationContext(), TotalLoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), TabLayoutActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        videoView = findViewById(R.id.videoView);

        // VideoView에 동영상 파일 설정

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();

                int viewWidth = videoView.getWidth();
                int viewHeight = videoView.getHeight();

                float scaleX = (float) viewWidth / videoWidth;
                float scaleY = (float) viewHeight / videoHeight;
                float scale = Math.max(scaleX, scaleY);

                int newWidth = Math.round(videoWidth * scale);
                int newHeight = Math.round(videoHeight * scale);

                ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                layoutParams.width = newWidth;
                layoutParams.height = newHeight;
                videoView.setLayoutParams(layoutParams);
            }
        });

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.subbackground;
        videoView.setVideoPath(videoPath);
        // 비디오 재생이 끝나면 반복 재생
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start(); // 비디오 재생 시작
            }
        });
        videoView.start(); // 비디오 재생 시작

    }

    private boolean checkLoginStatus() {
        // Firebase Authentication 인스턴스를 가져옵니다.
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // 현재 로그인된 사용자를 가져옵니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {

            return true;
        } else {
            // 로그인된 사용자가 있다면 로그인 상태로 간주합니다.

            return false;
        }
    }
}