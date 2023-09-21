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


public class CommunityMainFragment extends Fragment {
    private RecyclerView recyclerViewNew, recyclerViewLikes, recyclerViewHits, recyclerViewComment;
    private MainPageAdapter adapterNew, adapterLikes, adapterHits, adapterComment;
    private ArrayList<Board> arrayListNew, arrayListLikes, arrayListHits, arrayListComment;
    private ArrayList<String> sliderImageUrls = new ArrayList<>();
    private FirebaseFirestore db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConstraintLayout clFCTopMenu = (ConstraintLayout) getActivity().findViewById(R.id.clFCTopMenu);

        clFCTopMenu.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewNew = view.findViewById(R.id.recyclerViewNew);
        recyclerViewLikes = view.findViewById(R.id.recyclerViewLikes);
        recyclerViewHits = view.findViewById(R.id.recyclerViewHits);
        recyclerViewComment = view.findViewById(R.id.recyclerViewComment);
        recyclerViewNew.setHasFixedSize(true);
        recyclerViewLikes.setHasFixedSize(true);
        recyclerViewHits.setHasFixedSize(true);
        recyclerViewComment.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false); // 수평 스크롤로 변경
        recyclerViewNew.setLayoutManager(layoutManager);
        arrayListNew = new ArrayList<>();
        arrayListLikes = new ArrayList<>();
        arrayListHits = new ArrayList<>();
        arrayListComment = new ArrayList<>();
        db = FirebaseFirestore.getInstance(); // Firestore 초기화

        loadBoardNew();
        loadBoardLikes();
        loadBoardHits();
        loadBoardComment();
        adapterNew = new MainPageAdapter(arrayListNew, getActivity());
        recyclerViewNew.setAdapter(adapterNew);
        adapterLikes = new MainPageAdapter(arrayListLikes, getActivity());
        recyclerViewLikes.setAdapter(adapterLikes);
        adapterHits = new MainPageAdapter(arrayListHits, getActivity());
        recyclerViewHits.setAdapter(adapterHits);
        adapterComment = new MainPageAdapter(arrayListComment, getActivity());
        recyclerViewComment.setAdapter(adapterComment);

        SelectSingleBoard(adapterNew);
        SelectSingleBoard(adapterLikes);
        SelectSingleBoard(adapterHits);
        SelectSingleBoard(adapterComment);
    }


    private void loadBoardNew() {
        db.collection("Board")
                .orderBy("f_date", Query.Direction.DESCENDING)
                .whereEqualTo("f_del", false)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayListNew.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Board board = document.toObject(Board.class);
                            arrayListNew.add(board);
                        }
                        adapterNew.notifyDataSetChanged();
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void loadBoardLikes() {
        db.collection("Board")
                .orderBy("f_thumbs_up", Query.Direction.DESCENDING)
                .whereEqualTo("f_del", false)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayListLikes.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Board board = document.toObject(Board.class);
                            arrayListLikes.add(board);
                        }
                        adapterLikes.notifyDataSetChanged();
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void loadBoardHits() {
        db.collection("Board")
                .orderBy("hits", Query.Direction.DESCENDING)
                .whereEqualTo("f_del", false)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayListHits.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Board board = document.toObject(Board.class);
                            arrayListHits.add(board);
                        }
                        adapterHits.notifyDataSetChanged();
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void loadBoardComment() {
        db.collection("Board")
                .orderBy("f_count_comment", Query.Direction.DESCENDING)
                .whereEqualTo("f_del", false)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayListComment.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Board board = document.toObject(Board.class);
                            arrayListComment.add(board);
                        }
                        adapterComment.notifyDataSetChanged();
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.child_container_community, fragment);
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

    public void SelectSingleBoard(MainPageAdapter adapter) {
        adapter.setOnItemClickListener(new OnMainPageItemClickListener() {
            @Override
            public void OnItemClick(MainPageAdapter.MainPageViewholder viewHolder, View view, int position) {
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

}