package com.jica.newpts.ProfileFragment;

import android.view.View;

import com.jica.newpts.CommunityFragment.BoardAdapter;

public interface OnChattingItemClickListener {
    public void OnItemClick(ChattingAdapter.ChattingViewholder viewHolder, View view, int position);
}
