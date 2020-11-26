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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.PartialsInfo;
import com.team15.webchat.Model.SellerList;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.ChatViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtUserName, txtUserPhone, txtUserPoint, txtUserPurchase, txtPointDe;
    private TextView txtPurchaseIn, txtPurchaseDe;
    private TextView txtPointIn, txtNote;
    private EditText editPoint, editPurchasePoint;
    private Button btnGift, btnPurchasePoint, btnTransfer;
    private ImageView imgFavourite, imgBack;
    private ImageView imgUserProfile;
    private ProgressBar progressBar;
    private AppViewModel appViewModel;
    private SessionManager sessionManager;
    private String id, api;
    ArrayList<String> arraylist ;
    List<SellerList> sellerLists = new ArrayList<>();
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

        arraylist = new ArrayList<String>();
        txtPointDe.setOnClickListener(this);
        txtPointIn.setOnClickListener(this);
        txtNote.setOnClickListener(this);
        btnGift.setOnClickListener(this);
        imgFavourite.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        txtPurchaseIn.setOnClickListener(this);
        txtPurchaseDe.setOnClickListener(this);
        btnPurchasePoint.setOnClickListener(this);
        btnTransfer.setOnClickListener(this);
    }

    private void getData() {
        appViewModel.getPartialsInfo("Bearer " + api, id).observe(this, new Observer<PartialsInfo>() {
            @Override
            public void onChanged(PartialsInfo partialsInfo) {
                if (partialsInfo != null) {
                    txtUserPoint.setText(partialsInfo.getPoint().toString());
                    editPurchasePoint.setText(partialsInfo.getSetPoint().toString());
                    txtUserPurchase.setText(partialsInfo.getPurchase().toString());
                    if (partialsInfo.getNote() != null) {
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

    private void sellerList() {
        progressBar.setVisibility(View.VISIBLE);
        appViewModel.getSellerList("Bearer " + api).observe(this, new Observer<List<SellerList>>() {
            @Override
            public void onChanged(List<SellerList> object) {
                progressBar.setVisibility(View.GONE);
                arraylist.clear();
                sellerLists.clear();
                if (object != null) {
                    sellerLists.addAll(object);
                    for (int i = 0; i < object.size(); i++) {
                        String name = object.get(i).getName();
                        arraylist.add(name);
                    }
                    showSeller();

                }
            }
        });
    }

    private void showSeller() {
        AlertDialog.Builder alertDialog = new
                AlertDialog.Builder(this);
        View rowList = getLayoutInflater().inflate(R.layout.row, null);
        ListView listView = rowList.findViewById(R.id.listView);
        TextView btnAdd = rowList.findViewById(R.id.btnAdd);
        TextView btnClose = rowList.findViewById(R.id.btnClose);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item, arraylist);
        listView.setAdapter(adapter);
        ;
        adapter.notifyDataSetChanged();
        alertDialog.setView(rowList);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                transferUser(sellerLists.get(position).getId());
                progressBar.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });

        btnAdd.setVisibility(View.GONE);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void transferUser(String seller_id){

        appViewModel.transferUser("Bearer " + api,id,seller_id).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                progressBar.setVisibility(View.GONE);
                if (apiResponse!= null){
                    Toast.makeText(UserInfoActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void purchasePoint() {
        final String point = editPurchasePoint.getText().toString();
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("Purchase Point")
                .setMessage("Do you really want to save " + point + " point?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        appViewModel.purchasePoint("Bearer " + api, id, point).observe(UserInfoActivity.this, new Observer<ApiResponse>() {
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
        btnTransfer = findViewById(R.id.btnTransfer);

        txtPurchaseIn = findViewById(R.id.txtPurchaseIn);
        editPurchasePoint = findViewById(R.id.editPurchasePoint);
        txtPurchaseDe = findViewById(R.id.txtPurchaseDe);
        btnPurchasePoint = findViewById(R.id.btnPurchasePoint);
        progressBar = findViewById(R.id.progressBar);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtPointDe:
                int i = Integer.parseInt(editPoint.getText().toString());
                if (i <= 2) {
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

            case R.id.txtPurchaseDe:
                int x = Integer.parseInt(editPurchasePoint.getText().toString());
                if (x <= 2) {
                    return;
                }
                x = x - 1;
                editPurchasePoint.setText(String.valueOf(x));

                break;
            case R.id.txtPurchaseIn:
                int b = Integer.parseInt(editPurchasePoint.getText().toString());
                b = b + 1;
                editPurchasePoint.setText(String.valueOf(b));
                break;
            case R.id.btnPurchasePoint:
                purchasePoint();
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
            case R.id.btnTransfer:
                sellerList();
                break;
            default:
                break;
        }
    }
}