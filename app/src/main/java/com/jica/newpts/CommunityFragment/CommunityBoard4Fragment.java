package com.jica.newpts.CommunityFragment;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class CommunityBoard4Fragment extends Fragment {

    private RecyclerView recyclerView;
    private BoardAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Board> arrayList;
    private ArrayList<String> sliderImageUrls = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConstraintLayout clFCTopMenu = (ConstraintLayout) getActivity().findViewById(R.id.clFCTopMenu);
        clFCTopMenu.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_board2, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        db = FirebaseFirestore.getInstance(); // Firestore 초기화

        // 스크롤 내리면 상단바 사라짐
        ConstraintLayout clFCTopMenu = (ConstraintLayout) getActivity().findViewById(R.id.clFCTopMenu);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.canScrollVertically(-1)) {
                    clFCTopMenu.setVisibility(View.GONE);
                } else if (recyclerView.canScrollVertically(20)) {
                    clFCTopMenu.setVisibility(View.VISIBLE);
                }
            }
        });

        loadComment(3);

        adapter = new BoardAdapter(arrayList, getActivity());
        recyclerView.setAdapter(adapter);

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
                bundle.putString("f_writer_photo", item.getF_writer_photo());
                bundle.putString("f_writer_name", item.getF_writer_name());
                fragment.setArguments(bundle);

                navigateToFragment(fragment);
            }
        });
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.child_container_community, fragment);
        transaction.commit();
    }

    private void loadComment(int boardNumber) {
        db.collection("Board")
                .orderBy("f_board_idx", Query.Direction.DESCENDING)
                .whereEqualTo("f_board_info_idx", boardNumber)
                .whereEqualTo("f_del", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Board board = document.toObject(Board.class);
                            arrayList.add(board);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
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
