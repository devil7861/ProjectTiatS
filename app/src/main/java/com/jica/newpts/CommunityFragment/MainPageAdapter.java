package com.jica.newpts.CommunityFragment;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.jica.newpts.R;
import com.jica.newpts.beans.Board;

import java.util.ArrayList;
import java.util.Date;

public class MainPageAdapter extends RecyclerView.Adapter<MainPageAdapter.MainPageViewholder> implements OnMainPageItemClickListener {

    // 클릭이벤트 처리를 위한 리스터
    OnMainPageItemClickListener listener;
    private ArrayList<Board> arrayList = new ArrayList<Board>();
    private Context context;

    public MainPageAdapter(ArrayList<Board> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MainPageAdapter.MainPageViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_main_item, parent, false);
        MainPageViewholder holder = new MainPageAdapter.MainPageViewholder(view, this);

        return holder;
    }

    // 고쳐야할 부분
    @Override
    public void onBindViewHolder(@NonNull MainPageViewholder holder, int position) {

        if (arrayList.get(position).getF_down_url() != null) {
            Glide.with(holder.itemView)
                    .load(arrayList.get(position).getF_down_url())
                    .into(holder.ivLMIthumbnail);
        } else {
            holder.ivLMIthumbnail.setImageResource(R.drawable.noimg);
        }
        String boardName = "";
        switch (arrayList.get(position).getF_board_info_idx()) {
            case 1:
                boardName = "#나의화분";
                break;
            case 2:
                boardName = "#척척석사";
                holder.tvLMIBoardName.setBackgroundResource(R.drawable.drawable_round_rectangle_board_blue);
                break;
            case 3:
                boardName = "#자유티앗";
                holder.tvLMIBoardName.setBackgroundResource(R.drawable.drawable_round_rectangle_board_brown);
                break;
            default:
                boardName = "error";
                break;
        }
        holder.tvLMIBoardName.setText(boardName);// 숫자가 있으면 String.valueOf로 감싸줘야함

        if(arrayList.get(position).getF_subject().length()>=15){
            holder.tvLMISubject.setText(arrayList.get(position).getF_subject().substring(0,13)+"...");
        }else{
            holder.tvLMISubject.setText(arrayList.get(position).getF_subject());
        }




        holder.tvLMIUser.setText(arrayList.get(position).getF_user());

    }

    @Override
    public int getItemCount() {
        // 삼항연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    @Override
    public void OnItemClick(MainPageViewholder viewHolder, View view, int position) {
        if (listener != null) {
            // 현재 Adapter객체의 멤버변수인 Listener의 OnItemclick()메소드를 작동시킨다
            // 그런데 listener맴버변수는 아래의 setOnItemClickListener()메소드의 인자로 전달된
            // 외부에서 만들어진 익명의 클래스 객체이다
            listener.OnItemClick(viewHolder, view, position);
        }
    }

    public void setOnItemClickListener(OnMainPageItemClickListener listener) {
        this.listener = listener;
    }

    static class MainPageViewholder extends RecyclerView.ViewHolder {
        ImageView ivLMIthumbnail;
        TextView tvLMIBoardName;
        TextView tvLMISubject;
        TextView tvLMIUser;

        public MainPageViewholder(@NonNull View itemView, final OnMainPageItemClickListener listener) {
            super(itemView);
            this.ivLMIthumbnail = itemView.findViewById(R.id.ivLMIthumbnail);
            this.tvLMIBoardName = itemView.findViewById(R.id.tvLMIBoardName);
            this.tvLMISubject = itemView.findViewById(R.id.tvLMISubject);
            this.tvLMIUser = itemView.findViewById(R.id.tvLMIUser);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // getAdapterPosition 클릭한 항목의 순서(index)를 알아옴
                    int position = getAdapterPosition();

                    // 이 메소드가 작동하는 기준은 ViewHolder가 작동하는 순간이고 그때 listener를 넘겨줌
                    if (listener != null) {
                        // 첫번째 인자 : 자기자신(ViewHolder
                        // 두번째 : 클릭한 뷰
                        // 마지막 : 위에서 구한 position
                        // listener는 현재 객체를 의미한다
                        // 그러므로 아래의 코드는 현재 Adapter객체의 OnItemClick()메소드를 호출한다
                        listener.OnItemClick(MainPageViewholder.this, view, position);


                    }
                }
            });

        }
    }

    //원본 데이타를 추가/수정/삭제 할 수 있는 메서드----------------
    public void setItems(ArrayList<Board> arrayList) {
        this.arrayList = arrayList;
    }

    public void addItem(Board item) {
        arrayList.add(item);
    }

    public Board getItem(int position) {
        return arrayList.get(position);
    }

    public void setItem(int position, Board item) {
        arrayList.set(position, item);
    }

}
