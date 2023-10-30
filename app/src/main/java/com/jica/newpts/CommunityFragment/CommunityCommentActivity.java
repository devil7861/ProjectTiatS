package com.jica.newpts.CommunityFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jica.newpts.R;
import com.jica.newpts.TotalLoginActivity;
import com.jica.newpts.beans.Comment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommunityCommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private ArrayList<Comment> arrayList;
    private FirebaseFirestore db;
    TextView tvACCCountComment;
    TextView tvACCSubject;
    EditText etACCWriteComment;
    Button btnACCCommentRegister;
    private boolean subCommentLayoutOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_comment);
        Intent receiveIntent = getIntent();
        int f_board_idx = Integer.valueOf(receiveIntent.getStringExtra("f_board_idx"));
        String f_subject = receiveIntent.getStringExtra("f_subject");
        String f_user = receiveIntent.getStringExtra("f_user");

        tvACCCountComment = findViewById(R.id.tvACCCountComment);
        tvACCSubject = findViewById(R.id.tvACCSubject);
        etACCWriteComment = findViewById(R.id.etACCWriteComment);
        btnACCCommentRegister = findViewById(R.id.btnACCCommentRegister);

        addTextWatcherForMaxLength(etACCWriteComment, 0);

        recyclerView = findViewById(R.id.recyclerView_comment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        arrayList = new ArrayList<>();
        adapter = new CommentAdapter(arrayList, getApplicationContext(), f_user);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // 현재 로그인된 사용자를 가져옵니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // 댓글을 로드하고 RecyclerView를 설정
        loadComments(f_board_idx, f_subject);


        adapter.setOnItemClickListener(new OnCommentItemClickListener() {
            @Override
            public void OnItemClick(CommentAdapter.CommentViewholder viewHolder, View view, int position) {
                if (currentUser != null) {
                    viewHolder.btnLRIReCommentRegister.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String reComment = viewHolder.btnLRIReCommentWrite.getText().toString();
                            String check_level = arrayList.get(position).getR_check_level();

                            int r_level_idx = arrayList.get(position).getR_level_idx();
                            loadCurrentUserandSubWrite(f_board_idx, f_subject, Integer.valueOf(arrayList.get(position).getR_parent_idx()), reComment, r_level_idx, check_level);
                            /*saveSubCommentDataWithNextDocumentId(String.valueOf(f_board_idx), f_subject, Integer.valueOf(arrayList.get(position).getR_parent_idx()), reComment, r_level_idx, check_level);*/
                            countComment(f_board_idx);
                            viewHolder.btnLRIReCommentLayout.setVisibility(View.GONE);
                            viewHolder.btnLRIReCommentWrite.setText("");
                        }
                    });
                } else {
                    requireLogin();
                }
            }
        });


        btnACCCommentRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    loadCurrentUserandWrite(f_board_idx, f_subject);

                    countComment(f_board_idx);
                } else {
                    requireLogin();
                }
            }
        });


        ImageView backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼을 클릭했을 때의 동작을 정의합니다.
                // 이 코드에서는 프래그먼트로 돌아가는 예제를 보여줍니다.
                FragmentManager fragmentManager = getSupportFragmentManager();

                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                    // 뒤로 가기 동작 후 특정 프래그먼트의 메소드를 호출합니다.
                    CommunityBoardReadFragment fragment = (CommunityBoardReadFragment) fragmentManager.findFragmentById(R.id.child_container_community);
                    Bundle bundle = new Bundle();
                    bundle.putInt("f_board_idx", Integer.valueOf(f_subject)); // 데이터 추가

                } else {
                    // 스택에 이전 프래그먼트가 없는 경우 기본 뒤로 가기 동작을 실행합니다.
                    finish(); // 현재 액티비티를 종료하여 이전 화면으로 돌아갑니다.
                }
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
                if (charSequence.length() > maxLength) {
                    btnACCCommentRegister.setVisibility(View.VISIBLE);
                } else {
                    btnACCCommentRegister.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void showToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    /*  private void loadComments(int f_board_idx, String subject) {
          DocumentReference boardRef = db.collection("Board").document(String.valueOf(f_board_idx));

          boardRef.collection("Comment")
                  .orderBy("r_check_level", Query.Direction.ASCENDING)
                  *//*   .orderBy("r_child_idx", Query.Direction.ASCENDING)*//*
     *//*  .whereEqualTo("r_level_idx", 0)*//*
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comment comment = document.toObject(Comment.class);
                            arrayList.add(comment);
                        }
                        adapter.notifyDataSetChanged();
                        tvACCCountComment.setText("댓글 " + arrayList.size() + "개");
                        tvACCSubject.setText(subject);
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());

                    }
                });
    }*/
    // 실시간 업데이트추가
    private void loadComments(int f_board_idx, String subject) {
        DocumentReference boardRef = db.collection("Board").document(String.valueOf(f_board_idx));

        boardRef.collection("Comment")
                .orderBy("r_check_level", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("TAG", "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && !snapshot.isEmpty()) {
                            arrayList.clear();
                            for (QueryDocumentSnapshot document : snapshot) {
                                Comment comment = document.toObject(Comment.class);
                                arrayList.add(comment);
                            }
                            adapter.notifyDataSetChanged();
                            tvACCCountComment.setText("댓글 " + arrayList.size() + "개");
                            tvACCSubject.setText(subject);
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    }
                });
    }

    private void saveCommentDataWithNextDocumentId(String boardId, String subject, String uPhoto, String uName) {
        // "Board" 컬렉션에 대한 참조
        DocumentReference boardRef = db.collection("Board").document(boardId);

        // "Comment" 서브컬렉션 참조
        CollectionReference commentCollectionRef = boardRef.collection("Comment");

        // "Board" 컬렉션을 날짜 필드(f_date)를 기준으로 내림차순으로 정렬하고,
        // 가장 첫 번째 문서를 가져와서 가장 최근 문서의 ID를 확인합니다.
        commentCollectionRef.orderBy("r_date", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Board 컬렉션에 문서가 하나 이상 있는 경우
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            long currentDocumentId = lastDocument.getLong("r_idx");

                            // 현재 문서 ID에 1을 더하여 다음 문서의 ID를 설정합니다.
                            long nextDocumentId = currentDocumentId + 1;

                            // 나머지 저장 로직 구현
                            addCommentToBoard(boardId, nextDocumentId, subject, uPhoto, uName);
                        } else {
                            // Board 컬렉션에 문서가 없는 경우, ID를 1로 설정합니다.
                            addCommentToBoard(boardId, 1, subject, uPhoto, uName);
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

    public void addCommentToBoard(String boardId, long nextDocumentId, String subject, String uPhoto, String uName) {
        Comment comment = new Comment();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // "Board" 컬렉션의 "boardId" 문서 참조
        DocumentReference boardRef = db.collection("Board").document(boardId);

        // "Comment" 서브컬렉션 참조
        CollectionReference commentCollectionRef = boardRef.collection("Comment");

        // 현재 로그인된 사용자를 가져옵니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Timestamp date = Timestamp.now();

        String content = etACCWriteComment.getText().toString(); // 내용 가져오기

        /*String photo = "https://firebasestorage.googleapis.com/v0/b/newpts-26161.appspot.com/o/profile.png?alt=media&token=b1f1cdfd-0932-4603-bc50-70428a419da bv 6";*/
        String photo = uPhoto;
        comment.setR_idx((int) nextDocumentId);
        comment.setR_parent_idx((int) nextDocumentId);
        comment.setR_child_idx(0);
        comment.setR_content(content);
        comment.setR_user(currentUser != null ? currentUser.getEmail() : ""); // currentUser가 null이 아닌지 확인 후 getEmail 호출
        comment.setR_date(date);
        comment.setR_profile_photo(photo);
        // ----------------------------------------------------------
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date curruentdate = new Date(System.currentTimeMillis());
        // 문자정렬때문에 10이 넘어가면 1,10,2,3,4,5,6,7,8,9로 바꿔서 구조변경
        comment.setR_check_level(formatter.format(curruentdate).toString());

        // ---------------------------------------------------------
        /*  comment.setR_check_level(String.valueOf(nextDocumentId));*/
        comment.setR_name(uName);

        // 댓글 추가
        commentCollectionRef
                .document(String.valueOf(nextDocumentId)) // 사용자 정의 문서 식별자 설정
                .set(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공

                        // EditText 초기화
                        etACCWriteComment.setText("");

                        showToastMessage("글작성을 완료했습니다.");
                        // 댓글 추가 후 RecyclerView를 다시 로드하여 업데이트
                        loadComments(Integer.valueOf(boardId), subject);
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


    private void saveSubCommentDataWithNextDocumentId(String boardId, String subject, int parentIdx, String reComment, int r_level_idx, String check_level, String uPhoto, String Uname) {
        // "Board" 컬렉션에 대한 참조
        DocumentReference boardRef = db.collection("Board").document(boardId);

        // "Comment" 서브컬렉션 참조
        CollectionReference commentCollectionRef = boardRef.collection("Comment");

        // "Board" 컬렉션을 날짜 필드(f_date)를 기준으로 내림차순으로 정렬하고,
        // 가장 첫 번째 문서를 가져와서 가장 최근 문서의 ID를 확인합니다.
        commentCollectionRef.orderBy("r_date", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Board 컬렉션에 문서가 하나 이상 있는 경우
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            long currentDocumentId = lastDocument.getLong("r_idx");

                            // 현재 문서 ID에 1을 더하여 다음 문서의 ID를 설정합니다.
                            long nextDocumentId = currentDocumentId + 1;
                            saveSubCommentDataWithParentIdx(boardId, subject, nextDocumentId, parentIdx, reComment, r_level_idx, check_level, uPhoto, Uname);
                        } else {
                            // Board 컬렉션에 문서가 없는 경우, ID를 1로 설정합니다.
                            long nextDocumentId = 1;
                            saveSubCommentDataWithParentIdx(boardId, subject, 1, parentIdx, reComment, r_level_idx, check_level, uPhoto, Uname);
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

    private void saveSubCommentDataWithParentIdx(String boardId, String subject, long documentId, int parentIdx, String reComment, int r_level_idx, String check_level, String Uphoto, String Uname) {
        // "Board" 컬렉션에 대한 참조
        DocumentReference boardRef = db.collection("Board").document(boardId);

        // "Comment" 서브컬렉션 참조
        CollectionReference commentCollectionRef = boardRef.collection("Comment");

        // "Board" 컬렉션을 날짜 필드(f_date)를 기준으로 내림차순으로 정렬하고,
        // 가장 첫 번째 문서를 가져와서 가장 최근 문서의 ID를 확인합니다.
        commentCollectionRef
                .whereEqualTo("r_parent_idx", parentIdx) // r_parent_idx가 2인 문서만 가져오도록 조건 추가
                .orderBy("r_date", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Board 컬렉션에 문서가 하나 이상 있는 경우
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            long currentChildIdx = lastDocument.getLong("r_child_idx");

                            // 현재 문서 ID에 1을 더하여 다음 문서의 ID를 설정합니다.
                            long nextChildIdx = currentChildIdx + 1;

                            // 나머지 저장 로직 구현
                            addSubCommentToBoard(boardId, documentId, subject, parentIdx, nextChildIdx, reComment, r_level_idx, check_level, Uphoto, Uname);
                        } else {
                            long currentChildIdx = 0;

                            // 현재 문서 ID에 1을 더하여 다음 문서의 ID를 설정합니다.
                            long nextChildIdx = currentChildIdx + 1;

                            // 나머지 저장 로직 구현
                            addSubCommentToBoard(boardId, documentId, subject, parentIdx, nextChildIdx, reComment, r_level_idx, check_level, Uphoto, Uname);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 오류 처리
                        showToastMessage("문서 ID를 설정하는 중에 오류가 발생했습니다.");
                        Log.e("FirestoreError", "Firestore 오류: " + e.getMessage());
                    }
                });
    }

    public void addSubCommentToBoard(String boardId, long nextDocumentId, String subject, int parentIdx, long childIdx, String reComment, int r_level_idx, String check_level, String Uphoto, String Uname) {
        Comment comment = new Comment();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // "Board" 컬렉션의 "boardId" 문서 참조
        DocumentReference boardRef = db.collection("Board").document(boardId);

        // "Comment" 서브컬렉션 참조
        CollectionReference commentCollectionRef = boardRef.collection("Comment");

        // 현재 로그인된 사용자를 가져옵니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Timestamp date = Timestamp.now();

        String content = reComment; // 내용 가져오기
        int r_next_level_idx = r_level_idx + 1;
        comment.setR_idx((int) nextDocumentId);
        comment.setR_level_idx(r_next_level_idx);
        comment.setR_parent_idx(parentIdx);
        comment.setR_child_idx((int) childIdx);
        comment.setR_content(content);
        comment.setR_user(currentUser != null ? currentUser.getEmail() : ""); // currentUser가 null이 아닌지 확인 후 getEmail 호출
        comment.setR_date(date);
        comment.setR_profile_photo(Uphoto);
        comment.setR_name(Uname);
        // --------------------------------------------
        String originalString = check_level;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date curruentdate = new Date(System.currentTimeMillis());
        comment.setR_check_level(check_level + "-" + formatter.format(curruentdate).toString());
        // ---------------------------------------------

        // 댓글 추가
        commentCollectionRef
                .document(String.valueOf(nextDocumentId)) // 사용자 정의 문서 식별자 설정
                .set(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공

                        // EditText 초기화
                     /*   et_user_name.setText("");
                        et_user_email.setText("");*/

                        showToastMessage("글작성을 완료했습니다.");

                        // 댓글 추가 후 RecyclerView를 다시 로드하여 업데이트
                        loadComments(Integer.valueOf(boardId), subject);
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

    private void countComment(int document) {
        // 가져오려는 문서 ID를 사용하여 DocumentReference를 얻습니다.
        String documentId = String.valueOf(document); // 가져오려는 문서의 ID, 필요한 ID로 변경하세요.
        DocumentReference docRef = db.collection("Board").document(documentId);

// 문서 데이터를 가져옵니다.
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // 문서가 존재하는 경우
                            Long f_count_comment = documentSnapshot.getLong("f_count_comment");
                            String count = f_count_comment + "";
                            addCommentCount(document, Integer.valueOf(count));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 데이터 가져오기에 실패한 경우
                        // 에러 처리
                    }
                });
    }

    private void addCommentCount(int documentId, int f_count_comment) {
        DocumentReference boardRef = db.collection("Board").document(String.valueOf(documentId));
        Map<String, Object> updateCount = new HashMap<>();
        updateCount.put("f_count_comment", f_count_comment + 1);
        boardRef.update(updateCount)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 쓰기 실패
                    }
                });
    }

    public void requireLogin() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getApplicationContext());
        builder.setTitle("로그인이 필요합니다");
        builder.setIcon(android.R.drawable.ic_dialog_alert); //안드로이드에서 제공하는 아이콘 이미지 사용
        builder.setMessage("로그인이 필요한 행동입니다\n계속하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), TotalLoginActivity.class);
                startActivity(intent);
            }
        });    //긍정 버튼 - BUTTON_POSITIVE (-1)
        //builder.setNeutralButton("확인", null);   //확인 버튼 - BUTTON_NEUTRAL (-3)
        builder.setNegativeButton("아니오", null); //부정 버튼 -  BUTTON_NEGATIVE (-2)

        //대화상자가 보여진 이후에는 반드시 대화상자의 버튼으로만 대화상자가 종료하도록 한다.
        builder.setCancelable(false);

        //대화상자 만들기
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        // 다이얼로그가 나타날 때 배경을 반투명하게 설정


        alertDialog.show(); //대화상자 보이기
    }

    public void loadCurrentUserandWrite(int f_board_idx, String f_subject) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        db.collection("User")
                .whereEqualTo("u_id", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String uPhoto = document.getString("u_photo");
                            String uName = document.getString("u_name");
                            // u_photo 필드의 값(uPhoto)를 사용하거나 저장하세요.
                            // 예를 들어, 이미지 뷰에 로드하는 등의 작업을 수행할 수 있습니다.
                            saveCommentDataWithNextDocumentId(String.valueOf(f_board_idx), f_subject, uPhoto, uName);
                        }

                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void loadCurrentUserandSubWrite(int f_board_idx, String f_subject, int parent_idx, String reComment, int r_level_idx, String check_level) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        db.collection("User")
                .whereEqualTo("u_id", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String uPhoto = document.getString("u_photo");
                            String uName = document.getString("u_name");
                            // u_photo 필드의 값(uPhoto)를 사용하거나 저장하세요.
                            // 예를 들어, 이미지 뷰에 로드하는 등의 작업을 수행할 수 있습니다.
                            saveSubCommentDataWithNextDocumentId(String.valueOf(f_board_idx), f_subject, parent_idx, reComment, r_level_idx, check_level, uPhoto, uName);
                        }

                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }
}