package com.team15.webchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team15.webchat.Model.ActiveUserList;
import com.team15.webchat.R;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ActiveUserList> userList = new ArrayList<>();
    ArrayList<String> arrayListUser = new ArrayList<>();
    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    public userListAdapterListener onClickListener;

    public UserListAdapter(Context context, List<ActiveUserList> userList, ArrayList<String> arrayListUser, userListAdapterListener listener) {
        this.context = context;
        this.userList = userList;
        this.onClickListener = listener;
        this.arrayListUser = arrayListUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.user_list_checkbok, parent, false);
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        final ActiveUserList activeUserList = userList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                final UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.txtName.setText(activeUserList.getName());
                Glide.with(context).load(activeUserList.getImage()).apply(RequestOptions.centerCropTransform()).into(userViewHolder.profileImage);
                if (activeUserList.getActiveStatus() == 1) {
                    userViewHolder.active_status.setImageResource(R.color.active);
                } else {
                    userViewHolder.active_status.setImageResource(R.color.inactive);
                }

                userViewHolder.checkBoxparent.setChecked(arrayListUser.contains(activeUserList.getUserId().toString()));

                userViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.isSelectClick(v, position);
                    }
                });
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
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = userList.size() - 1;
        ActiveUserList result = getItem(position);
        if (result != null) {
            userList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ActiveUserList getItem(int position) {
        return userList.get(position);
    }

    public interface userListAdapterListener {
        void isSelectClick(View v, int position);
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private ImageView profileImage, active_status;
        CheckBox checkBoxparent;

        public UserViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            active_status = itemView.findViewById(R.id.active_status);
            txtName = itemView.findViewById(R.id.txtName);
            checkBoxparent = (CheckBox) itemView.findViewById(R.id.chbContent);
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