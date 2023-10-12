package com.jica.newpts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailLoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText etELId;
    private EditText etELPassword;
    private Button btnELLogin;
    private Button btnELSkip;
    private Button btnRegister;

    TextView tvELFindPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        firebaseAuth = FirebaseAuth.getInstance();

        etELId = findViewById(R.id.etELId);
        etELPassword = findViewById(R.id.etELPassword);
        btnELLogin = findViewById(R.id.btnELLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnELSkip = findViewById(R.id.btnELSkip);
        tvELFindPassword = findViewById(R.id.tvELFindPassword);

        btnELSkip.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), TabLayoutActivity.class)));

        btnRegister.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), UserRegisterActivity.class)));

        btnELLogin.setOnClickListener(view -> {
            String email = etELId.getText().toString();
            String password = etELPassword.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                loginUser(email, password);
            } else {
                Toast.makeText(EmailLoginActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
            }
        });

        tvELFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EmailLoginActivity.this);
                builder.setTitle("재설정할 이메일을 입력해주세요");
                builder.setIcon(R.drawable.search);

                View dialogView = View.inflate(EmailLoginActivity.this, R.layout.dialog_findpassword_input, null);

                builder.setView(dialogView);

                builder.setPositiveButton("입력 완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etDFIEmail = dialogView.findViewById(R.id.etDFIEmail);

                        String emailAddress = etDFIEmail.getText().toString();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        if (emailAddress.isEmpty()) {
                            Toast.makeText(EmailLoginActivity.this, "이메일은 필수입력사항입니다", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(EmailLoginActivity.this, "비밀번호 재설정 이메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EmailLoginActivity.this, "비밀번호 재설정 이메일 전송 실패", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                builder.setNegativeButton("입력 취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(EmailLoginActivity.this, "비밀번호찾기를 취소했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setCancelable(false);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        addTextWatcherForMaxLength(etELId, 30);
        addTextWatcherForMaxLength(etELPassword, 20);
    }

    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            // 로그인 성공
                            Toast.makeText(EmailLoginActivity.this, currentUser.getEmail() + "님 반갑습니다", Toast.LENGTH_SHORT).show();
                            firebaseAuth.addAuthStateListener(firebaseAuthListener);
                        } else {
                            // 로그인 실패
                            Toast.makeText(EmailLoginActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(EmailLoginActivity.this, TabLayoutActivity.class));
                    finish();
                }
            }
        };
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    public void addTextWatcherForMaxLength(final EditText editText, final int maxLength) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= maxLength) {
                    editText.setError("최대 " + maxLength + "자까지 입력 가능합니다.");
                    editText.setBackgroundResource(R.drawable.drawable_round_rectangle_stroke_solid_red);
                    btnELLogin.setClickable(false);

                } else {
                    editText.setError(null);
                    editText.setBackgroundResource(R.drawable.drawable_round_rectangle_stroke_solid);
                    btnELLogin.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
