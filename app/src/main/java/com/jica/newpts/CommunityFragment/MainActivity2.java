package com.jica.newpts.CommunityFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jica.newpts.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGES = 101;
    Button selectImagesButton;
    private Button uploadButton;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private RecyclerView photoRecyclerView;
    private PhotoAdapter photoAdapter;
    private StorageReference storageReference; // Firebase Storage 참조
    private TextView selectedImageCountTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Firebase Storage 초기화
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        selectImagesButton = findViewById(R.id.selectImagesButton);
        uploadButton = findViewById(R.id.uploadButton);
        photoRecyclerView = findViewById(R.id.photoRecyclerView);
        selectedImageCountTextView = findViewById(R.id.selectedImageCountTextView); // TextView 초기화

        // 리사이클러뷰 초기화
        photoAdapter = new PhotoAdapter(selectedImageUris, selectedImageCountTextView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false); // 수평 스크롤로 변경
        photoRecyclerView.setLayoutManager(layoutManager);
        photoRecyclerView.setAdapter(photoAdapter);

        selectImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 갤러리에서 이미지 선택을 위한 Intent를 생성
                Intent intent = new Intent();
                // 갤러리 앱에서 이미지 파일만
                intent.setType("image/*");
                // Intent.ACTION_GET_CONTENT를 사용하여 갤러리나 파일 탐색기와 상호 작용하여 컨텐츠(이미지)를 가져올 수 있도록 함
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // 다중 선택(multiple selection)을 허용하는 옵션
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                // 이미지 선택 화면으로 이동
                // "사진 선택"은 다이얼로그의 타이틀(제목)로 표시
                startActivityForResult(Intent.createChooser(intent, "사진을 선택해주세요"), REQUEST_CODE_PICK_IMAGES);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Firebase Storage에 업로드할 이미지 목록이 준비되면
                // 이미지를 하나씩 업로드
                for (Uri imageUri : selectedImageUris) {
                    uploadImage(imageUri);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == RESULT_OK) {
            if (data != null) {
                // 현재 선택된 이미지 갯수
                int currentSelectedCount = selectedImageUris.size();

                // 최대 선택 가능한 이미지 갯수
                int maxSelectableCount = 5;

                // 선택한 이미지 URI를 가져와서 리스트에 추가
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count && currentSelectedCount < maxSelectableCount; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        selectedImageUris.add(imageUri);
                        currentSelectedCount++;
                    }
                } else if (data.getData() != null && currentSelectedCount < maxSelectableCount) {
                    Uri imageUri = data.getData();
                    selectedImageUris.add(imageUri);
                } else {
                    // 선택 가능한 이미지 갯수를 초과한 경우 메시지 표시 또는 처리
                    // 여기에 처리 코드를 추가하세요.
                    Toast.makeText(this, "이미지는 최대 5개까지 업로드 가능합니다", Toast.LENGTH_SHORT).show();
                }

                // RecyclerView에 변경 사항을 알림
                photoAdapter.notifyDataSetChanged();

                // 선택한 이미지 개수를 표시하는 메서드 호출
                updateSelectedImageCount(currentSelectedCount, maxSelectableCount);

                // ------------------------------------------------------------------------
                if (selectedImageUris.size() == 5) {

                    selectImagesButton.setVisibility(View.GONE);
                }
                addTextWatcherForMaxLength(selectedImageCountTextView, 5);
                // ------------------------------------------------------------------------
            }
        }
    }

    // 이미지를 Firebase Storage에 업로드하는 함수
    private void uploadImage(Uri imageUri) {
        // 업로드할 파일 이름 설정 (여기서는 현재 시간을 파일 이름으로 사용)
        String fileName = "photo" + System.currentTimeMillis() + ".jpg";

        // 이미지를 업로드할 경로와 파일 이름 설정
        String uploadPath = "images/1/" + fileName;

        // StorageReference를 사용하여 이미지 업로드
        StorageReference imageRef = storageReference.child(uploadPath);

        // 이미지를 Storage에 업로드
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // 업로드 성공 또는 실패에 대한 리스너 설정
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // 업로드 성공 시 처리 (예: 성공 메시지 표시)
            Toast.makeText(MainActivity2.this, "이미지 업로드 성공", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // 업로드 실패 시 처리 (예: 실패 메시지 표시)
            Toast.makeText(MainActivity2.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
        });
    }

    // 선택한 이미지 개수를 표시하는 메서드
    public void updateSelectedImageCount(int currentSelectedCount, int maxSelectableCount) {
        String countText = currentSelectedCount + "/" + maxSelectableCount;
        selectedImageCountTextView.setText(countText);
    }

    public void addTextWatcherForMaxLength(final TextView textView, final int maxLength) {
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int count = Integer.valueOf(charSequence.toString().substring(0, charSequence.toString().indexOf("/")));
                if (count != maxLength) {
                    selectImagesButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
