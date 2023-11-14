package com.jica.newpts.CommunityFragment;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jica.newpts.R;
import com.jica.newpts.TabLayoutActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityBoardModifyFragment extends Fragment {
    // 페이지가 내려갔는지 확인
    boolean isPageDown = false;
    String[] items = {"나의화분", "척척석사", "프리티앗"};

    Spinner spFCWSelectBoard;
    ScrollView svFCWBoard;
    EditText etFCWSubject;
    EditText etFCWContent;
    EditText etFCWHashtag;
    Button btnFCWWrite;
    private int setBoard = 1;
    // 최대 선택 가능한 이미지 갯수
    int maxSelectableCount = 5;

    private FirebaseFirestore db;
    private SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy.MM.dd");
    private final int triggerScrollPosition = 20; // 이 위치로 스크롤이 내려갔을 때 작동하도록 설정
    // -----------------------------------------------------------------------------------------
    private static final int REQUEST_CODE_PICK_IMAGES = 101;
    ImageView selectImagesButton;
    private Button uploadButton;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private RecyclerView photoRecyclerView;
    private PhotoAdapter photoAdapter;
    private StorageReference storageReference; // Firebase Storage 참조
    private TextView selectedImageCountTextView;
    // -----------------------------------------------------------------------------------------

     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_board_modify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        spFCWSelectBoard = view.findViewById(R.id.spFCWSelectBoard);
        svFCWBoard = view.findViewById(R.id.svFCWBoard);
        etFCWSubject = view.findViewById(R.id.etFCWSubject);
        etFCWContent = view.findViewById(R.id.etFCWContent);
        etFCWHashtag = view.findViewById(R.id.etFCWHashtag);
        btnFCWWrite = view.findViewById(R.id.btnFCWWrite);

        countCharacterNumber(etFCWSubject, 20);
        countCharacterNumber(etFCWContent, 2000);

        // --------------------------------------------------------------
        // Firebase Storage 초기화
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        selectImagesButton = view.findViewById(R.id.selectImagesButton);
        uploadButton = view.findViewById(R.id.uploadButton);
        photoRecyclerView = view.findViewById(R.id.photoRecyclerView);
        selectedImageCountTextView = view.findViewById(R.id.selectedImageCountTextView); // TextView 초기화
        countCharacterNumber(selectedImageCountTextView,maxSelectableCount);
        if (bundle != null) {
            int f_board_idx = bundle.getInt("f_board_idx");
            String f_subject = bundle.getString("f_subject");
            String f_user = bundle.getString("f_user");
            String f_hashtag = bundle.getString("f_hashtag");
            String f_content = bundle.getString("f_content");
            ArrayList<String> f_photo = bundle.getStringArrayList("f_photo"); // 이미지는 뒤에서 세팅
            int f_board_info_idx = bundle.getInt("f_board_info_idx");


            // --------------------------------------------------------------

            // Firestore 초기화 (한 번만 호출)
            db = FirebaseFirestore.getInstance();

            // 어뎁터 생성
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, items);

            // 스피너 선택시 드롭다운되어 보여질 목록에 대한 xml 레이아웃 지정
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            // 어뎁터를 Spinner에 연동
            spFCWSelectBoard.setAdapter(adapter);

            // -----------------------------------------


            if (f_board_info_idx == 1) {
                spFCWSelectBoard.setSelection(0);
                setBoard = 1;
            } else if (f_board_info_idx == 2) {
                spFCWSelectBoard.setSelection(1);
                setBoard = 2;
            } else if (f_board_info_idx == 3) {
                spFCWSelectBoard.setSelection(2);
                setBoard = 3;
            }
            etFCWSubject.setText(f_subject);
            etFCWContent.setText(f_content);
            etFCWHashtag.setText(f_hashtag);


            ConstraintLayout clFCTopMenu = getActivity().findViewById(R.id.clFCTopMenu);
            svFCWBoard.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > triggerScrollPosition) {
                        clFCTopMenu.setVisibility(View.GONE);
                        isPageDown = true;
                    }
                }
            });
            // --------------------------------------------

            // --------------------------------------------사진 업로드
            // 리사이클러뷰 초기화
            photoAdapter = new PhotoAdapter(selectedImageUris, selectedImageCountTextView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false); // 수평 스크롤로 변경
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

            // --------------------------------------------사진 업로드


            // 스피너가 드롭다운 되어있을때 항목을 선택했을때 작동할 기능
            spFCWSelectBoard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    if (position == 0) {
                        setBoard = 1;
                    } else if (position == 1) {
                        setBoard = 2;
                    } else if (position == 2) {
                        setBoard = 3;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });


            // f_photo 목록에 있는 이미지 다운로드 및 RecyclerView에 추가
            for (String imageUrl : f_photo) {
                downloadAndAddImage(imageUrl);
            }

            btnFCWWrite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveBoardData(f_board_idx);
                    Intent intent = new Intent(getActivity(), TabLayoutActivity.class);
                    intent.putExtra("sendData", "CommunityBoardModifyFragment");
                    startActivity(intent);

                }
            });

        }
    }

    // ------------------------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                // 현재 선택된 이미지 갯수
                int currentSelectedCount = selectedImageUris.size();

                // 선택한 이미지 URI를 가져와서 리스트에 추가(getData단일, getClip다중선택)
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    if (count > maxSelectableCount) {
                        Toast.makeText(getContext(), "이미지는 최대 " + maxSelectableCount + "개 까지만 선택가능합니다", Toast.LENGTH_SHORT).show();
                    }
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
                }

                // RecyclerView에 변경 사항을 알림
                photoAdapter.notifyDataSetChanged();

                // 선택한 이미지 개수를 표시하는 메서드 호출
                updateSelectedImageCount(currentSelectedCount, maxSelectableCount);

                // ------------------------------------------------------------------------
                if (selectedImageUris.size() == maxSelectableCount) {
                    selectImagesButton.setVisibility(View.GONE);
                }
                countCharacterNumber(selectedImageCountTextView, maxSelectableCount);
                // ------------------------------------------------------------------------
            }
        }
    }

    // 이미지를 Firebase Storage에 업로드하는 함수
    private void uploadImage(Uri imageUri, long f_board_idx) {
        // 업로드할 파일 이름 설정 (여기서는 현재 시간을 파일 이름으로 사용)
        String fileName = "photo" + System.currentTimeMillis() + ".jpg";

        // 이미지를 업로드할 경로와 파일 이름 설정
        String uploadPath = "board/" + f_board_idx + "/" + fileName;

        // StorageReference를 사용하여 이미지 업로드
        StorageReference imageRef = storageReference.child(uploadPath);

        // 이미지를 Storage에 업로드
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // 업로드 성공 또는 실패에 대한 리스너 설정
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // 업로드 성공 시 처리
                // 이미지 업로드가 성공하면 해당 이미지의 다운로드 URL을 가져옵니다.
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // 다운로드 URL을 가져온 후에 Firestore의 "Board" 문서에 저장합니다.
                        String downloadUrl = uri.toString();
                        updateBoardDownloadUrl(f_board_idx, downloadUrl);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 다운로드 URL을 가져오는데 실패한 경우 처리
                        Toast.makeText(getActivity(), "다운로드 URL 가져오기 실패", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 업로드 실패 시 처리 (예: 실패 메시지 표시)
                Toast.makeText(getActivity(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // "Board" 문서의 "f_down_url" 필드를 업데이트하는 함수
    private void updateBoardDownloadUrl(long f_board_idx, String downloadUrl) {
        // "Board" 컬렉션에 대한 참조
        CollectionReference boardCollection = db.collection("Board");

        // "Board" 컬렉션에서 해당 게시물을 찾아 업데이트
        boardCollection.document(String.valueOf(f_board_idx))
                .update("f_down_url", downloadUrl)
                .addOnSuccessListener(aVoid -> {
                    // 업데이트 성공 시 처리
                    // Toast.makeText(getActivity(), "다운로드 URL 업데이트 성공", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // 업데이트 실패 시 처리
                    // Toast.makeText(getActivity(), "다운로드 URL 업데이트 실패", Toast.LENGTH_SHORT).show();
                });
    }

    // 선택한 이미지 개수를 표시하는 메서드
    public void updateSelectedImageCount(int currentSelectedCount, int maxSelectableCount) {
        String countText = currentSelectedCount + "/" + maxSelectableCount;
        selectedImageCountTextView.setText(countText);
    }

    public void countCharacterNumber(final TextView textView, final int maxLength) {
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
    // ----------------------------------------------------------------------------------------

    private void showToastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void saveBoardDataWithNextDocumentId() {
        // "Board" 컬렉션에 대한 참조
        CollectionReference boardCollection = db.collection("Board");

        // "Board" 컬렉션을 날짜 필드(f_date)를 기준으로 내림차순으로 정렬하고,
        // 가장 첫 번째 문서를 가져와서 가장 최근 문서의 ID를 확인합니다.
        boardCollection.orderBy("f_board_idx", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Board 컬렉션에 문서가 하나 이상 있는 경우
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            long currentDocumentId = lastDocument.getLong("f_board_idx");

                            // 현재 문서 ID에 1을 더하여 다음 문서의 ID를 설정합니다.
                            long nextDocumentId = currentDocumentId + 1;

                            // 나머지 저장 로직 구현
                            saveBoardData(nextDocumentId);


                        } else {
                            // Board 컬렉션에 문서가 없는 경우, ID를 1로 설정합니다.
                            saveBoardData(1);

                            // Firebase Storage에 업로드할 이미지 목록이 준비되면
                            // 이미지를 하나씩 업로드
                            for (Uri imageUri : selectedImageUris) {
                                uploadImage(imageUri, 1);
                            }

                            // 이미지 선택 초기화
                            resetImageSelect();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 오류 처리
                        showToastMessage("문서 ID를 설정하는 중에 오류가 발생했습니다.");
                    }
                });
    }

    private void saveBoardData(long documentId) {
        deleteFileFromStorage("board/" + documentId + "/");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // 현재 로그인된 사용자를 가져옵니다.

        Timestamp date = Timestamp.now();

        String subject = etFCWSubject.getText().toString(); // 제목 가져오기
        String content = etFCWContent.getText().toString(); // 내용 가져오기
        String hashtag = etFCWHashtag.getText().toString(); // 해시태그 가져오기
        String profile = "board/" + documentId;
        if (subject.isEmpty()) {
            Toast.makeText(getActivity(), "제목은 필수 입력사항입니다", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.isEmpty()) {
            Toast.makeText(getActivity(), "내용은 필수 입력사항입니다", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> updateBoard = new HashMap<>();
        updateBoard.put("f_subject", subject);
        updateBoard.put("f_context", content);
        updateBoard.put("f_hashtag", hashtag);
        updateBoard.put("f_board_info_idx", setBoard);
        updateBoard.put("f_modify_date", date);
        updateBoard.put("f_down_url", "");


        // "Board" 컬렉션에 사용자 정의 식별자를 가진 문서 추가
        db.collection("Board")
                .document(String.valueOf(documentId)) // 사용자 정의 문서 식별자 설정
                .update(updateBoard)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공

                        // EditText 초기화
                        etFCWSubject.setText("");
                        etFCWContent.setText("");
                        etFCWHashtag.setText("");
                        String boardTitle = "";
                        if (setBoard == 1) {
                            boardTitle = items[0].toString();
                        } else if (setBoard == 2) {
                            boardTitle = items[1].toString();
                        } else if (setBoard == 3) {
                            boardTitle = items[2].toString();
                        }
                        // Firebase Storage에 업로드할 이미지 목록이 준비되면
                        // 이미지를 하나씩 업로드
                        for (Uri imageUri : selectedImageUris) {
                            uploadImage(imageUri, documentId);
                        }

                        // 이미지 선택 초기화
                        resetImageSelect();

                        showToastMessage(boardTitle + " 게시판에 저장을 완료했습니다.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 쓰기 실패
                        showToastMessage("저장을 실패했습니다.");
                    }
                });
    }

    private void resetImageSelect() {
        selectedImageUris.clear(); // 글 작성 후 이미지 선택 해제
        photoAdapter = new PhotoAdapter(selectedImageUris, selectedImageCountTextView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false); // 수평 스크롤로 변경
        photoRecyclerView.setLayoutManager(layoutManager);
        photoRecyclerView.setAdapter(photoAdapter);
        photoAdapter.notifyDataSetChanged();
        updateSelectedImageCount(0, maxSelectableCount);
    }

    private void downloadAndAddImage(String imageUrl) {
        // 이미지 다운로드
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        File localFile = null;

        try {
            // 이미지를 저장할 로컬 파일을 생성
            localFile = File.createTempFile("images", "jpg");

            // 다운로드한 이미지를 로컬 파일로 저장
            File finalLocalFile = localFile;
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // 이미지 다운로드 및 로컬 저장 성공 시
                    Uri localUri = Uri.fromFile(finalLocalFile);

                    // RecyclerView에 이미지 추가
                    selectedImageUris.add(localUri);
                    photoAdapter.notifyDataSetChanged();
                    if (selectedImageUris.size() == 5) {
                        selectImagesButton.setVisibility(View.GONE);
                    }
                    // 이미지 선택 개수 업데이트
                    updateSelectedImageCount(selectedImageUris.size(), maxSelectableCount);

                    // 디버깅을 위한 로그
                    Log.d("ImageDownload", "Image downloaded successfully: " + localUri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 이미지 다운로드 실패 시 처리
                    Toast.makeText(getActivity(), "이미지 다운로드 실패", Toast.LENGTH_SHORT).show();

                    // 디버깅을 위한 로그
                    Log.e("ImageDownload", "Image download failed: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void deleteFileFromStorage(String filePath) {
        // Firebase Storage의 루트 참조를 가져옵니다.
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();


// 폴더 내의 모든 파일 목록을 가져오는 Task를 생성합니다.
        Task<ListResult> listAllTask = storageRef.child(filePath).listAll();

        listAllTask.addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                // 폴더 내의 모든 파일을 순회하며 삭제합니다.
                for (StorageReference item : listResult.getItems()) {
                    item.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // 파일 삭제 성공
                            String deletedFileName = item.getName();
                            // 삭제한 파일에 대한 처리 (예: 로그 기록)
                            Log.d("StorageDelete", "Deleted file: " + deletedFileName);
                        }
                    });
                }
            }
        });
    }

    public void countCharacterNumber(final EditText editText, final int maxLength) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= maxLength) {
                    editText.setError("최대 " + maxLength + "자까지 입력 가능합니다.");
                    editText.setBackgroundResource(R.drawable.drawable_round_rectangle_stroke_solid_red);
                    btnFCWWrite.setClickable(false);

                } else {
                    editText.setError(null);
                    editText.setBackgroundResource(R.drawable.drawable_round_rectangle_stroke_solid);
                    btnFCWWrite.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}