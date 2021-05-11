package fr.corentinbringer.endunav.viewmodels;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import fr.corentinbringer.endunav.R;
import fr.corentinbringer.endunav.apis.ApiService;
import fr.corentinbringer.endunav.apis.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordViewModel extends ViewModel
{
    private Context context;
    public final ObservableField<String> emailAddress = new ObservableField<>("");

    public ForgetPasswordViewModel() {}


    public void setRequiredParameters(Context context)
    {
        this.context = context;
    }


    public void sendResetPasswordRequest(String email)
    {
        ApiService apiService = RetrofitService.getAPIService();
        Call<JsonObject> resetResponse = apiService.resetPassword(email);
        resetResponse.enqueue(new Callback<JsonObject>() {
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
            case "NO_ACCOUNT":
                Toast.makeText(context, R.string.label_error_no_account, Toast.LENGTH_LONG).show();
                break;
            case "NEW_PASSWORD_SEND":
                Toast.makeText(context, R.string.label_forget_password_new_password_send, Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, R.string.label_error, Toast.LENGTH_LONG).show();
        }
    }
}
