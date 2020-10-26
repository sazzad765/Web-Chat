package com.team15.webchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team15.webchat.Model.ActiveUserList;
import com.team15.webchat.R;

import java.util.LinkedList;
import java.util.List;

public class ActiveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ActiveUserList> userList;
    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    public ActiveListAdapter(Context context) {
        this.context = context;
        userList = new LinkedList<>();
    }

    public void setMovieList(List<ActiveUserList> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.active_list_row, parent, false);
                viewHolder = new UserViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ActiveUserList activeUserList = userList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.txtName.setText(activeUserList.getName());
                Glide.with(context).load(activeUserList.getImage()).apply(RequestOptions.centerCropTransform()).into(userViewHolder.profileImage);
                if (activeUserList.getActiveStatus()==0){
                    userViewHolder.active_status.setImageResource(R.color.inactive);
                }else {
                    userViewHolder.active_status.setImageResource(R.color.active);
                }

                break;

            case LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == userList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new ActiveUserList());
    }

//    public void removeLoadingFooter() {
//        isLoadingAdded = false;
//
//        int position = movieList.size() - 1;
//        ActiveUserList result = getItem(position);
//
//        if (result != null) {
//            movieList.remove(position);
//            notifyItemRemoved(position);
//        }
//    }

    public void add(ActiveUserList movie) {
        userList.add(movie);
        notifyItemInserted(userList.size() - 1);
    }

    public void addAll(List<ActiveUserList> moveResults) {
        for (ActiveUserList result : moveResults) {
            add(result);
        }
    }

    public ActiveUserList getItem(int position) {
        return userList.get(position);
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private ImageView profileImage,active_status;

        public UserViewHolder(View itemView) {
            super(itemView);
            profileImage =  itemView.findViewById(R.id.profileImage);
            active_status =  itemView.findViewById(R.id.active_status);
            txtName = itemView.findViewById(R.id.txtName);
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