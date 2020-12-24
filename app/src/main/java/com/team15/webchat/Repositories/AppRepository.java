package com.team15.webchat.Repositories;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.App.Config;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Model.Category;
import com.team15.webchat.Model.PackageProduct;
import com.team15.webchat.Model.PartialsInfo;
import com.team15.webchat.Model.ProductList;
import com.team15.webchat.Model.PurchaseList;
import com.team15.webchat.Model.RefPointTotal;
import com.team15.webchat.Model.ReferralPointList;
import com.team15.webchat.Model.SellerList;
import com.team15.webchat.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository extends Observable {
    private APIInterface apiInterface;
    private static AppRepository appRepository;
    MutableLiveData<List<Banner>> bannerData = new MutableLiveData<>();
    MutableLiveData<ProductList.ProductListPaging> productListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<ReferralPointList.ReferralPointPaging> referralPointPagingMutableLiveData = new MutableLiveData<>();
    MutableLiveData<RefPointTotal.RefPointPaging> refPointPagingMutableLiveData = new MutableLiveData<>();
    MutableLiveData<JsonObject> sellerContactLiveData = new MutableLiveData<>();


    public static AppRepository getInstance() {
        if (appRepository == null) {
            appRepository = new AppRepository();
        }
        return appRepository;
    }

    private AppRepository() {
        apiInterface = APIClient.getRetrofitInstance().create(APIInterface.class);
    }

    public void isFavourite(String token, String user_id, String favourite) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(token);
        list.add(user_id);
        list.add(favourite);

        new AppRepository.IsFavourite(apiInterface).execute(list);
    }


    public LiveData<List<Banner>> getBanner() {

        Call<List<Banner>> call2 = apiInterface.getBanner(Config.APP_ID);
        call2.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                bannerData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {

            }
        });
        return bannerData;
    }

    public LiveData<List<Category>> getCategory(String token) {
        final MutableLiveData<List<Category>> liveData = new MutableLiveData<>();
        Call<List<Category>> call2 = apiInterface.getCategory(token,Config.APP_ID);
        call2.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                liveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
        return liveData;
    }

    public LiveData<ProductList.ProductListPaging> getProduct(String token, String page) {
        Call<ProductList.ProductListPaging> call2 = apiInterface.getProduct(token, Config.APP_ID, page);
        call2.enqueue(new Callback<ProductList.ProductListPaging>() {
            @Override
            public void onResponse(Call<ProductList.ProductListPaging> call, Response<ProductList.ProductListPaging> response) {
                productListMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ProductList.ProductListPaging> call, Throwable t) {

            }
        });
        return productListMutableLiveData;
    }

    public LiveData<PackageProduct.ProductListPaging> getPackage(String token, String category_id, String page) {
        final MutableLiveData<PackageProduct.ProductListPaging> liveData   = new MutableLiveData<>();
        Call<PackageProduct.ProductListPaging> call2 = apiInterface.getPackage(token,category_id, page);
        call2.enqueue(new Callback<PackageProduct.ProductListPaging>() {
            @Override
            public void onResponse(Call<PackageProduct.ProductListPaging> call, Response<PackageProduct.ProductListPaging> response) {
                liveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<PackageProduct.ProductListPaging> call, Throwable t) {

            }
        });
        return liveData;
    }

    public LiveData<PurchaseList.PurchaseListPaging> getSellerSell(String token, String type, String page) {
        final MutableLiveData<PurchaseList.PurchaseListPaging> purchaseListMutableLiveData = new MutableLiveData<>();
        Call<PurchaseList.PurchaseListPaging> call2 = apiInterface.getSellerSell(token, type, Config.APP_ID, page);
        call2.enqueue(new Callback<PurchaseList.PurchaseListPaging>() {
            @Override
            public void onResponse(Call<PurchaseList.PurchaseListPaging> call, Response<PurchaseList.PurchaseListPaging> response) {
                purchaseListMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<PurchaseList.PurchaseListPaging> call, Throwable t) {

            }
        });
        return purchaseListMutableLiveData;
    }

    public LiveData<PurchaseList.PurchaseListPaging> getUserPurchase(String token, String user_id, String page) {
        final MutableLiveData<PurchaseList.PurchaseListPaging> purchaseListMutableLiveData = new MutableLiveData<>();
        Call<PurchaseList.PurchaseListPaging> call2 = apiInterface.getUserPurchase(token, user_id, Config.APP_ID, page);
        call2.enqueue(new Callback<PurchaseList.PurchaseListPaging>() {
            @Override
            public void onResponse(Call<PurchaseList.PurchaseListPaging> call, Response<PurchaseList.PurchaseListPaging> response) {
                purchaseListMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<PurchaseList.PurchaseListPaging> call, Throwable t) {

            }
        });
        return purchaseListMutableLiveData;
    }

    public LiveData<ReferralPointList.ReferralPointPaging> getReferralPointList(String token, String user_id, String page) {
        Call<ReferralPointList.ReferralPointPaging> call2 = apiInterface.getReferralPointList(token, user_id, page);
        call2.enqueue(new Callback<ReferralPointList.ReferralPointPaging>() {
            @Override
            public void onResponse(Call<ReferralPointList.ReferralPointPaging> call, Response<ReferralPointList.ReferralPointPaging> response) {
                referralPointPagingMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ReferralPointList.ReferralPointPaging> call, Throwable t) {

            }
        });
        return referralPointPagingMutableLiveData;
    }

    public LiveData<RefPointTotal.RefPointPaging> getReferralPointTotal(String token, String user_id, String page) {
        Call<RefPointTotal.RefPointPaging> call2 = apiInterface.getReferralPointTotal(token, user_id, page);
        call2.enqueue(new Callback<RefPointTotal.RefPointPaging>() {
            @Override
            public void onResponse(Call<RefPointTotal.RefPointPaging> call, Response<RefPointTotal.RefPointPaging> response) {
                refPointPagingMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<RefPointTotal.RefPointPaging> call, Throwable t) {

            }
        });
        return refPointPagingMutableLiveData;
    }

    public LiveData<User> getSeller(String token, String userId) {
        final MutableLiveData<User> userProfile = new MutableLiveData<>();
        Call<User> call2 = apiInterface.getUser(token, userId);
        call2.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                userProfile.postValue(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                call.cancel();
            }
        });
        return userProfile;
    }

    public LiveData<ApiResponse> updateNote(String token, String userId, String note) {
        final MutableLiveData<ApiResponse> data = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.updateNote(token, userId, note);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
            }
        });
        return data;
    }

    public LiveData<ApiResponse> transferUser(String token, String userId, String seller_id) {
        final MutableLiveData<ApiResponse> data = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.transferUser(token,Config.APP_ID, userId, seller_id);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
            }
        });
        return data;
    }

    public LiveData<ApiResponse> cancelPurchase(String token, String sellId) {
        final MutableLiveData<ApiResponse> data = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.cancelPurchase(token, Config.APP_ID, sellId);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
            }
        });
        return data;
    }

    public LiveData<ApiResponse> acceptPurchase(String token, String sellId,String id) {
        final MutableLiveData<ApiResponse> data = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.acceptPurchase(token, Config.APP_ID, sellId,id);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
            }
        });
        return data;
    }

    public LiveData<ApiResponse> sellRequest(String token, String userId, String product_id, String game_id) {
        final MutableLiveData<ApiResponse> data = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.sellRequest(token, userId, product_id, game_id, Config.APP_ID);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
            }
        });
        return data;
    }

    public LiveData<ApiResponse> purchase(String token, String userId, String sellerId) {
        final MutableLiveData<ApiResponse> data = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.purchase(token, userId, sellerId);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
            }
        });
        return data;
    }

    public LiveData<ApiResponse> updatePoint(String token, String userId, String point) {
        final MutableLiveData<ApiResponse> data = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.updatePoint(token, userId, point);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
            }
        });
        return data;
    }

    public LiveData<ApiResponse> purchasePoint(String token, String userId, String point) {
        final MutableLiveData<ApiResponse> data = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.purchasePoint(token, userId, point);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
            }
        });
        return data;
    }

    public LiveData<ApiResponse> password_change(String token, String userId, String old_password, String new_password) {
        final MutableLiveData<ApiResponse> data = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.password_change(token, userId, old_password, new_password);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                data.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                call.cancel();
            }
        });
        return data;
    }

    public LiveData<JsonObject> getPosition(String token, String userId) {

        final MutableLiveData<JsonObject> liveData = new MutableLiveData<>();
        Call<JsonObject> call2 = apiInterface.waitingTime(token, userId, Config.APP_ID);
        call2.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                liveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
            }
        });
        return liveData;
    }

    public LiveData<JsonObject> getBadgeCount(String token, String userId) {

        final MutableLiveData<JsonObject> liveData = new MutableLiveData<>();
        Call<JsonObject> call2 = apiInterface.getBadgeCount(token, userId, Config.APP_ID);
        call2.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                liveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
            }
        });
        return liveData;
    }

    public LiveData<List<SellerList>> getSellerList(String token) {

        final MutableLiveData<List<SellerList>> liveData = new MutableLiveData<>();
        Call<List<SellerList>> call2 = apiInterface.getSellerList(token,Config.APP_ID);
        call2.enqueue(new Callback<List<SellerList>>() {
            @Override
            public void onResponse(Call<List<SellerList>> call, Response<List<SellerList>> response) {
                liveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<SellerList>> call, Throwable t) {
                call.cancel();
            }
        });
        return liveData;
    }

    public LiveData<JsonObject> getSeenCount(String token, String userId, String selle_id) {

        final MutableLiveData<JsonObject> liveData = new MutableLiveData<>();
        Call<JsonObject> call2 = apiInterface.getSeenCount(token, userId, selle_id, Config.APP_ID);
        call2.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                liveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
            }
        });
        return liveData;
    }

    public LiveData<JsonObject> getSellerContact(String token) {
        Call<JsonObject> call2 = apiInterface.getSellerContact(token, Config.APP_ID);
        call2.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                sellerContactLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
            }
        });
        return sellerContactLiveData;
    }
    public LiveData<JsonObject> getPurchaseCount(String token,String user_id) {
        final MutableLiveData<JsonObject> purchaseCountLiveData = new MutableLiveData<>();
        Call<JsonObject> call2 = apiInterface.getPurchaseCount(token, user_id);
        call2.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                purchaseCountLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
            }
        });
        return purchaseCountLiveData;
    }

    public LiveData<PartialsInfo> getPartialsInfo(String token, String userId) {

        final MutableLiveData<PartialsInfo> liveData = new MutableLiveData<>();
        Call<PartialsInfo> call2 = apiInterface.getPartialsInfo(token, userId);
        call2.enqueue(new Callback<PartialsInfo>() {
            @Override
            public void onResponse(Call<PartialsInfo> call, Response<PartialsInfo> response) {
                liveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<PartialsInfo> call, Throwable t) {
                call.cancel();
            }
        });
        return liveData;
    }


    private static class IsFavourite extends AsyncTask<List, Void, Void> {
        private final APIInterface apiInterface;

        private IsFavourite(APIInterface apiInterface) {
            this.apiInterface = apiInterface;
        }

        @Override
        protected Void doInBackground(List... lists) {
            Call<JsonObject> call = apiInterface.isFavourite(lists[0].get(0).toString(), lists[0].get(1).toString(), lists[0].get(2).toString());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
            return null;
        }
    }
}