package fr.corentinbringer.endunav.models;

import android.util.Patterns;

public class LoginUser
{
    private String strEmailAddress;
    private String strPassword;

    public LoginUser(String emailAddress, String password)
    {
        strEmailAddress = emailAddress;
        strPassword = password;
    }


    public String getStrEmailAddress()
    {
        return strEmailAddress;
    }


    public String getStrPassword()
    {
        return strPassword;
    }


    public boolean isEmailValid()
    {
        return Patterns.EMAIL_ADDRESS.matcher(getStrEmailAddress()).matches();
    }


    public boolean isPasswordLengthGreaterThan8()
    {
        return getStrPassword().length() >= 8;
    }
}
