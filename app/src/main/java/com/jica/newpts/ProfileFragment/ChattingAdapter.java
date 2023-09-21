package com.jica.newpts.ProfileFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jica.newpts.R;
import com.jica.newpts.beans.Chatting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ChattingViewholder> implements OnChattingItemClickListener {

    OnChattingItemClickListener listener;
    private ArrayList<Chatting> arrayList = new ArrayList<Chatting>();
    private Context context;
    private String writer;

    public ChattingAdapter(ArrayList<Chatting> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.writer = writer;
    }

    @NonNull
    @Override
    public ChattingAdapter.ChattingViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chatting_item, parent, false);
        ChattingAdapter.ChattingViewholder holder = new ChattingAdapter.ChattingViewholder(view, this);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChattingAdapter.ChattingViewholder holder, int position) {

     /*   Glide.with(holder.itemView)
                .load(arrayList.get(position).getR_profile_photo())
                .into(holder.ivLRIProfile);*/
        holder.tvLCIUser.setText(arrayList.get(position).getC_user());
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (!arrayList.get(position).getC_user().equals(currentUser.getEmail())) {
            holder.llLCIBackBoard.setBackgroundResource(R.drawable.drawable_your_talkbox);
            holder.tvLCILeftMargin.setVisibility(View.VISIBLE);
            holder.tvLCIRightMargin.setVisibility(View.GONE);
        }
        // Timestamp형으로 저장된 시간을 여기서 쓸수 있게 string형으로 변환
        Timestamp timestamp = arrayList.get(position).getC_date();
        // Timestamp를 Date로 변환
        Date date = timestamp.toDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String f_date = dateFormat.format(date);
        holder.tvLCIComment.setText(arrayList.get(position).getC_content());
        holder.tvLCIDate.setText(f_date);


    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    @Override
    public void OnItemClick(ChattingAdapter.ChattingViewholder viewHolder, View view, int position) {
        if (listener != null) {
            // 현재 Adapter객체의 멤버변수인 Listener의 OnItemclick()메소드를 작동시킨다
            // 그런데 listener맴버변수는 아래의 setOnItemClickListener()메소드의 인자로 전달된
            // 외부에서 만들어진 익명의 클래스 객체이다
            listener.OnItemClick(viewHolder, view, position);
        }
    }

    public void setOnItemClickListener(OnChattingItemClickListener listener) {
        this.listener = listener;
    }

    static class ChattingViewholder extends RecyclerView.ViewHolder {
        TextView tvLCIUser, tvLCIComment, tvLCIDate, tvLCILeftMargin, tvLCIRightMargin;
        LinearLayout llLCIBackBoard;

        public ChattingViewholder(@NonNull View itemView, final OnChattingItemClickListener listener) {
            super(itemView);
            /*     this.ivLRIProfile = itemView.findViewById(R.id.ivLRIProfile);*/
            this.tvLCIUser = itemView.findViewById(R.id.tvLCIUser);
            this.tvLCIComment = itemView.findViewById(R.id.tvLCIComment);
            this.tvLCIDate = itemView.findViewById(R.id.tvLCIDate);
            this.llLCIBackBoard = itemView.findViewById(R.id.llLCIBackBoard);
            this.tvLCILeftMargin = itemView.findViewById(R.id.tvLCILeftMargin);
            this.tvLCIRightMargin = itemView.findViewById(R.id.tvLCIRightMargin);


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
                        listener.OnItemClick(ChattingAdapter.ChattingViewholder.this, view, position);
                    }
                }
            });


        }


    }


    public void setItems(ArrayList<Chatting> arrayList) {
        this.arrayList = arrayList;
    }

    public void addItem(Chatting item) {
        arrayList.add(item);
    }

    public Chatting getItem(int position) {
        return arrayList.get(position);
    }

    public void setItem(int position, Chatting item) {
        arrayList.set(position, item);
    }

}