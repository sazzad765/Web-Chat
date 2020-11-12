package com.team15.webchat.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;

import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.User;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.UpdateProfileActivity;
import com.team15.webchat.UserInfoActivity;
import com.team15.webchat.ViewModel.UserViewModel;

import java.io.File;
import java.security.Principal;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private TextView txt_edit, txtName, txtName1, txtPhone, txtPoint, txtPurchase, txtRef,txtId;
    private ImageView profile_image;
    private Button btnRef;
    private EditText editRef;
    private View layoutIsRef, layoutNoRef, layoutPurchase;
    UserViewModel userViewModel;
    private SessionManager sessionManager;
    String userId, api;

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
        txtPoint = view.findViewById(R.id.txtPoint);
        txtPurchase = view.findViewById(R.id.txtPurchase);
        txtRef = view.findViewById(R.id.txtRef);
        btnRef = view.findViewById(R.id.btnRef);
        editRef = view.findViewById(R.id.editRef);
        layoutIsRef = view.findViewById(R.id.layoutIsRef);
        layoutNoRef = view.findViewById(R.id.layoutNoRef);
        layoutPurchase = view.findViewById(R.id.layoutPurchase);
        txtId = view.findViewById(R.id.txtId);

        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        userId = userInfo.get(SessionManager.USER_ID);
        api = userInfo.get(SessionManager.API_KEY);
        String type = userInfo.get(SessionManager.USER_TYPE);

        assert type != null;
        if (type.equals("seller")) {
            layoutPurchase.setVisibility(View.GONE);
        }

        txt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(intent);
            }
        });
        btnRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRef();
            }
        });
        setProfile();
        return view;
    }

    private void setProfile() {


        userViewModel.getUser("Bearer " + api, userId).observe(getActivity(), new Observer<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(User user) {

                txtName.setText(user.getName());
                txtName1.setText(user.getName());
                txtPhone.setText(user.getPhone());
                txtPoint.setText(user.getPoint().toString());
                txtPurchase.setText(user.getPurchase().toString());
                txtId.setText(user.getId().toString());
                Glide
                        .with(getActivity())
                        .load(user.getImage())
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(profile_image);

                if (user.getRef() != null){
                    layoutIsRef.setVisibility(View.VISIBLE);
                    layoutNoRef.setVisibility(View.GONE);
                    txtRef.setText(user.getRef());
                }else {
                    layoutNoRef.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateRef(){
        final String refId = editRef.getText().toString();
        if (refId == null){
            return;
        }

        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                .setTitle("Reference")
                .setMessage("Are you sure your reference id "+refId+" ?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        userViewModel.updateRef("Bearer " + api, userId,refId).observe(getActivity(), new Observer<ApiResponse>() {
                            @Override
                            public void onChanged(ApiResponse apiResponse) {
                                dialog(apiResponse.getMessage().toString());
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void dialog(String message){
        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                .setTitle("Reference")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.no, null).show();
    }


}