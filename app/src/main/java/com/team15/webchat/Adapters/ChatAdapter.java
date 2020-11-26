package com.team15.webchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team15.webchat.ImageViewActivity;
import com.team15.webchat.Model.ActiveUserList;
import com.team15.webchat.Model.Chat;
import com.team15.webchat.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String userId;
    private List<Chat> chatList = new ArrayList<>();
    ;
    private static final int LOADING = 0;
    public static final int MSG_TYPE_LEFT = 1;
    public static final int MSG_TYPE_RIGHT = 2;
    public static final int IMG_TYPE_RIGHT = 3;
    public static final int IMG_TYPE_LEFT = 4;
    public static final int BOT_MSG_LEFT = 5;

    private boolean isLoadingAdded = false;

    public ChatAdapter(Context context, List<Chat> chatList, String userId) {
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
            case IMG_TYPE_RIGHT:
                View imgRight = inflater.inflate(R.layout.image_item_right, parent, false);
                viewHolder = new ChatAdapter.RightImageViewHolder(imgRight);
                break;
            case IMG_TYPE_LEFT:
                View imgLeft = inflater.inflate(R.layout.image_item_left, parent, false);
                viewHolder = new ChatAdapter.LeftImageViewHolder(imgLeft);
                break;
            case BOT_MSG_LEFT:
                View defChat = inflater.inflate(R.layout.default_chat_layout, parent, false);
                viewHolder = new ChatAdapter.LeftImageViewHolder(defChat);
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

        final Chat chat = chatList.get(position);
        switch (getItemViewType(position)) {
            case MSG_TYPE_RIGHT:
                final ChatAdapter.RightChatViewHolder rightViewHolder = (ChatAdapter.RightChatViewHolder) holder;
                rightViewHolder.show_message.setText(chat.getMessage());
                rightViewHolder.txt_date.setText(chat.getCreatedAt());

                if (position == 0) {
                    rightViewHolder.txt_seen.setVisibility(View.VISIBLE);
                    if (chat.getSeen() == 0) {
                        rightViewHolder.txt_seen.setText("Seen");
                    } else {
                        rightViewHolder.txt_seen.setText("Sent");
                    }
                } else {
                    rightViewHolder.txt_seen.setVisibility(View.GONE);
                }
                rightViewHolder.show_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rightViewHolder.txt_date.getVisibility() == View.VISIBLE) {
                            rightViewHolder.txt_date.setVisibility(View.GONE);
                        } else {
                            rightViewHolder.txt_date.setVisibility(View.VISIBLE);

                        }
                    }
                });

                break;

            case MSG_TYPE_LEFT:
                final ChatAdapter.LeftChatViewHolder leftViewHolder = (ChatAdapter.LeftChatViewHolder) holder;
                leftViewHolder.show_message.setText(chat.getMessage());
                leftViewHolder.txt_date.setText(chat.getCreatedAt());
                leftViewHolder.show_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (leftViewHolder.txt_date.getVisibility() == View.VISIBLE) {
                            leftViewHolder.txt_date.setVisibility(View.GONE);
                        } else {
                            leftViewHolder.txt_date.setVisibility(View.VISIBLE);

                        }
                    }
                });
                break;

            case IMG_TYPE_RIGHT:
                ChatAdapter.RightImageViewHolder rightImageViewHolder = (ChatAdapter.RightImageViewHolder) holder;
                Glide.with(context)
                        .load(chat.getMessage())
                        .into(rightImageViewHolder.chatImageView);
                rightImageViewHolder.chatImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ImageViewActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("url", chat.getMessage());
                        context.startActivity(intent);
                    }
                });
                break;

            case IMG_TYPE_LEFT:
                ChatAdapter.LeftImageViewHolder leftImageViewHolder = (ChatAdapter.LeftImageViewHolder) holder;
                Glide.with(context)
                        .load(chat.getMessage())
                        .into(leftImageViewHolder.chatImageView);
                leftImageViewHolder.chatImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ImageViewActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("url", chat.getMessage());
                        context.startActivity(intent);
                    }
                });
                break;

            case BOT_MSG_LEFT:
                ChatAdapter.LeftBotChatViewHolder leftBotChatViewHolder = (ChatAdapter.LeftBotChatViewHolder) holder;


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

    public void addLoadingFooter() {
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = chatList.size() - 1;
        Chat result = getItem(position);
        if (result != null) {
            chatList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Chat getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == chatList.size() - 1 && isLoadingAdded) {
            return LOADING;
        } else {
            if (chatList.get(position).getSenderId().equals(userId)) {
                if (chatList.get(position).getType().equals("image")) {
                    return IMG_TYPE_RIGHT;
                }else if (chatList.get(position).getType().equals("text")) {
                    return MSG_TYPE_RIGHT;
                }else {
                    return BOT_MSG_LEFT;
                }
            } else {
                if (chatList.get(position).getType().equals("image")) {
                    return IMG_TYPE_LEFT;
                } else if (chatList.get(position).getType().equals("text")) {
                    return MSG_TYPE_LEFT;
                } else {
                    return BOT_MSG_LEFT;
                }
            }
        }
    }


    public class RightChatViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public TextView txt_seen;
        public TextView txt_date;

        public RightChatViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            txt_date = itemView.findViewById(R.id.txt_date);
        }
    }

    public class LeftChatViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public TextView txt_seen;
        public TextView txt_date;

        public LeftChatViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            txt_date = itemView.findViewById(R.id.txt_date);
        }
    }

    public class RightImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView chatImageView;

        public RightImageViewHolder(View itemView) {
            super(itemView);
            chatImageView = itemView.findViewById(R.id.chatImageView);
        }
    }

    public class LeftImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView chatImageView;

        public LeftImageViewHolder(View itemView) {
            super(itemView);
            chatImageView = itemView.findViewById(R.id.chatImageView);
        }
    }

    public class LeftBotChatViewHolder extends RecyclerView.ViewHolder {
        public ListView listView;
        public TextView txt_date;

        public LeftBotChatViewHolder(View itemView) {
            super(itemView);
            listView = itemView.findViewById(R.id.listView);
            txt_date = itemView.findViewById(R.id.txt_date);
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