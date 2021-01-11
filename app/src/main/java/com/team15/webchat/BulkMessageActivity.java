package com.team15.webchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.team15.webchat.Adapters.ActiveListAdapter;
import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Adapters.UserListAdapter;
import com.team15.webchat.App.Config;
import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.ActiveUserList;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.ChatPag;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.ChatViewModel;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BulkMessageActivity extends AppCompatActivity {
    ChatViewModel chatViewModel;
    private SessionManager sessionManager;
    private RecyclerView recyclerUserList;
    private Button btnSendAll, btnSend;
    private EditText editTextMessage;
    private NestedScrollView nestedScrollView;
    private ProgressBar activeListProgressBar;
    private UserListAdapter activeListAdapter;
    private String api;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;

    List<ActiveUserList> results = new ArrayList<>();
    ArrayList<String> arrayListUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_message);

        recyclerUserList = findViewById(R.id.recyclerUserList);
        activeListProgressBar = findViewById(R.id.activeListProgressBar);
        btnSendAll = findViewById(R.id.btnSendAll);
        btnSend = findViewById(R.id.btnSend);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        editTextMessage = findViewById(R.id.editTextMessage);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        activeListAdapter = new UserListAdapter(this, results, arrayListUser, new UserListAdapter.userListAdapterListener() {
            @Override
            public void isSelectClick(View v, int position) {
                select(position);
            }
        });
        recyclerUserList.setLayoutManager(linearLayoutManager);
        recyclerUserList.setAdapter(activeListAdapter);

        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        sessionManager = new SessionManager(this);
        HashMap<String, String> userInfo = sessionManager.get_user();
        api = userInfo.get(SessionManager.API_KEY);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {
                        if (!isLoading && !isLastPage) {
                            currentPage += 1;
                            isLoading = true;
                            loadNextPage();
                        }
                    }
                }
            }
        });
        loadFirstPage();
        btnSendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAll();
                sendBulkMessage("1");

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayListUser.isEmpty())
                    Toast.makeText(BulkMessageActivity.this, "No user selected", Toast.LENGTH_SHORT).show();
                else
                    sendBulkMessage("0");
            }
        });
    }

    private void selectAll() {
        arrayListUser.clear();
        for (int i = 0; i < results.size(); i++) {
            String userId = results.get(i).getUserId().toString();
            arrayListUser.add(userId);
        }
        activeListAdapter.notifyDataSetChanged();

    }


    private void select(int position) {
        String userId = results.get(position).getUserId().toString();
        if (!arrayListUser.contains(userId)) {
            arrayListUser.add(userId);
        } else {
            arrayListUser.remove(userId);
        }
        activeListAdapter.notifyDataSetChanged();
    }

    private void sendBulkMessage(String select) {
        String message = editTextMessage.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(this, "message not empty", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONArray jsonArray = new JSONArray(arrayListUser);
        String jsonUserId = jsonArray.toString();

        chatViewModel.bulkMessage("Bearer " + api, message, jsonUserId, select).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                Toast.makeText(BulkMessageActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNextPage() {
        chatViewModel.activeUser("Bearer " + api, Config.APP_ID, String.valueOf(currentPage)).observe(this, new Observer<ActiveUser>() {
            @Override
            public void onChanged(ActiveUser activeUser) {
                if (activeUser != null) {
                    activeListAdapter.removeLoadingFooter();
                    isLoading = false;
                    if (activeUser.getTotal() > 0) {
                        results.addAll(activeUser.getData());
                        activeListAdapter.notifyDataSetChanged();
                    }
                    if (currentPage != TOTAL_PAGES) activeListAdapter.addLoadingFooter();
                    else isLastPage = true;

                    selectAll();
                }
            }
        });
    }

    private void loadFirstPage() {
        activeListProgressBar.setVisibility(View.VISIBLE);
        chatViewModel.activeUser("Bearer " + api, Config.APP_ID, String.valueOf(currentPage)).observe(this, new Observer<ActiveUser>() {
            @Override
            public void onChanged(ActiveUser activeUser) {
                if (activeUser != null) {
                    results.clear();
                    activeListProgressBar.setVisibility(View.INVISIBLE);
                    TOTAL_PAGES = activeUser.getLastPage();
                    if (activeUser.getTotal() > 0) {
                        results.addAll(activeUser.getData());
                        activeListAdapter.notifyDataSetChanged();
                    }
                    if (currentPage < TOTAL_PAGES) activeListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });
    }

    public void imgBack(View view) {
        finish();
    }
}