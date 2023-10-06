package com.jica.newpts.CommunityFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.jica.newpts.R;
import com.jica.newpts.TabLayoutActivity;
import com.jica.newpts.TotalLoginActivity;
import com.jica.newpts.beans.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommunityBoardReadFragment extends Fragment {
    private TextView tvFCBRSubject;
    private TextView tvFCBRUser;
    private TextView tvFCBRDate;
    private TextView tvFCBRThumsup;
    private TextView tvFCBRContent;
    private TextView tvFCBRHits;
    TextView tvFCBRWriteComment;
    TextView tvFCBRGreatSave;
    TextView tvFCBRTitle, tvFCBWriterName;
    ImageView ivFCBRThumbsupDummy;
    ImageView ivFCBRNoImg, ivFCBRWriterPhoto;

    private ImageButton ibFCBRThumbsUp;
    private LinearLayout llFCBRHashtag;
    private FirebaseFirestore db;
    private List<String> hashtagItems = new ArrayList<>();

    // RecyclerView 부분
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private ArrayList<Comment> arrayList;
    private ArrayList<Comment> arrayList_test;
    // 이미지 슬라이딩부분
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;

    private ImageSliderAdapter sliderAdapter;
    private ArrayList<String> sliderImageUrls = new ArrayList<>();
    private Button btnFCBRdelete;
    private Button btnFCBRModify;
    boolean isProcessingClick = false; // 클릭 처리 중인지 여부를 나타내는 플래그


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConstraintLayout clFCTopMenu = getActivity().findViewById(R.id.clFCTopMenu);
        if (clFCTopMenu != null) {
            clFCTopMenu.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_board_read, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        tvFCBRSubject = view.findViewById(R.id.tvFCBRSubject);
        tvFCBRUser = view.findViewById(R.id.tvFCBRUser);
        tvFCBRDate = view.findViewById(R.id.tvFCBRDate);
        tvFCBRThumsup = view.findViewById(R.id.tvFCBRThumsup);
        tvFCBRContent = view.findViewById(R.id.tvFCBRContent);
        llFCBRHashtag = view.findViewById(R.id.llFCBRHashtag);
        ibFCBRThumbsUp = view.findViewById(R.id.ibFCBRThumbsUp);
        tvFCBRHits = view.findViewById(R.id.tvFCBRHits);
        tvFCBRWriteComment = view.findViewById(R.id.tvFCBRWriteComment);
        tvFCBRGreatSave = view.findViewById(R.id.tvFCBRGreatSave);
        ivFCBRThumbsupDummy = view.findViewById(R.id.ivFCBRThumbsupDummy);
        sliderViewPager = view.findViewById(R.id.sliderViewPager);
        layoutIndicator = view.findViewById(R.id.layoutIndicators);
        ivFCBRNoImg = view.findViewById(R.id.ivFCBRNoImg);
        tvFCBRTitle = view.findViewById(R.id.tvFCBRTitle);
        btnFCBRdelete = view.findViewById(R.id.btnFCBRdelete);
        btnFCBRModify = view.findViewById(R.id.btnFCBRModify);
        tvFCBWriterName = view.findViewById(R.id.tvFCBWriterName);
        ivFCBRWriterPhoto = view.findViewById(R.id.ivFCBRWriterPhoto);

        sliderViewPager.setOffscreenPageLimit(1);


        if (bundle != null) {
            int f_board_idx = bundle.getInt("f_board_idx");
            String f_subject = bundle.getString("f_subject");
            String f_user = bundle.getString("f_user");
            String f_date = bundle.getString("f_date");
            int f_thumbs_up = bundle.getInt("f_thumbs_up");
            int hits = bundle.getInt("hits");
            String f_hashtag = bundle.getString("f_hashtag");
            String f_content = bundle.getString("f_content");
            String f_photo = bundle.getString("f_photo"); // 이미지는 뒤에서 세팅
            int f_board_info_idx = bundle.getInt("f_board_indo_idx");
            String f_writer_photo = bundle.getString("f_writer_photo");
            String f_writer_name = bundle.getString("f_writer_name");

            String f_board_name = "";
            switch (f_board_info_idx) {
                case 1:
                    f_board_name = "<나의화분>";
                    break;
                case 2:
                    f_board_name = "<척척석사>";
                    break;
                case 3:
                    f_board_name = "<자유티앗>";
                    break;
                default:
                    f_board_name = "";
                    break;
            }
            tvFCBRSubject.setText(f_subject);
            tvFCBRUser.setText(f_user);
            tvFCBRDate.setText(f_date);
            tvFCBRThumsup.setText("좋아요 " + String.valueOf(f_thumbs_up) + "개");
            tvFCBRContent.setText(f_content);
            tvFCBRGreatSave.setText(String.valueOf(f_thumbs_up));
            tvFCBRHits.setText("조회 " + hits);
            tvFCBRTitle.setText(f_board_name);
            tvFCBWriterName.setText(f_writer_name);
            Glide.with(requireContext())
                    .load(f_writer_photo)
                    .into(ivFCBRWriterPhoto);
            Toast.makeText(requireContext(), f_content + "", Toast.LENGTH_SHORT).show();
            if (f_hashtag != null && !f_hashtag.isEmpty()) {
                String[] hashTags = f_hashtag.split("#");
                for (int i = 0; i < hashTags.length && i <= 3; i++) {
                    String hashTag = hashTags[i];
                    if (!hashTag.isEmpty()) {
                        hashtagItems.add("#" + hashTag);
                    }
                }
            }
            if (!(f_hashtag != null && !f_hashtag.isEmpty())) {
                llFCBRHashtag.setVisibility(View.GONE);
            } else {
                llFCBRHashtag.setVisibility(View.VISIBLE);
            }

            if (llFCBRHashtag != null) {
                for (String item : hashtagItems) {
                    AppCompatButton button = new AppCompatButton(getActivity());
                    button.setText(item);
                    button.setBackgroundResource(R.drawable.drawable_round_button_20_very_light_green);
                    LinearLayout.LayoutParams buttonStyle = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    buttonStyle.setMargins(16, 5, 16, 5);
                    button.setTextSize(12);
                    button.setLayoutParams(buttonStyle);
                    llFCBRHashtag.addView(button);
                    Log.d("ButtonDebug", "버튼 추가됨: " + item);
                }
            }

            recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            arrayList = new ArrayList<>();
            arrayList_test = new ArrayList<>();
            db = FirebaseFirestore.getInstance();
            loadComments(f_board_idx);
            countComments(f_board_idx);


            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            // 현재 로그인된 사용자를 가져옵니다.
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) {
                ibFCBRThumbsUp.setVisibility(View.GONE);
                ivFCBRThumbsupDummy.setVisibility(View.VISIBLE);
            } else {
                checkThumbsUp(f_board_idx);
            }
            ivFCBRThumbsupDummy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requireLogin();
                }
            });

            adapter = new CommentAdapter(arrayList, getActivity(), f_user);

            recyclerView.setAdapter(adapter);

           /* ibFCBRThumbsUp.setSelected(true); // 이미지 버튼을 미리 눌린 상태로 설정
            ibFCBRThumbsUp.setImageResource(R.drawable.heart_selected); // 선택된 이미지로 변경*/


            ibFCBRThumbsUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 클릭 처리 중인 경우 더 이상 클릭을 방지
                    if (!isProcessingClick) {
                        isProcessingClick = true; // 클릭 처리 중임을 표시

                        // 버튼 비활성화
                        ibFCBRThumbsUp.setEnabled(false);

                        if (ibFCBRThumbsUp.isSelected()) {
                            ibFCBRThumbsUp.setImageResource(R.drawable.heart);
                            setGreatdown(f_board_idx, Integer.valueOf(tvFCBRGreatSave.getText().toString()));
                        } else {
                            ibFCBRThumbsUp.setImageResource(R.drawable.heart_selected);
                            setGreatUp(f_board_idx, Integer.valueOf(tvFCBRGreatSave.getText().toString()));
                        }

                        // 클릭 처리가 완료되면 버튼을 다시 활성화
                        ibFCBRThumbsUp.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ibFCBRThumbsUp.setEnabled(true);
                                isProcessingClick = false; // 클릭 처리 완료
                                ibFCBRThumbsUp.setSelected(!ibFCBRThumbsUp.isSelected());
                            }
                        }, 500); // 클릭 후 0.5초 동안 연타 방지 (원하는 시간으로 조절 가능)
                    }
                }
            });

            adapter.setOnItemClickListener(new OnCommentItemClickListener() {
                @Override
                public void OnItemClick(CommentAdapter.CommentViewholder viewHolder, View view, int position) {
                    if (currentUser != null) {
                        Intent intent = new Intent(getActivity(), CommunityCommentActivity.class);
                        intent.putExtra("f_board_idx", f_board_idx + "");
                        intent.putExtra("f_subject", f_subject);
                        intent.putExtra("f_user", f_user);
                        intent.putExtra("f_content", f_content);
                        intent.putExtra("f_hashtag", f_hashtag);
                        intent.putExtra("f_board_info_idx", f_board_info_idx);

                        startActivity(intent);
                    } else {
                        requireLogin();
                    }
                }

            });
            tvFCBRWriteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentUser != null) {
                        Intent intent = new Intent(getActivity(), CommunityCommentActivity.class);
                        intent.putExtra("f_board_idx", f_board_idx + "");
                        intent.putExtra("f_subject", f_subject);
                        intent.putExtra("f_user", f_user);
                        intent.putExtra("f_content", f_content);
                        intent.putExtra("f_hashtag", f_hashtag);
                        intent.putExtra("f_board_info_idx", f_board_info_idx);

                        startActivity(intent);
                    } else {
                        requireLogin();
                    }
                }
            });

            if (currentUser != null) {
                if (currentUser.getEmail().toString().equals(f_user)) {
                    btnFCBRdelete.setVisibility(View.VISIBLE);
                    btnFCBRModify.setVisibility(View.VISIBLE);
                }
            }
            btnFCBRdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                    builder.setTitle("게시글 삭제");
                    builder.setIcon(android.R.drawable.ic_dialog_alert); //안드로이드에서 제공하는 아이콘 이미지 사용
                    builder.setMessage("게시글을 삭제하시겠습니까?");

                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteBoard(f_board_idx);
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


            // Firebase Storage에서 이미지 URL을 가져오는 메서드 호출
            loadSliderImagesFromFirebaseStorage(f_photo);

            sliderAdapter = new ImageSliderAdapter(getActivity(), sliderImageUrls);
            sliderViewPager.setAdapter(sliderAdapter);

            sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    setCurrentIndicator(position);
                }
            });
            // 이 모양이 아니면 제대로 작동안함
            if (sliderImageUrls.isEmpty() || sliderImageUrls == null) {
                ivFCBRNoImg.setVisibility(View.VISIBLE);
            }

            setupIndicators(sliderImageUrls.size());

            btnFCBRModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                    builder.setTitle("게시글 수정");
                    builder.setIcon(android.R.drawable.ic_dialog_alert); //안드로이드에서 제공하는 아이콘 이미지 사용
                    builder.setMessage("게시글을 수정하시겠습니까?");

                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            CommunityBoardModifyFragment fragment = new CommunityBoardModifyFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("f_board_idx", f_board_idx);
                            bundle.putString("f_subject", f_subject);
                            bundle.putString("f_user", f_user);
                            bundle.putString("f_content", f_content);
                            bundle.putString("f_hashtag", f_hashtag);
                            bundle.putInt("f_board_info_idx", f_board_info_idx);
                            bundle.putStringArrayList("f_photo", sliderImageUrls);
                            fragment.setArguments(bundle);

                            navigateToFragment(fragment);
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
    }


    public void loadComments(int f_board_idx) {
        DocumentReference boardRef = db.collection("Board").document(String.valueOf(f_board_idx));

        boardRef.collection("Comment")
                .orderBy("r_check_level", Query.Direction.ASCENDING)
                /*   .orderBy("r_idx", Query.Direction.ASCENDING)
                   .whereEqualTo("r_level_idx", 0)*/
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comment comment = document.toObject(Comment.class);
                            arrayList.add(comment);
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void countComments(int f_board_idx) {
        DocumentReference boardRef = db.collection("Board").document(String.valueOf(f_board_idx));
        boardRef.collection("Comment")
                .orderBy("r_idx", Query.Direction.ASCENDING)
                /*  .whereEqualTo("r_level_idx", 0)*/
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayList_test.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comment comment = document.toObject(Comment.class);
                            arrayList_test.add(comment);
                        }
                        adapter.notifyDataSetChanged();
                        arrayList_test.size();
                        String writeComment = "댓글이 없습니다. 댓글을 작성해주세요";
                        if (!arrayList_test.isEmpty() || arrayList_test.size() > 0) {
                            writeComment = "현재 댓글이 " + arrayList_test.size() + "개 있습니다";
                        }
                        tvFCBRWriteComment.setText(writeComment);
                        TextView tvLRICommentWrite = recyclerView.findViewById(R.id.tvLRICommentWrite);

                        if (tvLRICommentWrite != null) {
                            tvLRICommentWrite.setClickable(false);
                        }
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void setGreatUp(long documentId, int f_thumbs_up) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // "Board" 컬렉션의 "boardId" 문서 참조
        DocumentReference subBoardRef = db.collection("Board").document(String.valueOf(documentId));

        // "Comment" 서브컬렉션 참조
        CollectionReference commentCollectionRef = subBoardRef.collection("likes");
        // 현재 로그인된 사용자를 가져옵니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Timestamp date = Timestamp.now();

        Map<String, Object> like = new HashMap<>();
        like.put("f_board_idx", documentId);
        like.put("f_user", currentUser.getEmail());
        like.put(currentUser.getUid(), true);
        like.put("likeDate", date);

        // "Board" 컬렉션에 사용자 정의 식별자를 가진 문서 추가
        commentCollectionRef
                .document(currentUser.getUid()) // 사용자 정의 문서 식별자 설정
                .set(like)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공
                        // -------------------------------------------------------------------------
                        DocumentReference boardRef = db.collection("Board").document(String.valueOf(documentId));
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("f_thumbs_up", f_thumbs_up + 1);
                        boardRef.update(updateData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // 쓰기 성공
                                        getTheThumbsUpCount(documentId);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // 쓰기 실패
                                        Log.e("TAG", e.getMessage());
                                    }
                                });
                        // ---------------------------------------------------------------------------------
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 쓰기 실패
                        Log.e("TAG", e.getMessage());
                    }
                });
    }

    private void setGreatdown(long documentId, int f_thumbs_up) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // "Board" 컬렉션의 "boardId" 문서 참조
        DocumentReference subBoardRef = db.collection("Board").document(String.valueOf(documentId));

        // "Comment" 서브컬렉션 참조
        CollectionReference commentCollectionRef = subBoardRef.collection("likes");
        // 현재 로그인된 사용자를 가져옵니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Timestamp date = Timestamp.now();

        Map<String, Object> like = new HashMap<>();
        like.put("f_board_idx", documentId);
        like.put("f_user", currentUser.getEmail());
        like.put(currentUser.getUid(), false);
        like.put("likeDate", date);


        // "Board" 컬렉션에 사용자 정의 식별자를 가진 문서 추가
        commentCollectionRef
                .document(currentUser.getUid()) // 사용자 정의 문서 식별자 설정
                .set(like)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공
                        DocumentReference boardRef = db.collection("Board").document(String.valueOf(documentId));
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("f_thumbs_up", f_thumbs_up - 1);
                        boardRef.update(updateData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // 쓰기 성공
                                        getTheThumbsUpCount(documentId);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // 쓰기 실패
                                        Log.e("TAG", e.getMessage());
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 쓰기 실패
                        Log.e("TAG", e.getMessage());
                    }
                });
    }

    /*  private void getTheThumbsUpCount(long document) {
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
                              Long fThumbsUp = documentSnapshot.getLong("f_thumbs_up");
                              if (fThumbsUp != null) {
                                  // f_thumbs_up 필드가 null이 아닌 경우
                                  // fThumbsUp 변수에 값이 저장됩니다.
                                  // 이 값을 사용하여 필요한 작업을 수행합니다.
                                  tvFCBRGreatSave.setText(String.valueOf(fThumbsUp));
                                  tvFCBRThumsup.setText("좋아요 " + fThumbsUp + "개");

                              }
                          } else {
                              // 문서가 존재하지 않는 경우
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
      }*/
// 실시간 반영
    private void getTheThumbsUpCount(long document) {
        String documentId = String.valueOf(document);
        DocumentReference docRef = db.collection("Board").document(documentId);

        // 실시간 업데이트 리스너 등록
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // 에러 처리
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // 문서가 존재하고 업데이트되었을 때 실행됩니다.
                    Long fThumbsUp = documentSnapshot.getLong("f_thumbs_up");
                    if (fThumbsUp != null) {
                        // f_thumbs_up 필드가 null이 아닌 경우
                        tvFCBRGreatSave.setText(String.valueOf(fThumbsUp));
                        tvFCBRThumsup.setText("좋아요 " + fThumbsUp + "개");
                    }
                } else {
                    // 문서가 존재하지 않거나 삭제된 경우
                }
            }
        });
    }

    private void checkThumbsUp(long documentId) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // 현재 로그인된 사용자를 가져옵니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        DocumentReference boardDocRef = db.collection("Board").document(String.valueOf(documentId));

        // 서브컬렉션 "likes"에 대한 참조 가져오기
        CollectionReference likesCollectionRef = boardDocRef.collection("likes");


        // Firestore 쿼리를 사용하여 특정 값 검색
        Query query = likesCollectionRef.whereEqualTo(currentUser.getUid(), true);

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // 특정 값이 존재함
                            ibFCBRThumbsUp.setSelected(true); // 이미지 버튼을 미리 눌린 상태로 설정
                            ibFCBRThumbsUp.setImageResource(R.drawable.heart_selected); // 선택된 이미지로 변경
                        } else {
                            // 특정 값이 존재하지 않음
                        }
                    } else {
                        // 쿼리 실행 중 에러 발생
                    }
                });
    }

    private void setupIndicators(int count) {
        // 기존 인디케이터 뷰를 모두 제거합니다.
        layoutIndicator.removeAllViews();

        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < count; i++) {
            indicators[i] = new ImageView(getActivity());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_indicator_inactive));
            }
        }
    }


    private void loadSliderImagesFromFirebaseStorage(String f_photo) {
        // Firebase Storage 초기화
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Firebase Storage의 "slider_images/" 경로에 있는 파일 목록을 가져옵니다.
        StorageReference imagesRef = storage.getReference().child(f_photo);
        imagesRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                // 이미지 목록을 순회하면서 이미지의 다운로드 URL을 리스트에 추가
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sliderImageUrls.add(uri.toString());
                            // 이미지 URL을 가져온 후에 인디케이터 설정
                            setupIndicators(sliderImageUrls.size());
                            sliderAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // URL을 가져오지 못했을 때 처리
                            e.printStackTrace();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 이미지 목록을 가져오지 못했을 때 처리
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle != null) {
            int f_board_idx = bundle.getInt("f_board_idx");

            loadComments(f_board_idx);
            countComments(f_board_idx);
        }
    }

    public void deleteBoard(int documentId) {
        // -------------------------------------------------------------------------
        DocumentReference boardRef = db.collection("Board").document(String.valueOf(documentId));
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("f_del", true);
        Timestamp date = Timestamp.now();
        updateData.put("f_del_date", date);

        boardRef.update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공
                        Intent intent = new Intent(getContext(), TabLayoutActivity.class);
                        intent.putExtra("sendData", "CommunityBoardReadFragement");
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 쓰기 실패
                        Log.e("TAG", e.getMessage());
                    }
                });
        // ---------------------------------------------------------------------------------
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.child_container_community, fragment);
        transaction.commit();
    }

    public void requireLogin() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("로그인이 필요합니다");
        builder.setIcon(android.R.drawable.ic_dialog_alert); //안드로이드에서 제공하는 아이콘 이미지 사용
        builder.setMessage("로그인이 필요한 행동입니다\n계속하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(requireContext(), TotalLoginActivity.class);
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

}
