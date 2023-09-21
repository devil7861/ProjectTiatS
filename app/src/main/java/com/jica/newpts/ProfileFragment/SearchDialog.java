package com.jica.newpts.ProfileFragment;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.jica.newpts.R;

import java.util.ArrayList;
import java.util.List;

public class SearchDialog extends Dialog {
    private Context context;
    private List<String> items;
    private List<String> filteredItems;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private EditText searchEditText;
    private String selectedValue;

    public SearchDialog(@NonNull Context context, List<String> items) {
        super(context);
        this.context = context;
        this.items = items;
        this.filteredItems = new ArrayList<>(items);
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.jica.newpts.R.layout.dialog_search);

        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, filteredItems);
        listView = findViewById(com.jica.newpts.R.id.listView);
        searchEditText = findViewById(R.id.searchEditText);

        listView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 사용자가 항목을 선택할 때 필요한 동작을 수행하세요.
                selectedValue = filteredItems.get(position);
                // 예를 들어, 다이얼로그를 닫거나 선택한 항목을 다른 뷰에 표시할 수 있습니다.
                dismiss();
            }
        });
    }

    public String getSelectedValue() {
        return selectedValue;
    }
    private void filterItems(String query) {
        filteredItems.clear();
        for (String item : items) {
            if (item.toLowerCase().contains(query.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
