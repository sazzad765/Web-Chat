package com.team15.webchat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.PartialsInfo;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.ChatViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtUserName, txtUserPhone, txtUserPoint, txtUserPurchase, txtPointDe;
    private TextView txtPointIn, txtNote;
    private EditText editPoint;
    private Button btnGift;
    private ImageView imgFavourite, imgBack;
    private ImageView imgUserProfile;
    private AppViewModel appViewModel;
    private SessionManager sessionManager;
    private String id, api;

    int isFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        init();
        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        String name = extras.getString("name");
        String phone = extras.getString("phone");
        String image = extras.getString("image");
        txtUserName.setText(name);
        txtUserPhone.setText(phone);
        Glide.with(UserInfoActivity.this).load(image).apply(RequestOptions.centerCropTransform()).into(imgUserProfile);

        sessionManager = new SessionManager(this);
        HashMap<String, String> userInfo = sessionManager.get_user();
//        userId = userInfo.get(SessionManager.USER_ID);
        api = userInfo.get(SessionManager.API_KEY);
        getData();

        txtPointDe.setOnClickListener(this);
        txtPointIn.setOnClickListener(this);
        txtNote.setOnClickListener(this);
        btnGift.setOnClickListener(this);
        imgFavourite.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    private void getData() {
        appViewModel.getPartialsInfo("Bearer " + api, id).observe(this, new Observer<PartialsInfo>() {
            @Override
            public void onChanged(PartialsInfo partialsInfo) {
                if (partialsInfo != null) {
                    txtUserPoint.setText(partialsInfo.getPoint().toString());
                    txtUserPurchase.setText(partialsInfo.getPurchase().toString());
                    if (partialsInfo.getNote()!=null){
                        txtNote.setText(partialsInfo.getNote());
                    }
                    isFav = partialsInfo.getFavorite();
                    if (partialsInfo.getFavorite() == 1) {
                        imgFavourite.setImageResource(R.drawable.star_gd);
                    } else {
                        imgFavourite.setImageResource(R.drawable.star);
                    }
                }
            }
        });
    }

    private void isFavourite() {
        if (isFav == 0) {
            imgFavourite.setImageResource(R.drawable.star_gd);
            appViewModel.isFavourite("Bearer " + api, id, "1");
            isFav = 1;
        } else if (isFav == 1) {
            imgFavourite.setImageResource(R.drawable.star);
            appViewModel.isFavourite("Bearer " + api, id, "0");
            isFav = 0;
        }
    }

    private void updateNote() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        final EditText editTextTitle = (EditText) dialogView.findViewById(R.id.editTextTitle);
        Button buttonSubmit = (Button) dialogView.findViewById(R.id.buttonSubmit);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancel);
        TextView txtDialogLabel = dialogView.findViewById(R.id.txtDialogLabel);
        txtDialogLabel.setText("Note");
        editTextTitle.setText(txtNote.getText());


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = editTextTitle.getText().toString();
                appViewModel.updateNote("Bearer " + api, id, note).observe(UserInfoActivity.this, new Observer<ApiResponse>() {
                    @Override
                    public void onChanged(ApiResponse apiResponse) {
                        if (apiResponse != null) {
                            Toast.makeText(UserInfoActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            getData();
                        }
                    }
                });

                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void giftPoint() {
        final String point = editPoint.getText().toString();
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("Gift Point")
                .setMessage("Do you really want to sent " + point + " point?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        appViewModel.updatePoint("Bearer " + api, id, point).observe(UserInfoActivity.this, new Observer<ApiResponse>() {
                            @Override
                            public void onChanged(ApiResponse apiResponse) {
                                if (apiResponse != null) {
                                    Toast.makeText(UserInfoActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    getData();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void init() {
        txtUserName = findViewById(R.id.txtUserName);
        txtUserPhone = findViewById(R.id.txtUserPhone);
        txtUserPoint = findViewById(R.id.txtUserPoint);
        txtUserPurchase = findViewById(R.id.txtUserPurchase);
        txtPointDe = findViewById(R.id.txtPointDe);
        txtPointIn = findViewById(R.id.txtPointIn);
        txtNote = findViewById(R.id.txtNote);
        editPoint = findViewById(R.id.editPoint);
        btnGift = findViewById(R.id.btnGift);
        imgUserProfile = findViewById(R.id.imgUserProfile);
        imgFavourite = findViewById(R.id.imgFavourite);
        imgBack = findViewById(R.id.imgBack);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtPointDe:
                int i = Integer.parseInt(editPoint.getText().toString());
                if(i<=2){
                    return;
                }
                i = i - 1;
                editPoint.setText(String.valueOf(i));

                break;
            case R.id.txtPointIn:
                int a = Integer.parseInt(editPoint.getText().toString());
                a = a + 1;
                editPoint.setText(String.valueOf(a));
                break;
            case R.id.btnGift:
                giftPoint();
                break;
            case R.id.txtNote:
                updateNote();
                break;
            case R.id.imgFavourite:
                isFavourite();
                break;
            case R.id.imgBack:
                finish();
                break;
            default:
                break;
        }
    }
}