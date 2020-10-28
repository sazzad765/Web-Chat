package com.team15.webchat.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.team15.webchat.ChatActivity;
import com.team15.webchat.Model.ChatList;
import com.team15.webchat.R;

import java.util.LinkedList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ChatList> chatList;
    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    public ChatListAdapter(Context context) {
        this.context = context;
        chatList = new LinkedList<>();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.chat_list_row, parent, false);
                viewHolder = new ChatListAdapter.UserViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new ChatListAdapter.LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final ChatList chatList1 = chatList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                ChatListAdapter.UserViewHolder userViewHolder = (ChatListAdapter.UserViewHolder) holder;
                userViewHolder.txtName.setText(chatList1.getName());
//                userViewHolder.txtMessage.setText(chatList1.getName());
                userViewHolder.count.setText(chatList1.getTotalMessage().toString());
                Glide.with(context).load(chatList1.getImage()).apply(RequestOptions.centerCropTransform()).into(userViewHolder.profileImage);
                if (chatList1.getActiveStatus()==1){
                    userViewHolder.active_status.setImageResource(R.color.active);
                }else {
                    userViewHolder.active_status.setImageResource(R.color.inactive);
                }
                userViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("receiverId", chatList1.getUserId().toString());
                        context.startActivity(intent);

                    }
                });
                break;

            case LOADING:
                ChatListAdapter.LoadingViewHolder loadingViewHolder = (ChatListAdapter.LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatList == null ? 0 : chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == chatList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new ChatList());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = chatList.size() - 1;
        ChatList result = getItem(position);

        if (result != null) {
            chatList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void add(ChatList movie) {
        chatList.add(movie);
        notifyItemInserted(chatList.size() - 1);
    }

    public void addAll(List<ChatList> moveResults) {
        for (ChatList result : moveResults) {
            add(result);
        }
    }

    public ChatList getItem(int position) {
        return chatList.get(position);
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName,txtMessage,count;
        private ImageView profileImage,active_status;

        public UserViewHolder(View itemView) {
            super(itemView);
            profileImage =  itemView.findViewById(R.id.profileImage);
            active_status =  itemView.findViewById(R.id.active_status);
            txtName = itemView.findViewById(R.id.name);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            count = itemView.findViewById(R.id.count);
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