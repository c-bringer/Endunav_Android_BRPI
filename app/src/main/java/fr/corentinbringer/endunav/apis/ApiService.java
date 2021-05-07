package fr.corentinbringer.endunav.apis;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService
{
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("api/v1/CreateUser.php")
    Call<JsonObject> createUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );
}
