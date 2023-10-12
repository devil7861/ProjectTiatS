package com.jica.newpts.ProfileFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jica.newpts.EmailLoginActivity;
import com.jica.newpts.R;
import com.jica.newpts.TabLayoutActivity;
import com.jica.newpts.beans.Board;
import com.jica.newpts.beans.RegisterUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileEditActivity extends AppCompatActivity {

    private CardView cvAPECardView;
    private ImageView ivAPEProfilePhoto, ivAPEBackButton, ivAPEEditProfile;
    private Button btnAPEEditPhoto, btnAPEModifyPassword;
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText etAPEUserId, etAPEUserName, etAPEUserPhone, etAPEUserAddress1, etAPEUserAddress2;
    private Uri selectedImageUri; // 이미지를 저장할 변수
    TextView tvAPEDocumentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        cvAPECardView = findViewById(R.id.cvAPECardView);
        ivAPEProfilePhoto = findViewById(R.id.ivAPEProfilePhoto);
        btnAPEEditPhoto = findViewById(R.id.btnAPEEditPhoto);
        ivAPEBackButton = findViewById(R.id.ivAPEBackButton);
        etAPEUserId = findViewById(R.id.etAPEUserId);
        etAPEUserName = findViewById(R.id.etAPEUserName);
        etAPEUserPhone = findViewById(R.id.etAPEUserPhone);
        etAPEUserAddress1 = findViewById(R.id.etAPEUserAddress1);
        etAPEUserAddress2 = findViewById(R.id.etAPEUserAddress2);
        ivAPEEditProfile = findViewById(R.id.ivAPEEditProfile);
        tvAPEDocumentId = findViewById(R.id.tvAPEDocumentId);
        btnAPEModifyPassword = findViewById(R.id.btnAPEModifyPassword);

        db = FirebaseFirestore.getInstance(); // Firestore 초기화
        firebaseAuth = FirebaseAuth.getInstance();

        ReadCurrentUserProfile();
        etAPEUserId.setEnabled(false);
        addTextWatcherForMaxLength(etAPEUserName, 20);
        /* addTextWatcherForMaxLength(etAPEUserPhone, 15);*/
        checkPhoneNumber(etAPEUserPhone);

        btnAPEEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        ivAPEBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivAPEEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadImageToStorage(selectedImageUri);
                Toast.makeText(ProfileEditActivity.this, "프로필을 수정했습니다", Toast.LENGTH_SHORT).show();
            }
        });

        btnAPEModifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEditActivity.this);

                //커스텀 대화상자의 화면구성을 전개한다.
                //이전에 사용했던 방법 - LayoutInflater객체를 구하여 전개
                // 1) getSystemService(LAYOUT_INFLATOR)
                // 2) getLayoutInflator()
                //현재의 예제에서는 전개(inflation)할때 View.inflate()메서드를 사용할 수 도 있다.
                View dialogView = View.inflate(getApplicationContext(), R.layout.dialog_password, null);

                //전개한 화면구성을 대화상자에 설정한다.
                builder.setView(dialogView);
                builder.setPositiveButton("완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etDPPassword = dialogView.findViewById(R.id.etDPPassword);
                        String password = etDPPassword.getText().toString();

                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                        firebaseAuth.signInWithEmailAndPassword(currentUser.getEmail(), password)
                                .addOnCompleteListener(ProfileEditActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            firebaseAuth = FirebaseAuth.getInstance();

                                            firebaseAuth.addAuthStateListener(firebaseAuthListener);
                                        } else {
                                            // 로그인 실패
                                            Snackbar.make(view, "비밀번호가 일치하지 않습니다", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
                            @Override
                            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null) {
                                    showDialog();
                                }
                            }
                        };

                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(view, "비밀번호 변경을 취소하였습니다", Snackbar.LENGTH_SHORT).show();
                    }
                });
                builder.setCancelable(false);

                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }

        });

    }

    // 갤러리 열기
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // 갤러리에서 선택한 이미지 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                // 선택한 이미지를 비트맵으로 로드하여 ImageView에 설정
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                ivAPEProfilePhoto.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ReadCurrentUserProfile() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        db.collection("User")
                .whereEqualTo("u_id", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RegisterUser registerUser = document.toObject(RegisterUser.class);
                            etAPEUserId.setText(registerUser.getU_id());
                            etAPEUserName.setText(registerUser.getU_name());
                            etAPEUserPhone.setText(registerUser.getU_phone());
                            etAPEUserAddress1.setText(registerUser.getU_address1());
                            etAPEUserAddress2.setText(registerUser.getU_address2());
                            Glide.with(getApplication())
                                    .load(registerUser.getU_photo())
                                    .into(ivAPEProfilePhoto);
                            tvAPEDocumentId.setText(String.valueOf(registerUser.getU_idx()));
                        }
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void updateProfile(String imageUrl) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        String name = etAPEUserName.getText().toString();
        String phone = etAPEUserPhone.getText().toString();
        String address1 = etAPEUserAddress1.getText().toString();
        String address2 = etAPEUserAddress2.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(getApplication(), "이름은 필수 입력사항입니다", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("u_name", name);
        updateUser.put("u_phone", phone);
        updateUser.put("u_address1", address1);
        updateUser.put("u_address2", address2);
        updateUser.put("u_photo", imageUrl);

        // "Board" 컬렉션에 사용자 정의 식별자를 가진 문서 추가
        db.collection("User")
                .document(tvAPEDocumentId.getText().toString())
                .update(updateUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공
                        Intent intent = new Intent(getApplicationContext(), TabLayoutActivity.class);
                        intent.putExtra("sendData", "ProfileEditActivity");
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 쓰기 실패
                        Toast.makeText(ProfileEditActivity.this, "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void NoneupdateProfile() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        String name = etAPEUserName.getText().toString();
        String phone = etAPEUserPhone.getText().toString();
        String address1 = etAPEUserAddress1.getText().toString();
        String address2 = etAPEUserAddress2.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(getApplication(), "이름은 필수 입력사항입니다", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("u_name", name);
        updateUser.put("u_phone", phone);
        updateUser.put("u_address1", address1);
        updateUser.put("u_address2", address2);

        // "Board" 컬렉션에 사용자 정의 식별자를 가진 문서 추가
        db.collection("User")
                .document(tvAPEDocumentId.getText().toString())
                .update(updateUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공
                        Intent intent = new Intent(getApplicationContext(), TabLayoutActivity.class);
                        intent.putExtra("sendData", "ProfileEditActivity");
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 쓰기 실패
                        Toast.makeText(ProfileEditActivity.this, "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Firebase Storage에 이미지 업로드
    private void uploadImageToStorage(Uri imageUri) {
        if (imageUri != null) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            // 이미지를 저장할 경로를 지정합니다. 사용자 고유 ID를 이용하거나 원하는 경로로 지정하세요.
            StorageReference imageRef = storageRef.child("profile_images/" + currentUser.getUid() + ".jpg");

            // 이미지 업로드
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // 이미지 업로드 성공
                            // 이미지 다운로드 URL을 가져와 프로필 업데이트에 사용할 수 있습니다.
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    // Firebase Firestore를 사용하여 이미지 URL을 저장하거나 필요한 곳에서 사용하세요.
                                    // 예를 들어, Firestore에 imageUrl을 업데이트하는 코드를 추가하세요.
                                    updateProfile(imageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 이미지 업로드 실패
                            Toast.makeText(ProfileEditActivity.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            NoneupdateProfile();
        }
    }

    public void addTextWatcherForMaxLength(final EditText editText, final int maxLength) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isNameCheck(charSequence.toString()) != false) {
                    if (charSequence.length() >= maxLength) {
                        editText.setError("최대 " + maxLength + "자까지 입력 가능합니다.");
                        editText.setBackgroundResource(R.drawable.drawable_round_rectangle_stroke_solid_red);
                        ivAPEEditProfile.setClickable(false);

                    } else {
                        editText.setError(null);
                        TypedArray styledAttributes = getApplicationContext().getTheme().obtainStyledAttributes(
                                new int[]{android.R.attr.editTextBackground});
                        int editTextBackgroundResource = styledAttributes.getResourceId(0, 0);
                        styledAttributes.recycle();

                        // EditText의 배경 리소스를 기본 배경 리소스로 설정
                        editText.setBackgroundResource(editTextBackgroundResource);
                        ivAPEEditProfile.setClickable(true);
                    }
                } else {
                    editText.setError("이름은 한글/영어로만 작성해주세요");
                    editText.setBackgroundResource(R.drawable.drawable_round_rectangle_stroke_solid_red);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public boolean isPhoneNumberCheck(String cellphoneNumber) {
        boolean returnValue = false;
        String regex = "^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";
        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(cellphoneNumber);

        if (m.matches()) {
            returnValue = true;
        }

        return returnValue;
    }

    public void checkPhoneNumber(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence) || isPhoneNumberCheck(charSequence.toString())) {
                    editText.setError(null);
                    TypedArray styledAttributes = getApplicationContext().getTheme().obtainStyledAttributes(
                            new int[]{android.R.attr.editTextBackground});
                    int editTextBackgroundResource = styledAttributes.getResourceId(0, 0);
                    styledAttributes.recycle();

                    // EditText의 배경 리소스를 기본 배경 리소스로 설정
                    editText.setBackgroundResource(editTextBackgroundResource);
                    ivAPEEditProfile.setClickable(true);
                } else {
                    editText.setError("휴대전화 번호 형식으로 입력해주세요");
                    editText.setBackgroundResource(R.drawable.drawable_round_rectangle_stroke_solid_red);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public boolean isNameCheck(String name) {
        boolean returnValue = false;
        String regex = "^[ㄱ-ㅎ가-힣a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(name);

        if (m.matches()) {
            returnValue = true;
        }

        return returnValue;
    }

    // 다이얼로그를 띄우는 함수
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEditActivity.this);

        //커스텀 대화상자의 화면구성을 전개한다.
        //이전에 사용했던 방법 - LayoutInflater객체를 구하여 전개
        // 1) getSystemService(LAYOUT_INFLATOR)
        // 2) getLayoutInflator()
        //현재의 예제에서는 전개(inflation)할때 View.inflate()메서드를 사용할 수 도 있다.
        View dialogView = View.inflate(getApplicationContext(), R.layout.dialog_modify_password, null);

        //전개한 화면구성을 대화상자에 설정한다.
        builder.setView(dialogView);
        builder.setPositiveButton("완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText etDMPassword1 = dialogView.findViewById(R.id.etDMPassword1);
                String password1 = etDMPassword1.getText().toString();
                EditText etDMPassword2 = dialogView.findViewById(R.id.etDMPassword2);
                String password2 = etDMPassword2.getText().toString();
                if (!password1.equals(password2)) {
                    Toast.makeText(ProfileEditActivity.this, "비밀번호가 서로 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String newPassword = password1; // 새 비밀번호를 설정합니다.

                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileEditActivity.this, "비밀번호를 성공적으로 변경하였습니다", Toast.LENGTH_SHORT).show();
                                } else {
                                    // 비밀번호 변경에 실패한 경우
                                    Exception e = task.getException();
                                    // 오류 처리를 수행할 수 있습니다.
                                    Toast.makeText(ProfileEditActivity.this, "비밀번호 변경에서 에러가 발생하였습니다", Toast.LENGTH_SHORT).show();
                                    if (e != null) {
                                        // 오류 메시지를 Logcat에 출력합니다.
                                        Log.e("PasswordChangeError", e.getMessage());
                                    }
                                }
                            }
                        });

            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Snackbar.make(dialogView, "비밀번호 변경을 취소하였습니다", Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

}