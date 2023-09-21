package com.jica.newpts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class TotalLoginActivity extends AppCompatActivity {

    Button btnTLEmailLogin;
    ImageView ivTLNaver;

    Button btnTLKakao;
    ImageView ivTLGoogle;
    ImageView ivTLfacebook;

    TextView tvTLEmailLogin;
    TextView tvTLFindPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_login);

        btnTLEmailLogin = findViewById(R.id.btnTLEmailLogin);
        ivTLNaver = findViewById(R.id.ivTLNaver);
        btnTLKakao = findViewById(R.id.btnTLKakao);
        ivTLGoogle = findViewById(R.id.ivTLGoogle);
        ivTLfacebook = findViewById(R.id.ivTLfacebook);
        tvTLEmailLogin = findViewById(R.id.tvTLEmailLogin);
        tvTLFindPassword = findViewById(R.id.tvTLFindPassword);

        //이벤트 핸들러 설정
        ButtonHandler buttonHandler = new ButtonHandler();

        btnTLKakao.setOnClickListener(buttonHandler);
        ivTLGoogle.setOnClickListener(buttonHandler);
        tvTLEmailLogin.setOnClickListener(buttonHandler);
        ivTLfacebook.setOnClickListener(buttonHandler);
        tvTLFindPassword.setOnClickListener(buttonHandler);

        btnTLEmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EmailLoginActivity.class);
                startActivity(intent);
            }
        });

        ivTLNaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        // --------------------------------------------

        //버튼클릭시 제품정보를 입력하는 대화상자를 띄운다.
        tvTLFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TotalLoginActivity.this);
                builder.setTitle("재설정할 이메일을 입력해주세요");
                builder.setIcon(R.drawable.search);

                View dialogView = View.inflate(TotalLoginActivity.this, R.layout.dialog_findpassword_input, null);

                builder.setView(dialogView);
                builder.setPositiveButton("입력 완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etDFIEmail = dialogView.findViewById(R.id.etDFIEmail);

                        String emailAddress = etDFIEmail.getText().toString();
                        if(emailAddress.isEmpty()){
                            Toast.makeText(TotalLoginActivity.this,"이메일은 필수입력사항입니다",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(TotalLoginActivity.this, "비밀번호 재설정 이메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(TotalLoginActivity.this, "비밀번호 재설정 이메일 전송 실패", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                builder.setNegativeButton("입력 취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(TotalLoginActivity.this, "비밀번호찾기를 취소했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setCancelable(false);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // -------------------------------------------


    }

    class ButtonHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int curId = view.getId();
            if (curId == R.id.btnTLKakao) {
                Toast.makeText(getApplicationContext(), "카카오 로그인은 차후 지원 예정", Toast.LENGTH_SHORT).show();
            } else if (curId == R.id.ivTLGoogle) {
                Toast.makeText(getApplicationContext(), "구글 로그인은 차후 지원 예정", Toast.LENGTH_SHORT).show();
            } else if (curId == R.id.ivTLfacebook) {
                Toast.makeText(getApplicationContext(), "페이스북 로그인은 차후 지원 예정", Toast.LENGTH_SHORT).show();
            } else if (curId == R.id.tvTLEmailLogin) {
                Intent intent = new Intent(getApplicationContext(), UserRegisterActivity.class);
                startActivity(intent);
            } else if (curId == R.id.tvTLFindPassword) {
                Intent intent = new Intent(getApplicationContext(), FindPasswordActivity.class);
                startActivity(intent);
            }
        }
    }
}