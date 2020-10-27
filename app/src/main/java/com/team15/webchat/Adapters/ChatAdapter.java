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

import com.team15.webchat.Model.Chat;
import com.team15.webchat.R;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String userId;
    private List<Chat> chatList = new ArrayList<>();;
    private static final int LOADING = 0;
    public static final int MSG_TYPE_LEFT = 1;
    public static final int MSG_TYPE_RIGHT = 2;
    private boolean isLoadingAdded = false;

    public ChatAdapter(Context context,List<Chat> chatList, String userId) {
        this.context = context;
        this.userId = userId;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case MSG_TYPE_RIGHT:
                View viewItem = inflater.inflate(R.layout.chat_item_right, parent, false);
                viewHolder = new ChatAdapter.RightChatViewHolder(viewItem);
                break;
            case MSG_TYPE_LEFT:
                View viewLeft = inflater.inflate(R.layout.chat_item_left, parent, false);
                viewHolder = new ChatAdapter.LeftChatViewHolder(viewLeft);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new ChatAdapter.LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Chat chat =chatList.get(position);
        switch (getItemViewType(position)) {
            case MSG_TYPE_RIGHT:
                ChatAdapter.RightChatViewHolder rightViewHolder = (ChatAdapter.RightChatViewHolder) holder;
                rightViewHolder.show_message.setText(chat.getMessage());
                break;
            case MSG_TYPE_LEFT:
                ChatAdapter.LeftChatViewHolder leftViewHolder = (ChatAdapter.LeftChatViewHolder) holder;
                leftViewHolder.show_message.setText(chat.getMessage());
                break;
            case LOADING:
                ChatAdapter.LoadingViewHolder loadingViewHolder = (ChatAdapter.LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == chatList.size() - 1 && isLoadingAdded){
            return LOADING;
        }else {
            if (chatList.get(position).getSenderId().equals(userId)) {
                return MSG_TYPE_RIGHT;
            } else {
                return MSG_TYPE_LEFT;
            }
        }
    }


    public class RightChatViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView chatImageView;
        public TextView txt_seen;
        public RightChatViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            txt_seen = itemView.findViewById(R.id.txt_seen);
//            chatImageView = itemView.findViewById(R.id.chatImageView);
        }
    }
    public class LeftChatViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView chatImageView;
        public TextView txt_seen;
        public LeftChatViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            txt_seen = itemView.findViewById(R.id.txt_seen);
//            chatImageView = itemView.findViewById(R.id.chatImageView);
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