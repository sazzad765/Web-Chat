package com.team15.webchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team15.webchat.Model.RefPointTotal;
import com.team15.webchat.Model.ReferralPointList;
import com.team15.webchat.R;

import java.util.List;

public class UserReferralPointTotalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<RefPointTotal.RefPoint> points;
    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    public UserReferralPointTotalAdapter(Context context, List<RefPointTotal.RefPoint> points) {
        this.context = context;
        this.points = points;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.referral_point_sum_row, parent, false);
                viewHolder = new UserReferralPointTotalAdapter.ItemViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new UserReferralPointTotalAdapter.LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        RefPointTotal.RefPoint point = points.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                UserReferralPointTotalAdapter.ItemViewHolder viewHolder = (UserReferralPointTotalAdapter.ItemViewHolder) holder;
                viewHolder.txtUserName.setText(point.getName());
                viewHolder.txtPoint.setText(point.getTotalPoint().toString());
                Glide.with(context).load(point.getImage()).apply(RequestOptions.centerCropTransform()).into(viewHolder.imgProfile);
                break;

            case LOADING:
                UserReferralPointTotalAdapter.LoadingViewHolder loadingViewHolder = (UserReferralPointTotalAdapter.LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return points == null ? 0 : points.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == points.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = points.size() - 1;
        RefPointTotal.RefPoint result = getItem(position);

        if (result != null) {
            points.remove(position);
            notifyItemRemoved(position);
        }
    }

    public RefPointTotal.RefPoint getItem(int position) {
        return points.get(position);
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUserName, txtPoint;
        private ImageView imgProfile;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtPoint = itemView.findViewById(R.id.txtPoint);
            imgProfile = itemView.findViewById(R.id.imgProfile);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);

        }
    }

}
