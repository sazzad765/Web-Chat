package com.team15.webchat.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.R;

import java.util.ArrayList;
import java.util.List;

public class PagerSliderAdapter extends RecyclerView.Adapter<PagerSliderAdapter.SliderViewHolder> {
    private final Context context;
    private List<Banner> banners = new ArrayList<>();
    String sellerId;
    private ViewPager2 viewPager2;

    public PagerSliderAdapter(Context context,List<Banner> banners, ViewPager2 viewPager2 ,String sellerId) {
        this.context = context;
        this.banners = banners;
        this.viewPager2 = viewPager2;
        this.sellerId = sellerId;
    }

    @NonNull
    @Override
    public PagerSliderAdapter.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, parent,false);
        return new PagerSliderAdapter.SliderViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerSliderAdapter.SliderViewHolder holder, int position) {
        Banner sliderItem = banners.get(position);

//        viewHolder.textViewDescription.setText(sliderItem.getTitle());
        holder.textViewDescription.setTextSize(16);
        holder.textViewDescription.setTextColor(Color.WHITE);
        final String url =  sliderItem.getSlider();
        Glide.with(context).load(url).into(holder.imageViewBackground);

        if (position == banners.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(0, true);
            //mData.addAll(mData);
            //notifyDataSetChanged();
        }
    };
}
