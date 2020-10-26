package com.team15.webchat.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.DeviceReg;
import com.team15.webchat.Model.Login;
import com.team15.webchat.Model.User;
import com.team15.webchat.Repositories.UserRepository;

public class UserViewModel extends ViewModel {
    private UserRepository repository;
    private LiveData<User> getUser;

    public UserViewModel() {
        super();
        repository = UserRepository.getInstance();

    }

    public LiveData<User> getUser(String token,String userId) {
        getUser = repository.getUser(token,userId);
        return getUser;
    }
    public LiveData<Login> getLogin(String phone, String password) {
        LiveData<Login> userLogin;
        userLogin = repository.getLogin(phone, password);
        return userLogin;
    }
    public LiveData<ApiResponse> registration(String name,String phone,String password,String appId,String email) {
        LiveData<ApiResponse> apiResponse;
        apiResponse = repository.registration(name,phone,password,appId,email);
        return apiResponse;
    }

    public LiveData<ApiResponse> profileUpdate(String token,String userId,String name,String phone,String email) {
        LiveData<ApiResponse> apiResponse;
        apiResponse = repository.updateProfile(token,userId,name,phone,email);
        return apiResponse;
    }

    public void updateDeviceId(DeviceReg deviceReg) {
        repository.updateDeviceId(deviceReg);
    }
}