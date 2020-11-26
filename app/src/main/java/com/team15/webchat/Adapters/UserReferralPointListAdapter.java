package com.team15.webchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.team15.webchat.Model.PurchaseList;
import com.team15.webchat.Model.ReferralPointList;
import com.team15.webchat.R;

import java.util.List;

public class UserReferralPointListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<ReferralPointList.ReferralPoint> points;
    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    public UserReferralPointListAdapter(Context context, List<ReferralPointList.ReferralPoint> points) {
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
                View viewItem = inflater.inflate(R.layout.referral_point_row, parent, false);
                viewHolder = new UserReferralPointListAdapter.ItemViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new UserReferralPointListAdapter.LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ReferralPointList.ReferralPoint point = points.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                UserReferralPointListAdapter.ItemViewHolder viewHolder = (UserReferralPointListAdapter.ItemViewHolder) holder;
                viewHolder.txtUserName.setText(point.getName());
                viewHolder.txtPoint.setText(point.getPoint().toString());
                viewHolder.txtDate.setText(point.getCreatedAt());
                break;

            case LOADING:
                UserReferralPointListAdapter.LoadingViewHolder loadingViewHolder = (UserReferralPointListAdapter.LoadingViewHolder) holder;
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
        ReferralPointList.ReferralPoint result = getItem(position);

        if (result != null) {
            points.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ReferralPointList.ReferralPoint getItem(int position) {
        return points.get(position);
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUserName, txtPoint, txtDate;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtPoint = itemView.findViewById(R.id.txtPoint);
            txtDate = itemView.findViewById(R.id.txtDate);
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
