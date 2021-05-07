package fr.corentinbringer.endunav.viewmodels;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.ObservableField;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

import fr.corentinbringer.endunav.R;
import fr.corentinbringer.endunav.apis.ApiService;
import fr.corentinbringer.endunav.apis.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupViewModel extends Observable
{
    private Context context;
    public boolean userRegistrationCompleted;
    public final ObservableField<String> completeName = new ObservableField<>("");
    public final ObservableField<String> emailAddress = new ObservableField<>("");
    public final ObservableField<String> password = new ObservableField<>("");

    public SignupViewModel(Context context)
    {
        this.context = context;
        this.userRegistrationCompleted = false;
    }


    public void sendSignupRequest(String name, String email, String password)
    {
        ApiService apiService = RetrofitService.getAPIService();
        Call<JsonObject> signupResponse = apiService.createUser(name, email, password);
        signupResponse.enqueue(new Callback<JsonObject>() {
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
                Toast.makeText(context, R.string.label_signup_error_intpus_incomplete, Toast.LENGTH_LONG).show();
                break;
            case "INVALID_EMAIL_ADDRESS":
                Toast.makeText(context, R.string.label_signup_error_email_valid, Toast.LENGTH_LONG).show();
                break;
            case "INVALID_PASSWORD":
                Toast.makeText(context, R.string.label_signup_error_password_length, Toast.LENGTH_LONG).show();
                break;
            case "INVALID_NAME":
                Toast.makeText(context, R.string.label_signup_error_name_length, Toast.LENGTH_LONG).show();
                break;
            case "EMAIL_ALREADY_USED":
                Toast.makeText(context, R.string.label_signup_error_email_already_used, Toast.LENGTH_LONG).show();
                break;
            case "CREATE_ACCOUNT_SUCCESS":
                userRegistrationCompleted = true;
                Toast.makeText(context, R.string.label_signup_account_created, Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, R.string.label_error, Toast.LENGTH_LONG).show();
        }
    }
}
