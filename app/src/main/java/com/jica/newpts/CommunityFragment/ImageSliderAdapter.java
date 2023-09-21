package com.jica.newpts.CommunityFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jica.newpts.R;

import java.util.ArrayList;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<String> sliderImageUrls; // Firebase Storage에서 가져온 이미지 URL을 저장하는 리스트

    public ImageSliderAdapter(Context context, ArrayList<String> sliderImageUrls) {
        this.context = context;
        this.sliderImageUrls = sliderImageUrls;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindSliderImage(sliderImageUrls.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderImageUrls.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageSlider);
        }

        public void bindSliderImage(String imageURL) {
            // Firebase Storage에서 가져온 이미지 URL을 Glide를 사용하여 표시
            Glide.with(context)
                    .load(imageURL)
                    .into(mImageView);
        }
    }
}
