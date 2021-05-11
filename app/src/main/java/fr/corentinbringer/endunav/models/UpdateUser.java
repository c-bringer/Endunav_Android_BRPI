package fr.corentinbringer.endunav.models;

import android.util.Patterns;

public class UpdateUser
{
    private String strOldEmailAddress;
    private String strEmailAddress;
    private String strPassword;

    public UpdateUser(String oldEmailAddress, String emailAddress, String password)
    {
        this.strOldEmailAddress = oldEmailAddress;
        this.strEmailAddress = emailAddress;
        this.strPassword = password;
    }


    public String getStrOldEmailAddress()
    {
        return strOldEmailAddress;
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
