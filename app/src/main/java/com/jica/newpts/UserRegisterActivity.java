package com.jica.newpts;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jica.newpts.beans.RegisterUser;

import java.util.regex.Pattern;

public class UserRegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText etURName;
    private EditText etURId;
    private EditText etURPassword;
    private EditText etURPassword2;
    private Button btnURRegister;
    private Button btnRegisterCheck;
    LinearLayout layoutURGoback;
    TextView tvURIdValidation;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        firebaseAuth = FirebaseAuth.getInstance();

        etURName = findViewById(R.id.etURName);
        etURId = findViewById(R.id.etURId);
        etURPassword = findViewById(R.id.etURPassword);
        etURPassword2 = findViewById(R.id.etURPassword2);
        layoutURGoback = findViewById(R.id.layoutURGoback);
        btnURRegister = findViewById(R.id.btnURRegister);
        btnRegisterCheck = findViewById(R.id.btnRegisterCheck);
        tvURIdValidation = findViewById(R.id.tvURIdValidation);

        // Firestore 초기화 (한 번만 호출)
        db = FirebaseFirestore.getInstance();

        btnURRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUserwithNextDocumentId();
                registerUser();
            }
        });

        layoutURGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToTotalLoginActivity();
            }
        });

        btnRegisterCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDuplicateEmail();
            }
        });
        addTextWatcherForMaxLength(etURId,30);
        addTextWatcherForMaxLength(etURPassword,30);
        addTextWatcherForMaxLength(etURPassword2,30);
        addTextWatcherForMaxLength(etURName,10);
    }

    private void registerUser() {
        String id = etURId.getText().toString();
        String password = etURPassword.getText().toString();
        String name = etURName.getText().toString();

        if (id.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "비밀번호는 최소 6자리 이상 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(etURPassword2.getText().toString())) {
            Toast.makeText(this, "비밀번호 입력과 확인이 서로 다릅니다", Toast.LENGTH_SHORT).show();
            return;
        }

        createUser(id, password, name);
    }

    private void navigateToTotalLoginActivity() {
        Intent intent = new Intent(this, TotalLoginActivity.class);
        startActivity(intent);
    }

    private void checkDuplicateEmail() {
        String email = etURId.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, "중복확인할 아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
        if (!pattern.matcher(email).matches()) {
            Toast.makeText(this, "이메일 형식에 맞게 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().getSignInMethods().isEmpty()) {
                            tvURIdValidation.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(this, "중복된 사용자가 존재합니다. 다른 아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                            tvURIdValidation.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthInvalidUserException) {
                            tvURIdValidation.setVisibility(View.VISIBLE);
                        } else {
                            // 기타 예외 처리
                        }
                    }
                });
    }

    private void createUser(String id, String password, String name) {
        firebaseAuth.createUserWithEmailAndPassword(id, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserRegisterActivity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
                        if (pattern.matcher(id).matches()) {
                            Toast.makeText(UserRegisterActivity.this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserRegisterActivity.this, "아이디를 형식에 맞게 작성해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registerUserwithNextDocumentId() {
        // "Board" 컬렉션에 대한 참조
        CollectionReference userCollection = db.collection("User");

        // "Board" 컬렉션을 날짜 필드(f_date)를 기준으로 내림차순으로 정렬하고,
        // 가장 첫 번째 문서를 가져와서 가장 최근 문서의 ID를 확인합니다.
        userCollection.orderBy("u_idx", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Board 컬렉션에 문서가 하나 이상 있는 경우
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            long currentDocumentId = lastDocument.getLong("u_idx");

                            // 현재 문서 ID에 1을 더하여 다음 문서의 ID를 설정합니다.
                            long nextDocumentId = currentDocumentId + 1;

                            // 나머지 저장 로직 구현
                            saveUserData(nextDocumentId);

                        } else {
                            // Board 컬렉션에 문서가 없는 경우, ID를 1로 설정합니다.
                            saveUserData(1);


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 오류 처리
                        Log.e("Tag", "회원등록실패(firestore) : " + e.getMessage());
                    }
                });
    }

    private void saveUserData(long documentId) {
        RegisterUser user = new RegisterUser();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Timestamp date = Timestamp.now();

        String name = etURName.getText().toString(); // 제목 가져오기
        String id = etURId.getText().toString(); // 내용 가져오기


        user.setU_name(name);
        user.setU_id(id);
        user.setU_idx((int) documentId);
        user.setU_date(date);
        // "Board" 컬렉션에 사용자 정의 식별자를 가진 문서 추가
        db.collection("User")
                .document(String.valueOf(documentId)) // 사용자 정의 문서 식별자 설정
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공

                        // EditText 초기화
                        etURName.setText("");
                        etURId.setText("");
                        etURPassword.setText("");
                        etURPassword2.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 쓰기 실패
                        Log.e("Tag", "회원등록실패(firestore) : " + e.getMessage());
                    }
                });
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
                    btnURRegister.setClickable(false);

                } else {
                    editText.setError(null);
                    editText.setBackgroundResource(R.drawable.drawable_round_rectangle_stroke_solid);
                    btnURRegister.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
