package com.team15.webchat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.team15.webchat.Adapters.CategoryAdapter;
import com.team15.webchat.Adapters.PackageProductListAdapter;
import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Model.Category;
import com.team15.webchat.Model.PackageProduct;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.ChatViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PackageProductActivity extends AppCompatActivity {
    private RecyclerView recyclerProduct;
private TextView empty_view,textView6;
    private ImageView imgBack;
    private AppViewModel appViewModel;
    private ChatViewModel chatViewModel;
    private SessionManager sessionManager;
    List<PackageProduct.Product> product = new ArrayList<>();


    private String agentId, userId, api;

    PackageProductListAdapter productListAdapter;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_product);
        final Category list = (Category) getIntent().getSerializableExtra("data");

        recyclerProduct = findViewById(R.id.recyclerProduct);
        imgBack = findViewById(R.id.imgBack);
        empty_view = findViewById(R.id.empty_view);
        textView6 = findViewById(R.id.textView6);

        textView6.setText(list.getName());

        appViewModel =  new ViewModelProvider(this).get(AppViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        sessionManager = new SessionManager(this);
        HashMap<String, String> userInfo = sessionManager.get_user();
        agentId = userInfo.get(SessionManager.SELLER_ID);
        api = userInfo.get(SessionManager.API_KEY);
        userId = userInfo.get(SessionManager.USER_ID);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        productListAdapter = new PackageProductListAdapter(this, product, new PackageProductListAdapter.ProductListAdapterListener() {
            @Override
            public void requestOnClick(View v, int position) {
                String message = "Hi i want to buy \npackage: " + product.get(position).getProductName() + "\n" + "Diamond: "
                        + product.get(position).getQuantity() + " => price: " + product.get(position).getPrice();
                sendMessage(message, agentId, "text",message);
            }
        });
        recyclerProduct.setLayoutManager(linearLayoutManager);
        recyclerProduct.setAdapter(productListAdapter);

        recyclerProduct.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage(list.getId().toString());
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        loadFirstPage(list.getId().toString());

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void loadFirstPage(String category) {
        empty_view.setVisibility(View.VISIBLE);
        appViewModel.getPackage("Bearer " + api, category,"1").observe(this, new Observer<PackageProduct.ProductListPaging>() {
            @Override
            public void onChanged(PackageProduct.ProductListPaging productList) {
                if (productList != null) {
                    product.clear();
                    TOTAL_PAGES = productList.getLastPage();
                    product.addAll(productList.getData());
                    productListAdapter.notifyDataSetChanged();

                    if (currentPage < TOTAL_PAGES) productListAdapter.addLoadingFooter();
                    else isLastPage = true;

                    if (productList.getTotal()>0){
                        empty_view.setVisibility(View.GONE);
                    }

                }

            }
        });
    }

    private void loadNextPage(String category) {
        appViewModel.getPackage("Bearer " + api,category, String.valueOf(currentPage)).observe(this, new Observer<PackageProduct.ProductListPaging>() {
            @Override
            public void onChanged(PackageProduct.ProductListPaging productList) {
                if (productList != null) {
                    productListAdapter.removeLoadingFooter();
                    isLoading = false;
                    product.addAll(productList.getData());
                    productListAdapter.notifyDataSetChanged();

                    if (currentPage < TOTAL_PAGES) productListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }

            }
        });
    }

    public void sendMessage(final String message, final String receiverId, final String type, String body) {

        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("Message")
                .setMessage(body)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chatViewModel.sendMessage("Bearer " + api, userId, receiverId, message, type, "user");
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();


    }
}