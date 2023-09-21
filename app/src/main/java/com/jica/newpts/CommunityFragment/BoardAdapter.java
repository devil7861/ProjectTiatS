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


public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewholder> implements OnBoardItemClickListener {

    // 클릭이벤트 처리를 위한 리스터
    OnBoardItemClickListener listener;
    private ArrayList<Board> arrayList = new ArrayList<Board>();
    private Context context;

    public BoardAdapter(ArrayList<Board> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BoardAdapter.BoardViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        BoardViewholder holder = new BoardAdapter.BoardViewholder(view, this);

        return holder;
    }

    // 고쳐야할 부분
    @Override
    public void onBindViewHolder(@NonNull BoardViewholder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getF_down_url())
                .into(holder.ivLIProfile);
        holder.tvLISubject.setText(arrayList.get(position).getF_subject());// 숫자가 있으면 String.valueOf로 감싸줘야함
        holder.tvLIUser.setText(arrayList.get(position).getF_user());
        // Timestamp형으로 저장된 시간을 여기서 쓸수 있게 string형으로 변환
        Timestamp timestamp = arrayList.get(position).getF_date();
        // Timestamp를 Date로 변환
        Date date = timestamp.toDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String f_date = dateFormat.format(date);

        holder.tvLIDate.setText(f_date);
        String countComment = String.valueOf(arrayList.get(position).getF_count_comment());
        holder.tvLICountComment.setText(countComment);
        String boardCount = String.valueOf(arrayList.get(position).getHits());
        holder.tvLIBoardCount.setText("조회수 " + boardCount);
    }

    @Override
    public int getItemCount() {
        // 삼항연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    @Override
    public void OnItemClick(BoardViewholder viewHolder, View view, int position) {
        if (listener != null) {
            // 현재 Adapter객체의 멤버변수인 Listener의 OnItemclick()메소드를 작동시킨다
            // 그런데 listener맴버변수는 아래의 setOnItemClickListener()메소드의 인자로 전달된
            // 외부에서 만들어진 익명의 클래스 객체이다
            listener.OnItemClick(viewHolder, view, position);
        }
    }

    public void setOnItemClickListener(OnBoardItemClickListener listener) {
        this.listener = listener;
    }

    static class BoardViewholder extends RecyclerView.ViewHolder {
        ImageView ivLIProfile;
        TextView tvLISubject;
        TextView tvLIUser;
        TextView tvLIDate;
        TextView tvLICountComment;
        TextView tvLIBoardCount;

        public BoardViewholder(@NonNull View itemView, final OnBoardItemClickListener listener) {
            super(itemView);
            this.ivLIProfile = itemView.findViewById(R.id.ivLIProfile);
            this.tvLISubject = itemView.findViewById(R.id.tvLISubject);
            this.tvLIUser = itemView.findViewById(R.id.tvLIUser);
            this.tvLIDate = itemView.findViewById(R.id.tvLIDate);
            this.tvLICountComment = itemView.findViewById(R.id.tvLICountComment);
            this.tvLIBoardCount = itemView.findViewById(R.id.tvLIBoardCount);
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
                        listener.OnItemClick(BoardViewholder.this, view, position);


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


