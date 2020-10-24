package com.team15.webchat.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Login;
import com.team15.webchat.Model.User;

import java.util.List;
import java.util.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserRepository extends Observable {
    private APIInterface apiInterface;
    private MutableLiveData<List<User>> allNotes = new MutableLiveData<>();
    final MutableLiveData<User> userProfile = new MutableLiveData<>();
    private static UserRepository userRepository;

    public static UserRepository getInstance() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }
        return userRepository;
    }

    private UserRepository() {
        apiInterface = APIClient.getRetrofitInstance().create(APIInterface.class);
    }

//    public void update(Note note) {
//        new UpdateNoteAsyncTask(noteDao).execute(note);
//    }

    public LiveData<User> getUser(String token,String userId){

        Call<User> call2 = apiInterface.getUser(token,userId);
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


    public LiveData<Login> getLogin(String phone, String password){
        final MutableLiveData<Login> loginData = new MutableLiveData<>();
        Call<Login> call2 = apiInterface.getUserLogin(phone,password);
        call2.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                loginData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {

            }
        });
        return loginData;
    }

    public LiveData<ApiResponse> registration(String name,String phone,String password,String appId,String email){
        final MutableLiveData<ApiResponse> apiResponse = new MutableLiveData<>();
        Call<ApiResponse> call2 = apiInterface.registration(name,phone,password,appId,email);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                apiResponse.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
        return apiResponse;
    }

//    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {
//        private NoteDao noteDao;
//
//        private UpdateNoteAsyncTask(NoteDao noteDao) {
//            this.noteDao = noteDao;
//        }
//
//        @Override
//        protected Void doInBackground(Note... notes) {
//            noteDao.update(notes[0]);
//            return null;
//        }
//    }
}