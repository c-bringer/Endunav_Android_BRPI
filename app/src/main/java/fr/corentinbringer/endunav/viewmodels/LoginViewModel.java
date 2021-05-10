package fr.corentinbringer.endunav.viewmodels;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import fr.corentinbringer.endunav.R;
import fr.corentinbringer.endunav.apis.ApiService;
import fr.corentinbringer.endunav.apis.RetrofitService;
import fr.corentinbringer.endunav.sharedprefs.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel
{
    private Context context;
    private NavController navController;
    private SessionManager sessionManager;
    public String token;
    public final ObservableField<String> emailAddress = new ObservableField<>("");
    public final ObservableField<String> password = new ObservableField<>("");

    public LoginViewModel() { }


    public void setRequiredParameters(Context context, SessionManager sessionManager, NavController navController)
    {
        this.context = context;
        this.sessionManager = sessionManager;
        this.navController = navController;
    }


    public void sendLoginRequest(String email, String password)
    {
        ApiService apiService = RetrofitService.getAPIService();
        Call<JsonObject> loginResponse = apiService.loginUser(email, password);
        loginResponse.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response)
            {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        String jsonResponse = response.body().toString();
                        try {
                            parseData(jsonResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t)
            {
                Toast.makeText(context, R.string.label_error + " " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void sendGetUserDetailsRequest(String token)
    {
        ApiService apiService = RetrofitService.getAPIService();
        Call<JsonObject> detailResponse = apiService.getUserDetails("Bearer " + token);
        detailResponse.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response)
            {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        String jsonResponse = response.body().toString();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            int status = jsonObject.getInt("status");

                            if(status == 401) {
                                Toast.makeText(context, R.string.label_unauthorized, Toast.LENGTH_LONG).show();
                            } else {
                                JSONObject userInfos = new JSONObject(jsonObject.getString("user"));
                                String name = userInfos.getString("name");
                                String email = userInfos.getString("email");

                                sessionManager.createUserLoginSession(token, name, email);
                                navController.navigate(R.id.action_loginFragment_to_mapFragment);
                                Toast.makeText(context, R.string.label_connected, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t)
            {
                Toast.makeText(context, R.string.label_error + " " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void parseData(String response) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(response);
        String code = jsonObject.getString("code");

        switch(code) {
            case "ALL_INPUT_INCOMPLETE":
                Toast.makeText(context, R.string.label_error_intput_incomplete, Toast.LENGTH_LONG).show();
                break;
            case "INVALID_EMAIL_ADDRESS":
                Toast.makeText(context, R.string.label_error_email_valid, Toast.LENGTH_LONG).show();
                break;
            case "INVALID_PASSWORD":
                Toast.makeText(context, R.string.label_error_password_length, Toast.LENGTH_LONG).show();
                break;
            case "DISABLED_ACCOUNT":
                Toast.makeText(context, R.string.label_error_account_disabled, Toast.LENGTH_LONG).show();
                break;
            case "LOGIN_SUCCESS":
                token = jsonObject.getString("token");
                sendGetUserDetailsRequest(token);
                break;
            case "WRONG_PASSWORD":
                Toast.makeText(context, R.string.label_error_wrong_password, Toast.LENGTH_LONG).show();
                break;
            case "NO_ACCOUNT":
                Toast.makeText(context, R.string.label_error_no_account, Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, R.string.label_error, Toast.LENGTH_LONG).show();
        }
    }
}
