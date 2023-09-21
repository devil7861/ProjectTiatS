package com.jica.newpts.CommunityFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.jica.newpts.R;
import com.jica.newpts.TabLayoutActivity;

public class CommunitySearchActivity extends AppCompatActivity {

    SearchContentFragment searchContentFragment;
    SearchSubjectFragment searchSubjectFragment;
    SearchIdFragment searchIdFragment;
    Toolbar toolbar;
    TextView tvACSSearchWord, tvACSSearchWordDummy;
    ImageView backButton;
    EditText etACSSearchBar;
    ImageButton ibACSSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_search);

        Intent intent = getIntent();
        String receivedValue = intent.getStringExtra("searchKeyword");
        String myBoard = intent.getStringExtra("myBoard");
        searchContentFragment = new SearchContentFragment();
        searchSubjectFragment = new SearchSubjectFragment();
        searchIdFragment = new SearchIdFragment();
        tvACSSearchWord = findViewById(R.id.tvACSSearchWord);
        tvACSSearchWordDummy = findViewById(R.id.tvACSSearchWordDummy);
        backButton = findViewById(R.id.backButton);
        etACSSearchBar = findViewById(R.id.etACSSearchBar);
        ibACSSearch = findViewById(R.id.ibACSSearch);
        tvACSSearchWordDummy.setText("검색어 '" + receivedValue + "'에 대한 검색결과 입니다");
        tvACSSearchWord.setText(receivedValue);

        TabLayout tabs = findViewById(R.id.tabs);

        tabs.addTab(tabs.newTab().setText("글 내용"));
        tabs.addTab(tabs.newTab().setText("글 제목"));
        tabs.addTab(tabs.newTab().setText("작성자"));

        String searchWord = tvACSSearchWord.getText().toString();
        if (myBoard != null) {
            tvACSSearchWordDummy.setText("검색어 '" + myBoard + "'에 대한 검색결과 입니다");
            tvACSSearchWord.setText(myBoard);
            getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchIdFragment).commit();

            tabs.getTabAt(2).select();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchContentFragment).commit();
        }


        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selectedFragment = null;
                String valueToPass = searchWord;

                if (position == 0) {
                    selectedFragment = searchContentFragment;
                } else if (position == 1) {
                    selectedFragment = searchSubjectFragment;
                } else if (position == 2) {
                    selectedFragment = searchIdFragment;
                }
                // 선택된 프래그먼트에 값을 전달
                if (selectedFragment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("searchWord", valueToPass);
                    selectedFragment.setArguments(bundle);
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.search_container, selectedFragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼을 클릭했을 때의 동작을 정의합니다.
                // 이 코드에서는 프래그먼트로 돌아가는 예제를 보여줍니다.
                Intent intent = new Intent(getApplicationContext(), TabLayoutActivity.class);
                intent.putExtra("sendData", "CommunitySearchActivity");
                startActivity(intent);
            }
        });
        ibACSSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommunitySearchActivity.class);
                if (etACSSearchBar.getText().toString().isEmpty() || etACSSearchBar.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "검색창에 검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                String searchKeyword = etACSSearchBar.getText().toString();
                intent.putExtra("searchKeyword", searchKeyword);
                startActivity(intent);
            }
        });
    }

    public String getSearchWordText() {
        return tvACSSearchWord.getText().toString();
    }
}