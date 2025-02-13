package com.team15.webchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.team15.webchat.ChatActivity;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Model.SliderItem;
import com.team15.webchat.R;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {
    private Context context;
    private List<Banner> mSliderItems = new ArrayList<>();
    public SliderAdapterListener onClickListener;

    public SliderAdapter(Context context, SliderAdapterListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
    }

    public void renewItems(List<Banner> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        Banner sliderItem = mSliderItems.get(position);

//        viewHolder.textViewDescription.setText(sliderItem.getTitle());
        viewHolder.textViewDescription.setTextSize(16);
        viewHolder.textViewDescription.setTextColor(Color.WHITE);
        final String url =  sliderItem.getSlider();
        Glide.with(context).load(url).into(viewHolder.imageViewBackground);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.SliderOnClick(v,position);
            }
        });
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    public interface SliderAdapterListener {
        void SliderOnClick(View v, int position);
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder{

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}