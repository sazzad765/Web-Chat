package com.team15.webchat.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.team15.webchat.Adapters.ActiveListAdapter;
import com.team15.webchat.FCM.Config;
import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.ActiveUserList;
import com.team15.webchat.Model.User;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.ChatViewModel;
import com.team15.webchat.ViewModel.UserViewModel;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveListFragment extends Fragment {
    ChatViewModel chatViewModel;
    private SessionManager sessionManager;
    private RecyclerView recyclerActiveUser;
    private ActiveListAdapter activeListAdapter;
    String api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_list, container, false);
        recyclerActiveUser = view.findViewById(R.id.recyclerActiveUser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        activeListAdapter = new ActiveListAdapter(getActivity());
        recyclerActiveUser.setLayoutManager(linearLayoutManager);
        recyclerActiveUser.setAdapter(activeListAdapter);

        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        final String userId = userInfo.get(SessionManager.USER_ID);
        api = userInfo.get(SessionManager.API_KEY);


        loadFirstPage();
        return view;
    }

    private void loadFirstPage() {

        chatViewModel.activeUser("Bearer " + api, Config.APP_ID).observe(getActivity(), new Observer<ActiveUser>() {
            @Override
            public void onChanged(ActiveUser activeUser) {
                List<ActiveUserList> results = activeUser.getData();
                activeListAdapter.addAll(results);

//                if (currentPage <= TOTAL_PAGES) paginationAdapter.addLoadingFooter();
//                else isLastPage = true;
            }
        });

    }

}