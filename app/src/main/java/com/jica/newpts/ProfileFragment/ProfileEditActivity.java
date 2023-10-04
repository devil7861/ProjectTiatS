package com.jica.newpts.ProfileFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jica.newpts.R;
import com.jica.newpts.beans.Board;
import com.jica.newpts.beans.RegisterUser;

public class ProfileEditActivity extends AppCompatActivity {

    private CardView cvAPECardView;
    private ImageView ivAPEProfilePhoto, ivAPEBackButton;
    private Button btnAPEEditPhoto;
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseFirestore db;
    private EditText etAPEUserId, etAPEUserName, etAPEUserPhone, etAPEUserAddress1, etAPEUserAddress2;

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
        etAPEUserAddress2 = findViewById(R.id.etAPEUserAddress1);

        db = FirebaseFirestore.getInstance(); // Firestore 초기화

        ReadCurrentUserProfile();
        etAPEUserId.setEnabled(false);

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
            Uri imageUri = data.getData();
            try {
                // 선택한 이미지를 비트맵으로 로드하여 ImageView에 설정
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
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
                        }
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }
}