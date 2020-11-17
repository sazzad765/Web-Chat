package com.team15.webchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team15.webchat.Fragment.ChatListFragment;
import com.team15.webchat.Model.WaitingList;
import com.team15.webchat.R;

import java.util.ArrayList;
import java.util.List;

public class WaitingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<WaitingList> waitingLists = new ArrayList<>();
    private View.OnClickListener mOnItemClickListener;

    public WaitingListAdapter(Context context, List<WaitingList> waitingLists) {
        this.context = context;
        this.waitingLists = waitingLists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());


        View viewItem = inflater.inflate(R.layout.waiting_list_layout, parent, false);
        viewHolder = new WaitingListAdapter.WaitingViewHolder(viewItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        final WaitingList waitingList = waitingLists.get(position);
        WaitingListAdapter.WaitingViewHolder userViewHolder = (WaitingListAdapter.WaitingViewHolder) holder;
        userViewHolder.txtName.setText(waitingList.getName());
        Glide.with(context).load(waitingList.getImage()).apply(RequestOptions.centerCropTransform()).into(userViewHolder.profileImage);
        if (waitingList.getActiveStatus() == 1) {
            userViewHolder.active_status.setImageResource(R.color.active);
        } else {
            userViewHolder.active_status.setImageResource(R.color.inactive);
        }
    }

    @Override
    public int getItemCount() {
        return waitingLists == null ? 0 : waitingLists.size();
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class WaitingViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private ImageView profileImage, active_status;

        public WaitingViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            active_status = itemView.findViewById(R.id.active_status);
            txtName = itemView.findViewById(R.id.txtName);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }

}