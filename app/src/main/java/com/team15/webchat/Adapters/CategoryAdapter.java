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
import com.team15.webchat.Model.Category;
import com.team15.webchat.Model.WaitingList;
import com.team15.webchat.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Category> categories = new ArrayList<>();
    private View.OnClickListener mOnItemClickListener;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());


        View viewItem = inflater.inflate(R.layout.category_layout, parent, false);
        viewHolder = new CategoryAdapter.CategoryViewHolder(viewItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        final Category category = categories.get(position);
        CategoryAdapter.CategoryViewHolder userViewHolder = (CategoryAdapter.CategoryViewHolder) holder;
        userViewHolder.txtName.setText(category.getName());
        Glide.with(context).load(category.getIcon()).apply(RequestOptions.centerCropTransform()).into(userViewHolder.profileImage);
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private ImageView profileImage;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.imageView9);
            txtName = itemView.findViewById(R.id.txtName);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }

}