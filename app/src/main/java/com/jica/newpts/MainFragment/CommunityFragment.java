package com.jica.newpts.MainFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jica.newpts.CommunityFragment.CommunityMainFragment;
import com.jica.newpts.CommunityFragment.CommunityBoard2Fragment;
import com.jica.newpts.CommunityFragment.CommunityBoard3Fragment;
import com.jica.newpts.CommunityFragment.CommunityBoard4Fragment;

import com.jica.newpts.CommunityFragment.CommunitySearchActivity;
import com.jica.newpts.CommunityFragment.CommunityWriteFragment;
import com.jica.newpts.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommunityFragment extends Fragment {
    private String hashtag = "";
    ConstraintLayout clFCTopMenu;
    CommunityMainFragment communityMainFragment;
    CommunityBoard2Fragment communityBoard2Fragment;
    CommunityBoard3Fragment communityBoard3Fragment;
    CommunityBoard4Fragment communityBoard4Fragment;
    CommunityWriteFragment communityWriteFragment;
    TextView tvFCHotHashtag;
    TextView tvFCHiddenBoardIdx;
    ImageButton ibFCSearch;
    EditText etFCSearchBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        communityMainFragment = new CommunityMainFragment();
        communityBoard2Fragment = new CommunityBoard2Fragment();
        communityBoard3Fragment = new CommunityBoard3Fragment();
        communityBoard4Fragment = new CommunityBoard4Fragment();
        communityWriteFragment = new CommunityWriteFragment();
        tvFCHotHashtag = view.findViewById(R.id.tvFCHotHashtag);
        tvFCHiddenBoardIdx = view.findViewById(R.id.tvFCHiddenBoardIdx);
        ibFCSearch = view.findViewById(R.id.ibFCSearch);
        etFCSearchBar = view.findViewById(R.id.etFCSearchBar);

        getChildFragmentManager().beginTransaction().replace(R.id.child_container_community, communityMainFragment).commit();

        TabLayout tabs = view.findViewById(R.id.tabs_community);

        clFCTopMenu = view.findViewById(R.id.clFCTopMenu);
        findHotHashtag();
        tabs.addTab(tabs.newTab().setText("Main"));
        tabs.addTab(tabs.newTab().setText("나의화분"));
        tabs.addTab(tabs.newTab().setText("척척석사"));
        tabs.addTab(tabs.newTab().setText("자유티앗"));
        tabs.addTab(tabs.newTab().setIcon(R.drawable.write_icon));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selectedFragment = null;
                if (position == 0) {
                    selectedFragment = communityMainFragment;
                } else if (position == 1) {
                    selectedFragment = communityBoard2Fragment;
                } else if (position == 2) {
                    selectedFragment = communityBoard3Fragment;
                } else if (position == 3) {
                    selectedFragment = communityBoard4Fragment;
                } else if (position == 4) {
                    selectedFragment = communityWriteFragment;
                }
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.child_container_community, selectedFragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ibFCSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CommunitySearchActivity.class);
                if (etFCSearchBar.getText().toString().isEmpty() || etFCSearchBar.getText().toString() == null) {
                    Toast.makeText(getContext(), "검색창에 검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                String searchKeyword = etFCSearchBar.getText().toString();
                intent.putExtra("searchKeyword", searchKeyword);
                startActivity(intent);
            }
        });
    }

    public void findHotHashtag() {
        // Firestore 인스턴스를 가져옵니다.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Firestore에서 데이터를 가져옵니다.
        db.collection("Board")
                .whereGreaterThan("f_hashtag", "")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> combinedList = new ArrayList<>();

                        // 결과에서 필요한 필드 값을 가져와서 조합합니다.
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String fHashtag = document.getString("f_hashtag");
                            Long fBoardInfoIdx = document.getLong("f_board_info_idx");
                            String fSubject = document.getString("f_subject");
                            String boardName = "";
                            if (fBoardInfoIdx == 1) {
                                boardName = "<나의화분>";
                            } else if (fBoardInfoIdx == 2) {
                                boardName = "<척척석사>";
                            } else if (fBoardInfoIdx == 3) {
                                boardName = "<자유티앗>";
                            }
                            String fSubjectCut = fSubject;
                            int cutLength = 8;
                            if (fSubjectCut.length() >= cutLength) {
                                fSubjectCut = fSubjectCut.substring(0, cutLength) + "...";
                            }

                            if (fHashtag != null && fBoardInfoIdx != null && fSubject != null) {
                                String combinedValue = fHashtag + "\n" + boardName + fSubjectCut;
                                combinedList.add(combinedValue);
                            }
                        }

                        // 리스트에서 랜덤하게 값을 선택합니다.
                        if (!combinedList.isEmpty()) {
                            Random random = new Random();
                            int randomIndex = random.nextInt(combinedList.size());
                            String randomCombinedValue = combinedList.get(randomIndex);

                            // 선택된 값을 tvFCHotHashtag에 설정합니다.
                            tvFCHotHashtag.setText(randomCombinedValue);

                            // 선택된 값으로 원하는 작업을 수행합니다.
                        } else {
                            // 필요한 필드 값이 없는 경우 처리
                        }
                    } else {
                        // 쿼리가 실패한 경우 처리
                    }
                });
    }
}