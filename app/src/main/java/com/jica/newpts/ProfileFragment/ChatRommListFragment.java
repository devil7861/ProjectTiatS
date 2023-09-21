package com.jica.newpts.ProfileFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jica.newpts.R;
import com.jica.newpts.beans.ChatRoom;

import java.util.ArrayList;


public class ChatRommListFragment extends Fragment {
    private RecyclerView recyclerView, recyclerViewReceive;
    private ChatRoomAdapter adapter, adapterReceive;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ChatRoom> arrayList, arrayListReceive;
    private ArrayList<String> sliderImageUrls = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatromm_list, container, false);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerViewReceive = view.findViewById(R.id.recyclerViewReceive);
        recyclerView.setHasFixedSize(true);
        recyclerViewReceive.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        arrayListReceive = new ArrayList<>();
        db = FirebaseFirestore.getInstance(); // Firestore 초기화

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

        loadRoom();
        loadRoomReceive();
        adapter = new ChatRoomAdapter(arrayList, getActivity());
        adapterReceive = new ChatRoomAdapter(arrayListReceive, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerViewReceive.setAdapter(adapterReceive);

        SelectSingleBoard(adapter);
        SelectSingleBoard(adapterReceive);
    }

    private void loadRoom() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // 현재 로그인된 사용자를 가져옵니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getEmail();
        db.collection("Chatting")
                .orderBy("c_idx", Query.Direction.DESCENDING)
                .whereEqualTo("c_send", userId)
                .whereEqualTo("c_delete", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ChatRoom> loadedRooms = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ChatRoom chatRoom = document.toObject(ChatRoom.class);
                            loadedRooms.add(chatRoom);
                        }

                        // 데이터 로드가 완료된 후에 RecyclerView 업데이트
                        arrayList.clear();
                        arrayList.addAll(loadedRooms);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void loadRoomReceive() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // 현재 로그인된 사용자를 가져옵니다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getEmail();
        db.collection("Chatting")
                .orderBy("c_idx", Query.Direction.DESCENDING)
                .whereEqualTo("c_receive", userId)
                .whereEqualTo("c_delete", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ChatRoom> loadedRooms = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ChatRoom chatRoom = document.toObject(ChatRoom.class);
                            loadedRooms.add(chatRoom);
                        }

                        // 데이터 로드가 완료된 후에 RecyclerView 업데이트
                        arrayListReceive.clear();
                        arrayListReceive.addAll(loadedRooms);

                        adapterReceive.notifyDataSetChanged();
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void SelectSingleBoard(ChatRoomAdapter adapter) {
        adapter.setOnItemClickListener(new OnChatRoomItemClickListener() {
            @Override
            public void OnItemClick(ChatRoomAdapter.ChatRoomViewholder viewHolder, View view, int position) {
                ChatRoom item = adapter.getItem(position);
                Intent intent = new Intent(requireContext(), ChattingActivity.class);
                intent.putExtra("c_idx", item.getC_idx() + "");
                intent.putExtra("c_subject", item.getC_subject());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRoom();
    }
}