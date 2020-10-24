package com.team15.webchat.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.Model.User;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.UpdateProfileActivity;
import com.team15.webchat.ViewModel.UserViewModel;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private TextView txt_edit, txtName, txtName1, txtPhone;
    private ImageView profile_image;
    UserViewModel userViewModel;
    private SessionManager sessionManager;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        txt_edit = view.findViewById(R.id.txt_edit);
        txtName = view.findViewById(R.id.txtName);
        txtName1 = view.findViewById(R.id.txtName1);
        txtPhone = view.findViewById(R.id.txtPhone);
        profile_image = view.findViewById(R.id.profile_image);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        final String userId = userInfo.get(SessionManager.USER_ID);
        String api = userInfo.get(SessionManager.API_KEY);

        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);

        userViewModel.getUser("Bearer "+api, userId).observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                txtName.setText(user.getName());
                txtName1.setText(user.getName());
                txtPhone.setText(user.getPhone());
                Glide
                        .with(getActivity())
                        .load(user.getImage())
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(profile_image);
            }
        });
        txt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}