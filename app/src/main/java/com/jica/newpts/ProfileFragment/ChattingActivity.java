package com.jica.newpts.ProfileFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.jica.newpts.CommunityFragment.CommentAdapter;
import com.jica.newpts.CommunityFragment.CommunityBoardReadFragment;
import com.jica.newpts.R;
import com.jica.newpts.TabLayoutActivity;
import com.jica.newpts.beans.Chatting;
import com.jica.newpts.beans.Comment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChattingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChattingAdapter adapter;
    private ArrayList<Chatting> arrayList;
    private FirebaseFirestore db;
    TextView tvACCCountComment;
    TextView tvACCSubject;
    EditText etACCWriteComment;
    Button btnACCCommentRegister, btnACOut;
    ImageView backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        Intent receiveIntent = getIntent();
        int c_idx = Integer.valueOf(receiveIntent.getStringExtra("c_idx"));
        String c_subject = receiveIntent.getStringExtra("c_subject");


        tvACCCountComment = findViewById(R.id.tvACCCountComment);
        tvACCSubject = findViewById(R.id.tvACCSubject);
        etACCWriteComment = findViewById(R.id.etACCWriteComment);
        btnACCCommentRegister = findViewById(R.id.btnACCCommentRegister);
        backButton = findViewById(R.id.backButton);
        btnACOut = findViewById(R.id.btnACOut);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // 현재 로그인된 사용자를 가져옵니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        tvACCSubject.setText(currentUser.getEmail() + "님 안녕하세요");
        tvACCCountComment.setText("유저간 이야기");

        addTextWatcherForMaxLength(etACCWriteComment, 0);

        recyclerView = findViewById(R.id.recyclerView_comment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        arrayList = new ArrayList<>();
        adapter = new ChattingAdapter(arrayList, getApplicationContext());
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // 댓글을 로드하고 RecyclerView를 설정
        loadComments(c_idx);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), TabLayoutActivity.class);
                intent.putExtra("sendData", "ChattingActivity");
                startActivity(intent);
            }
        });
        btnACCCommentRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCommentDataWithNextDocumentId(String.valueOf(c_idx), c_subject);
            }
        });
        btnACOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChattingActivity.this);
                builder.setTitle("채팅방 나가기");
                builder.setIcon(android.R.drawable.ic_dialog_alert); //안드로이드에서 제공하는 아이콘 이미지 사용
                builder.setMessage("채팅방을 나가겠습니까??");

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        etACCWriteComment.setText("< " + currentUser.getEmail() + "님이\n 채팅방을 나갔습니다 >");
                        saveCommentDataWithNextDocumentId(String.valueOf(c_idx), c_subject);
                        deleteBoard(c_idx);
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
    private void loadComments(int f_board_idx) {
        DocumentReference boardRef = db.collection("Chatting").document(String.valueOf(f_board_idx));

        boardRef.collection("Chat")
                .orderBy("c_idx", Query.Direction.ASCENDING)
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
                                Chatting chatting = document.toObject(Chatting.class);
                                arrayList.add(chatting);
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d("TAG", "No such document");
                        }
                    }
                });
    }

    private void saveCommentDataWithNextDocumentId(String boardId, String subject) {
        // "Board" 컬렉션에 대한 참조
        DocumentReference boardRef = db.collection("Chatting").document(boardId);

        // "Comment" 서브컬렉션 참조
        CollectionReference commentCollectionRef = boardRef.collection("Chat");

        // "Board" 컬렉션을 날짜 필드(f_date)를 기준으로 내림차순으로 정렬하고,
        // 가장 첫 번째 문서를 가져와서 가장 최근 문서의 ID를 확인합니다.
        commentCollectionRef.orderBy("c_idx", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Board 컬렉션에 문서가 하나 이상 있는 경우
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            long currentDocumentId = lastDocument.getLong("c_idx");

                            // 현재 문서 ID에 1을 더하여 다음 문서의 ID를 설정합니다.
                            long nextDocumentId = currentDocumentId + 1;

                            // 나머지 저장 로직 구현
                            addCommentToBoard(boardId, nextDocumentId, subject);
                        } else {
                            // Board 컬렉션에 문서가 없는 경우, ID를 1로 설정합니다.
                            addCommentToBoard(boardId, 1, subject);
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

    public void addCommentToBoard(String boardId, long nextDocumentId, String subject) {
        Chatting chatting = new Chatting();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // "Board" 컬렉션의 "boardId" 문서 참조
        DocumentReference boardRef = db.collection("Chatting").document(boardId);

        // "Comment" 서브컬렉션 참조
        CollectionReference commentCollectionRef = boardRef.collection("Chat");

        // 현재 로그인된 사용자를 가져옵니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Timestamp date = Timestamp.now();

        String content = etACCWriteComment.getText().toString(); // 내용 가져오기

        String photo = "https://firebasestorage.googleapis.com/v0/b/newpts-26161.appspot.com/o/profile.png?alt=media&token=b1f1cdfd-0932-4603-bc50-70428a419da6";
        chatting.setC_idx((int) nextDocumentId);
        chatting.setC_content(content);
        chatting.setC_user(currentUser != null ? currentUser.getEmail() : ""); // currentUser가 null이 아닌지 확인 후 getEmail 호출
        chatting.setC_date(date);


        // 댓글 추가
        commentCollectionRef
                .document(String.valueOf(nextDocumentId)) // 사용자 정의 문서 식별자 설정
                .set(chatting)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공

                        // EditText 초기화
                     /*   et_user_name.setText("");
                        et_user_email.setText("");*/

                        etACCWriteComment.setText("");
                        // 댓글 추가 후 RecyclerView를 다시 로드하여 업데이트
                        loadComments(Integer.valueOf(boardId));
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

    public void deleteBoard(int documentId) {
        // -------------------------------------------------------------------------
        DocumentReference boardRef = db.collection("Chatting").document(String.valueOf(documentId));
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("c_delete", true);
        Timestamp date = Timestamp.now();
        updateData.put("c_del_date", date);

        boardRef.update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공
                        Toast.makeText(getApplicationContext(), "게시물을 삭제했습니다", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), TabLayoutActivity.class);
                        intent.putExtra("sendData", "ChattingActivity");

                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 쓰기 실패
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                    }
                });
        // ---------------------------------------------------------------------------------
    }
}