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

public class UpdateAccountDetailsViewModel extends ViewModel
{
    private Context context;
    private NavController navController;
    private SessionManager sessionManager;
    private String newEmail;
    public final ObservableField<String> emailAddress = new ObservableField<>("");
    public final ObservableField<String> password = new ObservableField<>("");

    public UpdateAccountDetailsViewModel() {}


    public void setRequiredParameters(Context context, SessionManager sessionManager, NavController navController)
    {
        this.context = context;
        this.sessionManager = sessionManager;
        this.navController = navController;
    }


    public void sendUpdateAccountDetailsRequest(String oldEmail, String email, String password)
    {
        this.newEmail = email;

        if(password.isEmpty()) {
            password = "EMPTY_PASSWORD";
        }

        ApiService apiService = RetrofitService.getAPIService();
        Call<JsonObject> updateResponse = apiService.updateAccountDetails(oldEmail, email, password);
        updateResponse.enqueue(new Callback<JsonObject>() {
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


    public void sendDeleteAccountRequest(String email)
    {
        ApiService apiService = RetrofitService.getAPIService();
        Call<JsonObject> deleteResponse = apiService.deleteUser(email);
        deleteResponse.enqueue(new Callback<JsonObject>() {
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
            case "DELETE_ACCOUNT_SUCCESS":
                sessionManager.logoutUser();
                navController.navigate(R.id.action_updateAccountDetailsFragment_to_loginFragment);
                Toast.makeText(context, R.string.label_delete_account, Toast.LENGTH_LONG).show();
                break;
            case "UPDATE_USER_INFOS_SUCCESS":
                sessionManager.updateEmail(newEmail);
                Toast.makeText(context, R.string.label_update_account_update_success, Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, R.string.label_error, Toast.LENGTH_LONG).show();
        }
    }
}
