package com.jica.newpts.ProfileFragment;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.jica.newpts.R;
import com.jica.newpts.beans.ChatRoom;

import java.util.ArrayList;
import java.util.Date;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewholder> implements OnChatRoomItemClickListener {

    // 클릭이벤트 처리를 위한 리스터
    OnChatRoomItemClickListener listener;
    private ArrayList<ChatRoom> arrayList = new ArrayList<ChatRoom>();
    private Context context;

    public ChatRoomAdapter(ArrayList<ChatRoom> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatRoomAdapter.ChatRoomViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chatroom_item, parent, false);
        ChatRoomAdapter.ChatRoomViewholder holder = new ChatRoomAdapter.ChatRoomViewholder(view, this);

        return holder;
    }

    // 고쳐야할 부분
    @Override
    public void onBindViewHolder(@NonNull ChatRoomAdapter.ChatRoomViewholder holder, int position) {

        String subject = arrayList.get(position).getC_subject();
        // Timestamp형으로 저장된 시간을 여기서 쓸수 있게 string형으로 변환
        Timestamp timestamp = arrayList.get(position).getC_date();
        // Timestamp를 Date로 변환
        Date date = timestamp.toDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String f_date = dateFormat.format(date);
        holder.tvLCISubject.setText(subject);
        holder.tvLCIDate.setText(f_date);

    }

    @Override
    public int getItemCount() {
        // 삼항연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    @Override
    public void OnItemClick(ChatRoomViewholder viewHolder, View view, int position) {
        if (listener != null) {
            // 현재 Adapter객체의 멤버변수인 Listener의 OnItemclick()메소드를 작동시킨다
            // 그런데 listener맴버변수는 아래의 setOnItemClickListener()메소드의 인자로 전달된
            // 외부에서 만들어진 익명의 클래스 객체이다
            listener.OnItemClick(viewHolder, view, position);
        }
    }

    public void setOnItemClickListener(OnChatRoomItemClickListener listener) {
        this.listener = listener;
    }

    static class ChatRoomViewholder extends RecyclerView.ViewHolder {
        TextView tvLCISubject;
        TextView tvLCIDate;

        public ChatRoomViewholder(@NonNull View itemView, final OnChatRoomItemClickListener listener) {
            super(itemView);
            this.tvLCISubject = itemView.findViewById(R.id.tvLCISubject);
            this.tvLCIDate = itemView.findViewById(R.id.tvLCIDate);

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
                        listener.OnItemClick(ChatRoomAdapter.ChatRoomViewholder.this, view, position);


                    }
                }
            });

        }
    }

    //원본 데이타를 추가/수정/삭제 할 수 있는 메서드----------------
    public void setItems(ArrayList<ChatRoom> arrayList) {
        this.arrayList = arrayList;
    }

    public void addItem(ChatRoom item) {
        arrayList.add(item);
    }

    public ChatRoom getItem(int position) {
        return arrayList.get(position);
    }

    public void setItem(int position, ChatRoom item) {
        arrayList.set(position, item);
    }

}

