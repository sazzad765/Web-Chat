package com.team15.webchat.Fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.team15.webchat.Adapters.ProductListAdapter;
import com.team15.webchat.ChangePasswordActivity;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.ProductList;
import com.team15.webchat.Model.User;
import com.team15.webchat.R;
import com.team15.webchat.ReferralPointActivity;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.UpdateProfileActivity;
import com.team15.webchat.UserPurchaseActivity;
import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.ChatViewModel;
import com.team15.webchat.ViewModel.UserViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ProfileUserFragment extends Fragment {
    private RecyclerView recyclerProduct;
    private NestedScrollView nestedScrollView;
    private TextView txtName, txtName1, txtPhone, txtPoint, txtPurchase, txtRef, txtId;
    private ImageView edit_image;
    private TextView txtSellerContact, txtSellerFb, txtSellerMail,txtChangePass;
    private TextView txtPenPoint,txtRefPoint;
    private ImageView profile_image,imageCopy;
    private Button btnRef;
    private EditText editRef;
    private View layoutIsRef, layoutNoRef,cardPurchase,layoutRef;
    UserViewModel userViewModel;
    private AppViewModel appViewModel;
    private ChatViewModel chatViewModel;
    private SessionManager sessionManager;

    List<ProductList.Product> product = new ArrayList<>();
    ProductListAdapter productListAdapter;

    String userId, api,agentId;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_profile_user, container, false);
        init(view);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        userId = userInfo.get(SessionManager.USER_ID);
        api = userInfo.get(SessionManager.API_KEY);
        agentId= userInfo.get(SessionManager.SELLER_ID);

        userViewModel =  new ViewModelProvider(getActivity()).get(UserViewModel.class);
        appViewModel = new ViewModelProvider(getActivity()).get(AppViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        edit_image.setOnClickListener(new View.OnClickListener() {
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
        cardPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserPurchaseActivity.class));
            }
        });
        layoutRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ReferralPointActivity.class));
            }
        });

        imageCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Ref Id", txtId.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Saved to clip board", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        productListAdapter = new ProductListAdapter(getActivity(), product, new ProductListAdapter.ProductListAdapterListener() {
            @Override
            public void requestOnClick(View v, int position) {
                purchaseDialog(product.get(position).getId().toString());
            }

            @Override
            public void messageOnClick(View v, int position) {
                String message = "Hi i want to buy \npackage: " + product.get(position).getProductName() + "\n" + "Diamond: "
                        + product.get(position).getQuantity() + " => price: " + product.get(position).getPrice();
                sendMessage(message, agentId, "text",message);
            }
        });
        recyclerProduct.setLayoutManager(linearLayoutManager);
        recyclerProduct.setAdapter(productListAdapter);

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

        setProfile();
        getSellerContact();
        loadFirstPage();
        return view;
    }

    private void setProfile() {
        userViewModel.getUser("Bearer " + api, userId).observe(getActivity(), new Observer<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(User user) {
                if (user!=null) {

                    txtName.setText(user.getName());
                    txtName1.setText(user.getName());
                    txtPhone.setText(user.getPhone());
                    txtPoint.setText(user.getPoint().toString());
                    txtPurchase.setText(user.getPurchase().toString());
                    txtId.setText(user.getId().toString());
                    txtPenPoint.setText(user.getPendingPoint().toString());
                    txtRefPoint.setText(user.getReferralPoint().toString());
                    Glide
                            .with(getActivity())
                            .load(user.getImage())
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(profile_image);

                    if (!user.getRef().equals("0")) {
                        layoutIsRef.setVisibility(View.VISIBLE);
                        layoutNoRef.setVisibility(View.GONE);
                        txtRef.setText(user.getRef());
                    } else {
                        layoutNoRef.setVisibility(View.VISIBLE);
                        layoutIsRef.setVisibility(View.GONE);
                    }
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

    public void sendMessage(final String message, final String receiverId, final String type, String body) {

        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
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

    private void purchaseDialog(final String productId) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.purchase_request_dialog, null);

        final EditText editGameId = (EditText) dialogView.findViewById(R.id.editGameId);
        Button buttonSubmit = (Button) dialogView.findViewById(R.id.btnReq);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btnCancel);


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gameId = editGameId.getText().toString();
                if (gameId.length() <= 0) {
                    editGameId.requestFocus();
                    editGameId.setError("enter note");
                    return;
                }
                purchaseRequest(productId, gameId);
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void purchaseRequest(String productId, String gameId) {
        appViewModel.sellRequest("Bearer " + api, userId, productId, gameId).observe(getActivity(), new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                if (apiResponse != null) {
                    showDialog(apiResponse.getMessage());
                }
            }
        });
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                .setTitle("Purchase")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void updateRef() {
        final String refId = editRef.getText().toString();
        if (refId.length() == 0) {
            editRef.requestFocus();
            editRef.setError("FIELD CANNOT BE EMPTY");
            return;
        }

        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                .setTitle("Reference")
                .setMessage("Are you sure your reference id " + refId + " ?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        userViewModel.updateRef("Bearer " + api, userId, refId).observe(getActivity(), new Observer<ApiResponse>() {
                            @Override
                            public void onChanged(ApiResponse apiResponse) {
                                if (apiResponse!= null) {
                                    setProfile();
                                    dialog(apiResponse.getMessage());
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();


    }

    private void loadFirstPage() {
        appViewModel.getProduct("Bearer " + api, "1").observe(getActivity(), new Observer<ProductList.ProductListPaging>() {
            @Override
            public void onChanged(ProductList.ProductListPaging productList) {
                if (productList != null) {
                    product.clear();
                    TOTAL_PAGES = productList.getLastPage();
                    product.addAll(productList.getData());
                    productListAdapter.notifyDataSetChanged();

                    if (currentPage < TOTAL_PAGES) productListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }

            }
        });
    }

    private void loadNextPage() {
        appViewModel.getProduct("Bearer " + api, String.valueOf(currentPage)).observe(getActivity(), new Observer<ProductList.ProductListPaging>() {
            @Override
            public void onChanged(ProductList.ProductListPaging productList) {
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

    @Override
    public void onResume() {
        super.onResume();
    }

    private void dialog(String message) {
        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                .setTitle("Reference")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void init(View view) {
        edit_image = view.findViewById(R.id.edit_image);
        txtName = view.findViewById(R.id.txtName);
        txtName1 = view.findViewById(R.id.txtName1);
        txtPhone = view.findViewById(R.id.txtPhone);
        profile_image = view.findViewById(R.id.profile_image);
        imageCopy = view.findViewById(R.id.imageCopy);
        txtPoint = view.findViewById(R.id.txtPoint);
        txtPurchase = view.findViewById(R.id.txtPurchase);
        txtRef = view.findViewById(R.id.txtRef);
        btnRef = view.findViewById(R.id.btnRef);
        editRef = view.findViewById(R.id.editRef);
        layoutIsRef = view.findViewById(R.id.layoutIsRef);
        layoutNoRef = view.findViewById(R.id.layoutNoRef);
        txtId = view.findViewById(R.id.txtId);
        txtSellerContact = view.findViewById(R.id.txtSellerContact);
        txtSellerFb = view.findViewById(R.id.txtSellerFb);
        txtSellerMail = view.findViewById(R.id.txtSellerMail);
        txtChangePass= view.findViewById(R.id.txtChangePass);
        txtRefPoint = view.findViewById(R.id.txtRefPoint);
        txtPenPoint= view.findViewById(R.id.txtPenPoint);
        cardPurchase = view.findViewById(R.id.cardPurchase);
        layoutRef = view.findViewById(R.id.layoutRef);
        recyclerProduct = view.findViewById(R.id.recyclerProduct);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
    }


}