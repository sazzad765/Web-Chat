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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team15.webchat.Model.ProductList;
import com.team15.webchat.Model.PurchaseList;
import com.team15.webchat.R;

import java.util.List;

public class SellerPurchaseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<PurchaseList.Purchase> purchase;
    public SellerPurchaseListAdapterListener onClickListener;
    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoadingAdded = false;
    public SellerPurchaseListAdapter(Context context, List<PurchaseList.Purchase> purchase,SellerPurchaseListAdapterListener listener) {
        this.context = context;
        this.purchase = purchase;
        this.onClickListener = listener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.puschase_request_layout, parent, false);
                viewHolder = new SellerPurchaseListAdapter.ItemViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new SellerPurchaseListAdapter.LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        PurchaseList.Purchase product1 = purchase.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                SellerPurchaseListAdapter.ItemViewHolder viewHolder = (SellerPurchaseListAdapter.ItemViewHolder) holder;
                viewHolder.txtProductName.setText(product1.getProductName());
                viewHolder.txtQuantity.setText(product1.getQuantity().toString());
                viewHolder.txtPoint.setText(product1.getPoint().toString());
                viewHolder.txtGameId.setText(product1.getGameId());
                viewHolder.txtStatus.setText(product1.getStatus());
                viewHolder.txtUserName.setText(product1.getName());
                if (product1.getStatus().equals("Pending")){
                    viewHolder.btnAccept.setVisibility(View.VISIBLE);
                    viewHolder.btnCancel.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.btnAccept.setVisibility(View.GONE);
                    viewHolder.btnCancel.setVisibility(View.GONE);
                }
                viewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.cancelOnClick(v,position);
                    }
                });
                viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.acceptOnClick(v,position);
                    }
                });
//                viewHolder.txtPrice.setText(product1.getPrice().toString());
//                Glide.with(context)
//                        .load(product1.getImage())
//                        .apply(RequestOptions.centerCropTransform())
//                        .placeholder(R.drawable.purchase)
//                        .into(viewHolder.imageProduct);
                break;

            case LOADING:
                SellerPurchaseListAdapter.LoadingViewHolder loadingViewHolder = (SellerPurchaseListAdapter.LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return purchase == null ? 0 : purchase.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == purchase.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = purchase.size() - 1;
        PurchaseList.Purchase result = getItem(position);

        if (result != null) {
            purchase.remove(position);
            notifyItemRemoved(position);
        }
    }
    public PurchaseList.Purchase getItem(int position) {
        return purchase.get(position);
    }

    public interface SellerPurchaseListAdapterListener {

        void cancelOnClick(View v, int position);

        void acceptOnClick(View v, int position);
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView txtProductName, txtQuantity, txtPrice, txtPoint,txtUserName,txtGameId,txtStatus;
        private ImageView imageProduct;
        private Button btnAccept,btnCancel;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
//            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtPoint = itemView.findViewById(R.id.txtPoint);

            btnCancel = itemView.findViewById(R.id.btnCancel);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtGameId = itemView.findViewById(R.id.txtGameId);
            txtStatus = itemView.findViewById(R.id.txtStatus);
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
