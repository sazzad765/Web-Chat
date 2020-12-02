package com.team15.webchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team15.webchat.ChatActivity;
import com.team15.webchat.Model.ChatList;
import com.team15.webchat.Model.ProductList;
import com.team15.webchat.R;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<ProductList.Product> product;
    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoadingAdded = false;
    public ProductListAdapter.ProductListAdapterListener onClickListener;

    public ProductListAdapter(Context context,  List<ProductList.Product> product,ProductListAdapterListener listener) {
        this.context = context;
        this.product = product;
        this.onClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.product_row, parent, false);
                viewHolder = new ProductListAdapter.ItemViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new ProductListAdapter.LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ProductList.Product product1 = product.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                ProductListAdapter.ItemViewHolder viewHolder = (ProductListAdapter.ItemViewHolder) holder;
                viewHolder.txtProductName.setText(product1.getProductName());
                viewHolder.txtQuantity.setText(product1.getQuantity().toString());
                viewHolder.txtPoint.setText(product1.getPoint().toString());
//                viewHolder.txtPrice.setText(product1.getPrice().toString());
                Glide.with(context)
                        .load(product1.getImage())
                        .apply(RequestOptions.centerCropTransform())
                        .placeholder(R.drawable.purchase)
                        .into(viewHolder.imageProduct);
                viewHolder.btnRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.requestOnClick(v,position);
                    }
                });
                viewHolder.imgMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.messageOnClick(v,position);
                    }
                });
                break;

            case LOADING:
                ProductListAdapter.LoadingViewHolder loadingViewHolder = (ProductListAdapter.LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return product == null ? 0 : product.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == product.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = product.size() - 1;
        ProductList.Product result = getItem(position);

        if (result != null) {
            product.remove(position);
            notifyItemRemoved(position);
        }
    }
    public ProductList.Product getItem(int position) {
        return product.get(position);
    }

    public interface ProductListAdapterListener{
        void requestOnClick(View v, int position);
        void messageOnClick(View v, int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView txtProductName, txtQuantity, txtPrice, txtPoint;
        private ImageView imageProduct,imgMessage;
        private Button btnRequest;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            btnRequest = itemView.findViewById(R.id.btnRequest);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
//            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtPoint = itemView.findViewById(R.id.txtPoint);
            imgMessage = itemView.findViewById(R.id.imgMessage);
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
