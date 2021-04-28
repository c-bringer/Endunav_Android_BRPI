package fr.corentinbringer.endunav;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.auth0.android.jwt.JWT;

import java.util.HashMap;

class SessionManager
{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREFER_NAME = "EndunavUserPref";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";

    //Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context)
    {
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }


    //Create login session
    public void createUserLoginSession(String token, String name, String email)
    {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }


    //Check login session
    public boolean checkLogin() throws Exception
    {
        return !this.isUserLoggedIn();
    }


    //Check if user logged in and if token is expired
    public boolean isUserLoggedIn() throws Exception
    {
        JWT jwt = new JWT(sharedPreferences.getString(KEY_TOKEN, null));

        if(jwt.isExpired(0)) {
            editor.putBoolean(IS_USER_LOGIN, false);
            editor.commit();
        }

        return sharedPreferences.getBoolean(IS_USER_LOGIN, false);
    }


    //Get stored session data
    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_TOKEN, sharedPreferences.getString(KEY_TOKEN, null));
        user.put(KEY_NAME, sharedPreferences.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));

        return user;
    }


    //Clear session details
    public void logoutUser()
    {
        editor.clear();
        editor.commit();
    }
}
