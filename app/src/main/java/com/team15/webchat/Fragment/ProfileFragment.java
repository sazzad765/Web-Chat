package com.team15.webchat.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import com.google.gson.JsonObject;
import com.team15.webchat.ChangePasswordActivity;
import com.team15.webchat.Model.ShortProfile;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.UpdateProfileActivity;

import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.UserViewModel;

import java.util.HashMap;


public class ProfileFragment extends Fragment {
    private TextView  txtName, txtName1, txtPhone, txtId,txtPurchaseCount;
    private ImageButton txt_edit;
    private TextView txtSellerContact, txtSellerFb, txtSellerMail,txtChangePass;
    private ImageView profile_image;
    UserViewModel userViewModel;
    private AppViewModel appViewModel;
    private SessionManager sessionManager;
    String userId, api;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        userId = userInfo.get(SessionManager.USER_ID);
        api = userInfo.get(SessionManager.API_KEY);

        userViewModel =  new ViewModelProvider(getActivity()).get(UserViewModel.class);
        appViewModel = new ViewModelProvider(getActivity()).get(AppViewModel.class);

        txt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(intent);
            }
        });

        txtChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userId!=null) {
                    Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                    intent.putExtra("userId",userId);
                    startActivity(intent);
                }
            }
        });
        setProfile();
        getSellerContact();
        return view;
    }

    private void setProfile() {
        userViewModel.getShortProfile("Bearer " + api, userId).observe(getActivity(), new Observer<ShortProfile>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(ShortProfile user) {
                if (user!=null) {
                    txtName.setText(user.getName());
                    txtName1.setText(user.getName());
                    txtPhone.setText(user.getPhone());
                    txtId.setText(user.getId().toString());
                    if (getActivity() == null) {
                        return;
                    }
                    Glide
                            .with(getActivity())
                            .load(user.getImage())
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(profile_image);
                }
            }
        });
    }

    private void getSellerContact() {
        appViewModel.getSellerContact("Bearer " + api).observe(getActivity(), new Observer<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(JsonObject object) {
                if (object != null) {
                    txtSellerContact.setText(object.get("number").toString());
                    txtSellerFb.setText(object.get("facebook").toString());
                    txtSellerMail.setText(object.get("email").toString());
                }

            }
        });
    }

    private void getPurchaseCount() {
        appViewModel.getPurchaseCount("Bearer " + api,userId).observe(getActivity(), new Observer<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(JsonObject object) {
                if (object != null) {
                    txtPurchaseCount.setText(object.get("seller_purchase_count").toString());
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPurchaseCount();
    }

    private void init(View view) {
        txt_edit = view.findViewById(R.id.txt_edit);
        txtName = view.findViewById(R.id.txtName);
        txtName1 = view.findViewById(R.id.txtName1);
        txtPhone = view.findViewById(R.id.txtPhone);
        profile_image = view.findViewById(R.id.profile_image);
        txtId = view.findViewById(R.id.txtId);
        txtSellerContact = view.findViewById(R.id.txtSellerContact);
        txtSellerFb = view.findViewById(R.id.txtSellerFb);
        txtSellerMail = view.findViewById(R.id.txtSellerMail);
        txtChangePass= view.findViewById(R.id.txtChangePass);
        txtPurchaseCount= view.findViewById(R.id.txtPurchaseCount);
    }


}