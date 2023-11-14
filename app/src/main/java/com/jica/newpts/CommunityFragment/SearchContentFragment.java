package com.jica.newpts.CommunityFragment;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jica.newpts.R;
import com.jica.newpts.beans.Board;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SearchContentFragment extends Fragment {
    private RecyclerView recyclerViewBoard1, recyclerViewBoard2, recyclerViewBoard3;
    private BoardAdapter adapterBoard1, adapterBoard2, adapterBoard3;
    private ArrayList<Board> arrayListBoard1, arrayListBoard2, arrayListBoard3;
    private ArrayList<String> sliderImageUrls = new ArrayList<>();
    private FirebaseFirestore db;
    TextView tvFSCBoard1Count, tvFSCBoard2Count, tvFSCBoard3Count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (getActivity() instanceof CommunitySearchActivity) {
            String searchWord = ((CommunitySearchActivity) getActivity()).getSearchWordText();

            tvFSCBoard1Count = view.findViewById(R.id.tvFSCBoard1Count);
            tvFSCBoard2Count = view.findViewById(R.id.tvFSCBoard2Count);
            tvFSCBoard3Count = view.findViewById(R.id.tvFSCBoard3Count);

            recyclerViewBoard1 = view.findViewById(R.id.recyclerBoard1);
            recyclerViewBoard2 = view.findViewById(R.id.recyclerBoard2);
            recyclerViewBoard3 = view.findViewById(R.id.recyclerBoard3);
            recyclerViewBoard1.setHasFixedSize(true);
            recyclerViewBoard2.setHasFixedSize(true);
            recyclerViewBoard3.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()); // 수평 스크롤로 변경
            recyclerViewBoard1.setLayoutManager(layoutManager);
            arrayListBoard1 = new ArrayList<>();
            arrayListBoard2 = new ArrayList<>();
            arrayListBoard3 = new ArrayList<>();
            db = FirebaseFirestore.getInstance(); // Firestore 초기화

            loadBoard1(searchWord);
            loadBoard2(searchWord);
            loadBoard3(searchWord);

            adapterBoard1 = new BoardAdapter(arrayListBoard1, getActivity());
            recyclerViewBoard1.setAdapter(adapterBoard1);
            adapterBoard2 = new BoardAdapter(arrayListBoard2, getActivity());
            recyclerViewBoard2.setAdapter(adapterBoard2);
            adapterBoard3 = new BoardAdapter(arrayListBoard3, getActivity());
            recyclerViewBoard3.setAdapter(adapterBoard3);


            SelectSingleBoard(adapterBoard1);
            SelectSingleBoard(adapterBoard2);
            SelectSingleBoard(adapterBoard3);
        }
    }


    private void loadBoard1(String searchWord) {
        db.collection("Board")
                .orderBy("f_date", Query.Direction.DESCENDING)
                .whereEqualTo("f_board_info_idx", 1)
                .whereEqualTo("f_del", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayListBoard1.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Board board = document.toObject(Board.class);

                            // f_context 값이 searchWord를 포함하는지 확인
                            if (board.getF_context().contains(searchWord)) {
                                arrayListBoard1.add(board);
                            }
                        }
                        if (arrayListBoard1.size() != 0) {
                            tvFSCBoard1Count.setText(String.valueOf(arrayListBoard1.size()));
                        } else {
                            tvFSCBoard1Count.setText("검색결과가 없습니다");

                        }
                        adapterBoard1.notifyDataSetChanged();
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void loadBoard2(String searchWord) {
        db.collection("Board")
                .orderBy("f_date", Query.Direction.DESCENDING)
                .whereEqualTo("f_board_info_idx", 2)
                .whereEqualTo("f_del", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayListBoard2.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Board board = document.toObject(Board.class);

                            // f_context 값이 searchWord를 포함하는지 확인
                            if (board.getF_context().contains(searchWord)) {
                                arrayListBoard2.add(board);

                            }
                        }
                        if (arrayListBoard2.size() != 0) {
                            tvFSCBoard2Count.setText(String.valueOf(arrayListBoard2.size()));
                        } else {
                            tvFSCBoard2Count.setText("검색결과가 없습니다");
                        }
                        adapterBoard2.notifyDataSetChanged();
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void loadBoard3(String searchWord) {
        db.collection("Board")
                .orderBy("f_date", Query.Direction.DESCENDING)
                .whereEqualTo("f_board_info_idx", 3)
                .whereEqualTo("f_del", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayListBoard3.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Board board = document.toObject(Board.class);
                            // f_context 값이 searchWord를 포함하는지 확인
                            if (board.getF_context().contains(searchWord)) {
                                arrayListBoard3.add(board);
                            }
                        }
                        if (arrayListBoard3.size() != 0) {
                            tvFSCBoard3Count.setText(String.valueOf(arrayListBoard3.size()));
                        } else {
                            tvFSCBoard3Count.setText("검색결과가 없습니다");
                        }
                        adapterBoard3.notifyDataSetChanged();
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void SelectSingleBoard(BoardAdapter adapter) {
        adapter.setOnItemClickListener(new OnBoardItemClickListener() {
            @Override
            public void OnItemClick(BoardAdapter.BoardViewholder viewHolder, View view, int position) {
                Board item = adapter.getItem(position);
                CommunityBoardReadFragment fragment = new CommunityBoardReadFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("f_board_idx", item.getF_board_idx());
                bundle.putString("f_subject", item.getF_subject());
                bundle.putString("f_user", item.getF_user());
                bundle.putString("f_photo", item.getF_photo());
                bundle.putInt("f_board_indo_idx", item.getF_board_info_idx());

                Timestamp timestamp = item.getF_date();
                Date date = timestamp.toDate();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd HH:mm");
                String f_date = dateFormat.format(date);

                bundle.putString("f_date", f_date);
                bundle.putInt("f_thumbs_up", item.getF_thumbs_up());
                bundle.putString("f_content", item.getF_context());
                bundle.putString("f_hashtag", item.getF_hashtag());
                bundle.putString("f_content", item.getF_context());
                countReadBoard(item.getF_board_idx(), item.getHits());
                bundle.putInt("hits", item.getHits() + 1);
                fragment.setArguments(bundle);

                navigateToFragment(fragment);
            }
        });
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.search_container, fragment);
        transaction.commit();
    }

    private void countReadBoard(int documentId, int hits) {
        DocumentReference boardRef = db.collection("Board").document(String.valueOf(documentId));
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("hits", hits + 1);
        boardRef.update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 쓰기 성공
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 쓰기 실패
                    }
                });
    }
}