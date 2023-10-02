package com.jica.newpts.MainFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jica.newpts.CommunityFragment.CommunitySearchActivity;
import com.jica.newpts.ProfileFragment.ChatRommListFragment;
import com.jica.newpts.ProfileFragment.ProfileEditActivity;
import com.jica.newpts.ProfileFragment.SearchDialog;
import com.jica.newpts.R;
import com.jica.newpts.TabLayoutActivity;
import com.jica.newpts.TotalLoginActivity;
import com.jica.newpts.beans.Board;
import com.jica.newpts.beans.ChatRoom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {
    ListView lvFPSelectMenu;
    List<String> MenuData;
    private ArrayList<Board> arrayList = new ArrayList<>();
    // 어뎁터
    ArrayAdapter<String> arrayAdapter;
    TextView tvFPCountMyBoard, tvFPMyName;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    LinearLayout llFPLogoutLayout, llFPMyBoard, llFPLoginWarning, llFPMyWork;
    Button btnFPGotoLogin, btnFPLogout, btnFPEditProfile;
    String menuChoice[] = {"채팅상대 선택", "채팅접속"};
    List<String> selectUser = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        lvFPSelectMenu = view.findViewById(R.id.lvFPSelectMenu);
        tvFPCountMyBoard = view.findViewById(R.id.tvFPCountMyBoard);
        llFPLogoutLayout = view.findViewById(R.id.llFPLogoutLayout);
        btnFPGotoLogin = view.findViewById(R.id.btnFPGotoLogin);
        btnFPLogout = view.findViewById(R.id.btnFPLogout);
        llFPMyBoard = view.findViewById(R.id.llFPMyBoard);
        tvFPMyName = view.findViewById(R.id.tvFPMyName);
        llFPLoginWarning = view.findViewById(R.id.llFPLoginWarning);
        llFPMyWork = view.findViewById(R.id.llFPMyWork);
        btnFPEditProfile = view.findViewById(R.id.btnFPEditProfile);

        // 원본데이터 만들기
        MenuData = (List<String>) Arrays.asList("유저채팅", "전문가 등록/취소", "고객센터", "알림센터", "어플설정", "회원탈퇴");
        // 어뎁터만들기
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, MenuData);
        lvFPSelectMenu.setAdapter(arrayAdapter);

        if (currentUser != null) {
            countMyBoard();
            btnFPGotoLogin.setVisibility(View.GONE);
            llFPLogoutLayout.setVisibility(View.VISIBLE);
            tvFPMyName.setText(currentUser.getEmail());
            llFPLoginWarning.setVisibility(View.GONE);
            llFPMyWork.setVisibility(View.VISIBLE);
        } else {
            btnFPGotoLogin.setVisibility(View.VISIBLE);
            llFPLogoutLayout.setVisibility(View.GONE);
            llFPLoginWarning.setVisibility(View.VISIBLE);
            llFPMyWork.setVisibility(View.GONE);
        }
        btnFPGotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireLogin();
            }
        });
        btnFPLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                builder.setTitle("로그인페이지로 이동");
                builder.setIcon(android.R.drawable.ic_dialog_alert); //안드로이드에서 제공하는 아이콘 이미지 사용
                builder.setMessage("로그인페이지로 이동하시겠습니까??");

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(requireContext(), TotalLoginActivity.class);
                        startActivity(intent);
                    }
                });    //긍정 버튼 - BUTTON_POSITIVE (-1)
                //builder.setNeutralButton("확인", null);   //확인 버튼 - BUTTON_NEUTRAL (-3)
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(requireContext(), TabLayoutActivity.class);
                        startActivity(intent);
                    }
                }); //부정 버튼 -  BUTTON_NEGATIVE (-2)

                //대화상자가 보여진 이후에는 반드시 대화상자의 버튼으로만 대화상자가 종료하도록 한다.
                builder.setCancelable(false);

                //대화상자 만들기
                androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                // 다이얼로그가 나타날 때 배경을 반투명하게 설정


                alertDialog.show(); //대화상자 보이기
            }
        });
        llFPMyBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CommunitySearchActivity.class);
                intent.putExtra("myBoard", currentUser.getEmail());
                startActivity(intent);
            }
        });


        lvFPSelectMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentUser != null) {
                    if (i == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("메뉴를 선택하세요");
                        builder.setIcon(R.drawable.main_logo);

                        builder.setItems(menuChoice, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    findUserList();
                                } else if (which == 1) {
                                    navigateToFragment(new ChatRommListFragment());
                                }
                            }
                        });

                        builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "선택을 취소했습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setCancelable(false);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                } else {
                    Toast.makeText(requireContext(), "채팅기능을 사용하기위해서는\n 로그인을 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnFPEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), ProfileEditActivity.class);
                startActivity(intent);
            }
        });

    }

    public void countMyBoard() {
        db = FirebaseFirestore.getInstance(); // Firestore 초기화
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        db.collection("Board")
                .orderBy("f_board_idx", Query.Direction.DESCENDING)
                .whereEqualTo("f_user", currentUser.getEmail())
                .whereEqualTo("f_del", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Board board = document.toObject(Board.class);
                            arrayList.add(board);
                        }
                        tvFPCountMyBoard.setText(arrayList.size() + "개");

                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void findUserList() {
        // FirebaseAuth 및 FirebaseFirestore 인스턴스 가져오기
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

// 현재 로그인한 사용자의 UID 가져오기
        String currentUserId = firebaseAuth.getCurrentUser().getEmail();

// Firestore 컬렉션 및 필드 설정 (사용자 정보가 저장된 컬렉션 및 필드 이름을 사용하세요)
        String collectionPath = "User"; // 사용자 정보가 저장된 컬렉션 경로
        String fieldPath = "u_id"; // 사용자 이름이 저장된 필드 경로

// Firestore에서 사용자 데이터 가져오기
        firestore.collection(collectionPath)
                .whereNotEqualTo("u_id", currentUserId) // 현재 사용자 제외
                .get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                selectUser.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // 사용자 이름 필드를 가져와서 List에 추가
                                    String userName = document.getString(fieldPath);
                                    if (userName != null) {
                                        selectUser.add(userName);
                                    }
                                }
                                // Firestore 작업이 완료된 후에 SearchDialog를 생성하고 업데이트
                                createAndShowSearchDialog(selectUser);

                                // selectUser 리스트에 사용자 이름 목록이 저장됩니다.
                                // 이제 이 목록을 사용할 수 있습니다.
                            } else {
                                // Firestore에서 사용자 데이터를 가져오는데 실패한 경우
                                Exception exception = task.getException();
                                if (exception != null) {
                                    // 에러 메시지를 출력하거나 로그에 기록할 수 있습니다.
                                    Log.e("FirestoreError", "Firestore 데이터 가져오기 실패: " + exception.getMessage());
                                }
                            }
                        }
                );
    }

    private void createAndShowSearchDialog(List<String> selectUser) {
        // Firestore 작업이 완료된 후에 SearchDialog를 생성하고 업데이트
        SearchDialog selectdialog = new SearchDialog(requireContext(), selectUser);
        selectdialog.show();

        // 다이얼로그가 닫힐 때까지 대기(이부분이 없으면 그냥 null값만 나옴)
        selectdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                String selectedValue = selectdialog.getSelectedValue();
                if (selectedValue != null) {
                    createChatRoomWithNextDocumentId(selectedValue);
                }
            }
        });
    }

    private void createChatRoomWithNextDocumentId(String userId) {
        // "Board" 컬렉션에 대한 참조
        CollectionReference boardCollection = db.collection("Chatting");

        // "Board" 컬렉션을 날짜 필드(f_date)를 기준으로 내림차순으로 정렬하고,
        // 가장 첫 번째 문서를 가져와서 가장 최근 문서의 ID를 확인합니다.
        boardCollection.orderBy("c_idx", Query.Direction.DESCENDING)
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
                            creatChatRoom((int) nextDocumentId, userId);


                        } else {
                            // Board 컬렉션에 문서가 없는 경우, ID를 1로 설정합니다.
                            creatChatRoom(1, userId);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 오류 처리
                        Log.e("TAG", "채팅방 생성실패(문서Id) : " + e.getMessage());
                    }
                });
    }

    private void creatChatRoom(int documentId, String userId) {
        ChatRoom chatRoom = new ChatRoom();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // 현재 로그인된 사용자를 가져옵니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Timestamp date = Timestamp.now();


        chatRoom.setC_idx(documentId);
        chatRoom.setC_send(currentUser.getEmail());
        chatRoom.setC_receive(userId);
        chatRoom.setC_subject(currentUser.getEmail() + "와\n " + userId + "의 채팅");
        chatRoom.setC_date(date);


        // "Board" 컬렉션에 사용자 정의 식별자를 가진 문서 추가
        db.collection("Chatting")
                .document(String.valueOf(documentId)) // 사용자 정의 문서 식별자 설정
                .set(chatRoom)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공

                        Toast.makeText(requireContext(), "채팅방생성성공", Toast.LENGTH_SHORT).show();
                        navigateToFragment(new ChatRommListFragment());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 쓰기 실패
                        Log.e("TAG", "채팅방 생성실패 : " + e.getMessage());
                    }
                });
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
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