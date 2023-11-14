package com.jica.newpts.CommunityFragment;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jica.newpts.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<Uri> photoUris;
    private TextView selectedImageCountTextView; // 이미지 개수를 나타내는 TextView
    int maxSelectableCount = 5;

    public PhotoAdapter(List<Uri> photoUris, TextView selectedImageCountTextView) {
        this.photoUris = photoUris;
        this.selectedImageCountTextView = selectedImageCountTextView;
    }

    // 이미지 개수를 업데이트하는 메서드
    private void updatePhotoCountText() {
        int photoCount = photoUris.size();
        selectedImageCountTextView.setText(String.valueOf(photoCount) + "/" + maxSelectableCount);

    }


    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.photo_item_layout, parent, false);
        return new PhotoViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        final Uri photoUri = photoUris.get(position);

        // 이미지뷰에 이미지 설정
        holder.photoImageView.setImageURI(photoUri);
        // 수정부분에서 이미지를 휴대폰에 저장하고 사용하는 방식이 아니라 데이터베이스에서 가져오는 방식을 사용하면
        // 기존 이미지를 로드할때 리사이클러뷰에 이미지가 선택은 되지만 보이지가 않는다 그걸 방지하기 위해서 아래의 방식을 사용하면 됨
        // 현재 사용한 방식은 아래의 방식으로 수정을 안해도 잘 작동하므로 일부러 수정을 안함(수정해도 돌아감)
        /*Glide.with(holder.itemView)
                .load(photoUri)
                .into(holder.photoImageView);*/

        // "X" 버튼 클릭 이벤트 처리
        holder.removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선택한 이미지 목록에서 해당 이미지 제거
                int positionToRemove = photoUris.indexOf(photoUri);
                if (positionToRemove != -1) {
                    photoUris.remove(positionToRemove);
                    notifyItemRemoved(positionToRemove);
                    updatePhotoCountText(); // 이미지 개수 업데이트

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoUris.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        ImageView removeImageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            removeImageView = itemView.findViewById(R.id.removeImageView);

        }
    }
}
