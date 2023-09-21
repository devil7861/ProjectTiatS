package com.jica.newpts.ProfileFragment;

import android.view.View;

import com.jica.newpts.CommunityFragment.BoardAdapter;

public interface OnChatRoomItemClickListener {
    public void OnItemClick(ChatRoomAdapter.ChatRoomViewholder viewHolder, View view, int position);
}
