package com.jica.newpts.CommunityFragment;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.jica.newpts.R;
import com.jica.newpts.beans.Comment;

import java.util.ArrayList;
import java.util.Date;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewholder> implements OnCommentItemClickListener {

    OnCommentItemClickListener listener;
    private ArrayList<Comment> arrayList = new ArrayList<Comment>();
    private Context context;
    private String writer;
    private static boolean subCommentLayoutOpen = false;

    public CommentAdapter(ArrayList<Comment> arrayList, Context context, String writer) {
        this.arrayList = arrayList;
        this.context = context;
        this.writer = writer;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reply_item, parent, false);
        CommentViewholder holder = new CommentAdapter.CommentViewholder(view, this);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewholder holder, int position) {

        Glide.with(holder.itemView)
                .load(arrayList.get(position).getR_profile_photo())
                .into(holder.ivLRIProfile);
        holder.tvLRIContent.setText(arrayList.get(position).getR_content());// 숫자가 있으면 String.valueOf로 감싸줘야함
        holder.tvLRIUser.setText(arrayList.get(position).getR_user());
        if (arrayList.get(position).getR_user().equals(writer)) {
            holder.tvLRIcheckWritter.setVisibility(View.VISIBLE);
        }
        // Timestamp형으로 저장된 시간을 여기서 쓸수 있게 string형으로 변환
        Timestamp timestamp = arrayList.get(position).getR_date();
        // Timestamp를 Date로 변환
        Date date = timestamp.toDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String f_date = dateFormat.format(date);

        holder.tvLRIDate.setText(f_date);
        // --------------------------------------
        String marginRole = "";
        if (arrayList.get(position).getR_level_idx() >= 1) {
            for (int i = 0; i <= arrayList.get(position).getR_level_idx() - 1; i++) {
                marginRole += "1";
            }
        }
        holder.tvLRIMargineText.setText(marginRole);


        // --------------------------------------
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    @Override
    public void OnItemClick(CommentAdapter.CommentViewholder viewHolder, View view, int position) {
        if (listener != null) {
            // 현재 Adapter객체의 멤버변수인 Listener의 OnItemclick()메소드를 작동시킨다
            // 그런데 listener맴버변수는 아래의 setOnItemClickListener()메소드의 인자로 전달된
            // 외부에서 만들어진 익명의 클래스 객체이다
            listener.OnItemClick(viewHolder, view, position);
        }
    }

    public void setOnItemClickListener(OnCommentItemClickListener listener) {
        this.listener = listener;
    }

    static class CommentViewholder extends RecyclerView.ViewHolder {
        ImageView ivLRIProfile;
        TextView tvLRIUser;
        TextView tvLRIContent;
        TextView tvLRIDate;
        Button tvLRICommentWrite;
        EditText btnLRIReCommentWrite;
        Button btnLRIReCommentRegister;
        ConstraintLayout btnLRIReCommentLayout;
        TextView tvLRIMargineText;
        TextView tvLRIcheckWritter;


        public CommentViewholder(@NonNull View itemView, final OnCommentItemClickListener listener) {
            super(itemView);
            this.ivLRIProfile = itemView.findViewById(R.id.ivLRIProfile);
            this.tvLRIUser = itemView.findViewById(R.id.tvLRIUser);
            this.tvLRIContent = itemView.findViewById(R.id.tvLRIContent);
            this.tvLRIDate = itemView.findViewById(R.id.tvLRIDate);
            this.tvLRICommentWrite = itemView.findViewById(R.id.tvLRICommentWrite);
            this.btnLRIReCommentWrite = itemView.findViewById(R.id.btnLRIReCommentWrite);
            this.btnLRIReCommentRegister = itemView.findViewById(R.id.btnLRIReCommentRegister);
            this.btnLRIReCommentLayout = itemView.findViewById(R.id.btnLRIReCommentLayout);
            this.tvLRIMargineText = itemView.findViewById(R.id.tvLRIMargineText);
            this.tvLRIcheckWritter = itemView.findViewById(R.id.tvLRIcheckWritter);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    // 이 메소드가 작동하는 기준은 ViewHolder가 작동하는 순간이고 그때 listener를 넘겨줌
                    if (listener != null) {
                        // 첫번째 인자 : 자기자신(ViewHolder
                        // 두번째 : 클릭한 뷰
                        // 마지막 : 위에서 구한 position
                        // listener는 현재 객체를 의미한다
                        // 그러므로 아래의 코드는 현재 Adapter객체의 OnItemClick()메소드를 호출한다
                        listener.OnItemClick(CommentViewholder.this, view, position);
                    }
                }
            });

            // tvLRICommentWrite 버튼 클릭 시 서브 댓글 레이아웃을 토글합니다.
            tvLRICommentWrite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        // 레이아웃을 열거나 닫는 작업을 여기에서 수행합니다.
                        if (!subCommentLayoutOpen) {
                            btnLRIReCommentLayout.setVisibility(View.GONE);
                            subCommentLayoutOpen = true;
                        } else {
                            btnLRIReCommentLayout.setVisibility(View.VISIBLE);
                            subCommentLayoutOpen = false;
                        }

                        // 이벤트 리스너를 통해 클릭 이벤트를 전달합니다.
                        listener.OnItemClick(CommentViewholder.this, view, position);
                    }
                }
            });
        }


    }


    public void setItems(ArrayList<Comment> arrayList) {
        this.arrayList = arrayList;
    }

    public void addItem(Comment item) {
        arrayList.add(item);
    }

    public Comment getItem(int position) {
        return arrayList.get(position);
    }

    public void setItem(int position, Comment item) {
        arrayList.set(position, item);
    }

}