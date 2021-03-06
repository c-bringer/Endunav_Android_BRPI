package fr.corentinbringer.endunav.apis;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
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


    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("api/v1/LoginUser.php")
    Call<JsonObject> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );


    @Headers({"Accept: application/json"})
    @GET("api/v1/GetUserInfo.php")
    Call<JsonObject> getUserDetails(
            @Header("Authorization") String token
    );


    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("api/v1/ResetPassword.php")
    Call<JsonObject> resetPassword(
            @Field("email") String email
    );


    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("api/v1/UpdateUserInfo.php")
    Call<JsonObject> updateAccountDetails(
            @Field("oldEmail") String oldEmail,
            @Field("email") String email,
            @Field("password") String password
    );


    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("api/v1/DeleteUser.php")
    Call<JsonObject> deleteUser(
            @Field("email") String email
    );
}
