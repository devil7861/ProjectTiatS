package com.jica.newpts;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 권한 설정하기
        setPermission();
    }

    // tedpermission 권한 설정
    private void setPermission() {
        // 권한 묻는 팝업 만들기
        PermissionListener permissionListener = new PermissionListener() {
            // 설정해놓은 권한을 허용됐을 때
            @Override
            public void onPermissionGranted() {
                // 1초간 스플래시 보여주기
                ScheduledExecutorService backgroundExecutable = Executors.newSingleThreadScheduledExecutor();
                Executor mainExecutor = ContextCompat.getMainExecutor(SplashActivity.this);
                backgroundExecutable.schedule(() -> {
                    mainExecutor.execute(() -> {
                        // MainActivity 넘어가기
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }, 1, TimeUnit.SECONDS);

                Toast.makeText(SplashActivity.this, "권한 허용", Toast.LENGTH_SHORT).show();
            }

            // 설정해놓은 권한을 거부됐을 때
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                // 권한 없어서 요청
                new AlertDialog.Builder(SplashActivity.this)
                        .setMessage("권한 거절로 인해 일부 기능이 제한됩니다.")
                        .setPositiveButton("권한 설정하러 가기", (dialog, which) -> {
                            try {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:com.example.myweathertest2"));
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .show();

                Toast.makeText(SplashActivity.this, "권한 거부", Toast.LENGTH_SHORT).show();
            }
        };

        // 권한 설정
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("정확한 날씨 정보를 위해 권한을 허용해주세요.")
                .setDeniedMessage("권한을 거부하셨습니다. [앱 설정]->[권한] 항목에서 허용해주세요.")
                .setPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION) // 필수 권한만 문기
                .check();
    }
}
